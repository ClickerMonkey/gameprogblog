
package com.gameprogblog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.TimeUnit;

import com.gameprogblog.engine.Camera;
import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopInterpolated;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.core.Vector2;
import com.gameprogblog.engine.input.GameInput;
import com.gameprogblog.engine.map.Map;
import com.gameprogblog.engine.map.MapEntity;
import com.gameprogblog.engine.map.MapQueryArray;


public class ColorGame implements Game
{

	public static final int GROUP_ENTITY = 1 << 0;
	public static final int GROUP_PARTICLE = 1 << 1;
	public static final int GROUP_BUTTONS = 1 << 6;

	public static final int COLOR_GREEN = 1 << 2;
	public static final int COLOR_RED = 1 << 3;
	public static final int COLOR_BLUE = 1 << 4;
	public static final int COLOR_YELLOW = 1 << 5;
	public static final int COLOR_WHITE = COLOR_GREEN | COLOR_RED | COLOR_BLUE | COLOR_YELLOW;
	
	public static Color getColor(long groups)
	{
		int g = (int)(groups & ColorGame.COLOR_WHITE);
		
		switch (g) {
		case ColorGame.COLOR_WHITE:		return Color.white;
		case ColorGame.COLOR_RED:		return Color.red;
		case ColorGame.COLOR_BLUE:		return Color.blue;
		case ColorGame.COLOR_GREEN:		return Color.green;
		case ColorGame.COLOR_YELLOW:	return Color.yellow;
		}
		
		return Color.gray;
	}
	
	public static ColorGame get;
	
	public static void main( String[] args )
	{
		Game game = new ColorGame();
		GameLoop loop = new GameLoopInterpolated( 3, 20, TimeUnit.MILLISECONDS, false );
		GameScreen screen = new GameScreen( WIDTH, HEIGHT, ANTIALIASING, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "GameProgBlog - Article 4 - Tile Map" );
	}

	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final boolean ANTIALIASING = false;

	public boolean playing;
	public Map map;
	public Player player;
	public Vector2 mouse = new Vector2();
	public float direction = 1.0f;
	public boolean shooting = false;
	public float reloadTime = 0.1f;
	public float time = 0.0f;
	public boolean querying = false;
	public Spring spring;
	public int selected;
	public boolean commandPlace = false;
	public boolean commandTake = false;
	
	public ColorGame()
	{
		get = this;
	}

	@Override
	public void start( Scene scene )
	{
		final Camera cam = scene.camera;

		cam.containedInWorld = true;
		cam.world.set( 0, -520, 1000, 1280 );

		player = new Player( 100, 100 );
		player.acceleration.y = 1000.0f;
		player.terminal = 400.0f;
		player.friction = 0.2f;
		player.restitution = 0.0f;
		player.jumpTimeMax = 0.5f;
		player.jumpVelocity = -300f;
		player.kickOffForce.x = 200.0f;
		player.kickOffForce.y = 400.0f;
		player.slidingFactor = 0.5f;
		player.sideAcceleration = 900.0f;
		player.climbSpeed = -400.0f;
		player.canSlide = true;
		player.canKickOff = true;
		player.canClimb = false;
		player.groups = COLOR_RED;

		spring = new Spring( player.position, new Vector2(), player.velocity, 100, 2f, -75f );

		map = new Map();
		map.set( 32, 32, 0, 0 );
		map.setData( new int[][] {
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{11,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{12,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{13,8,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{14,9,0,0,0,0,5,5,3,3,3,0,1,1,0,5,2,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{15,10,0,0,0,0,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
			{5,5,5,5,5,5,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
		} );
		map.setTiles(
			/* 0 */new ColorTile( 0, null ),
			/* 1 */new ColorTile( COLOR_GREEN, Color.green ),
			/* 2 */new ColorTile( COLOR_YELLOW, Color.yellow ),
			/* 3 */new ColorTile( COLOR_RED, Color.red ),
			/* 4 */new ColorTile( COLOR_BLUE, Color.blue ),
			/* 5 */new ColorTile( COLOR_WHITE, Color.white ),
			/* 6 */new ButtonTile( COLOR_GREEN, new Bound2( 0.3f, 0.8f, 0.7f, 1.0f ) ),
			/* 7 */new ButtonTile( COLOR_YELLOW, new Bound2( 0.3f, 0.8f, 0.7f, 1.0f ) ),
			/* 8 */new ButtonTile( COLOR_RED, new Bound2( 0.3f, 0.8f, 0.7f, 1.0f ) ),
			/* 9 */new ButtonTile( COLOR_BLUE, new Bound2( 0.3f, 0.8f, 0.7f, 1.0f ) ),
			/*10 */new ButtonTile( COLOR_WHITE, new Bound2( 0.3f, 0.8f, 0.7f, 1.0f ) ),
			/*11 */new BouncyTile( COLOR_GREEN, Color.green ),
			/*12 */new BouncyTile( COLOR_YELLOW, Color.yellow ),
			/*13 */new BouncyTile( COLOR_RED, Color.red ),
			/*14 */new BouncyTile( COLOR_BLUE, Color.blue ),
			/*15 */new BouncyTile( COLOR_WHITE, Color.white )
		);
		map.add( player );

		playing = true;
	}

	@Override
	public void input( GameInput input )
	{
		if (input.keyDown[KeyEvent.VK_ESCAPE])
		{
			playing = false;
		}

		if (input.keyDown[KeyEvent.VK_LEFT])
		{
			player.input.x = -1;
			direction = -1;
		}
		else if (input.keyDown[KeyEvent.VK_RIGHT])
		{
			player.input.x = 1;
			direction = 1;
		}
		else
		{
			player.input.x = 0;
		}
		
		if (input.keyUp[KeyEvent.VK_R])
		{
			player.position.set( 100, 100 );
			player.velocity.set( 0, 0 );
		}

		if (input.keyDown[KeyEvent.VK_SPACE])
		{
			player.input.y = 1.0f;
		}
		else
		{
			player.input.y = 0.0f;
		}

		if (input.keyUp[KeyEvent.VK_1])
		{
			player.canClimb = !player.canClimb;
			System.out.println( "Climb ability toggled" );
		}

		if (input.keyUp[KeyEvent.VK_2])
		{
			player.canKickOff = !player.canKickOff;
			System.out.println( "KickOff ability toggled" );
		}

		if (input.keyUp[KeyEvent.VK_3])
		{
			player.canSlide = !player.canSlide;
			System.out.println( "Slide ability toggled" );
		}
		
		commandPlace = input.keyUp[KeyEvent.VK_V];
		commandTake = input.keyDown[KeyEvent.VK_C];
		
		shooting = input.keyDown[KeyEvent.VK_S];
		querying = input.keyDown[KeyEvent.VK_Q];
		spring.enabled = input.keyDown[KeyEvent.VK_P];

		mouse.x = input.mouseX;
		mouse.y = input.mouseY;
	}

	@Override
	public void update( GameState state, Scene scene )
	{
		time += state.seconds;

		if (shooting)
		{
			if (time >= reloadTime)
			{
				time = 0.0f;

				Ball p = new Ball( player.bounds.cx(), player.bounds.cy(), 5, 10.0f, player.groups );
				p.velocity.x = direction * 400.0f + player.velocity.x;
				p.velocity.y = -100.0f + player.velocity.y;
				p.acceleration.y = 1000.0f;
				p.terminal = 400.0f + player.terminal;
				p.restitution = 0.5f;
				p.friction = 0.2f;

				map.add( p );
			}
		}

		scene.getWorldCoordinate( mouse, spring.rest );
		
		if (commandTake)
		{
			Vector2 v = scene.getWorldCoordinate( mouse, new Vector2() );
			int tileX = map.getX( v.x, true );
			int tileY = map.getY( v.y, true );
			
			if (!map.isOutside( tileX, tileY ))
			{
				selected = map.data[tileY][tileX];
			}
		}
		else if (commandPlace)
		{
			Vector2 v = scene.getWorldCoordinate( mouse, new Vector2() );
			int tileX = map.getX( v.x, true );
			int tileY = map.getY( v.y, true );
			
			if (!map.isOutside( tileX, tileY ))
			{
				map.data[tileY][tileX] = selected;
			}
		}
		
		spring.update( state, scene );

		map.update( state, scene );

		if (player.node.isOutside())
		{
			player.position.set( 100, 100 );
			player.velocity.set( 0, 0 );
		}
		
		scene.camera.center.set( player.position );
	}

	@Override
	public void draw( GameState state, final Graphics2D gr, final Scene scene )
	{
		map.draw( state, gr, scene );

		if (querying)
		{
			final float QUERY_RADIUS = 30.0f;
			final float QUERY_BUFFER = 64.0f;
			final long QUERY_GROUPS = GROUP_ENTITY | GROUP_PARTICLE;
			final int QUERY_MAX = 32;

			Bound2 query = new Bound2();
			query.left = mouse.x - QUERY_RADIUS;
			query.right = mouse.x + QUERY_RADIUS;
			query.top = mouse.y - QUERY_RADIUS;
			query.bottom = mouse.y + QUERY_RADIUS;

			MapQueryArray found = new MapQueryArray( QUERY_MAX );

			map.query( null, query, QUERY_BUFFER, QUERY_GROUPS, QUERY_MAX, found );

			for (MapEntity e : found)
			{
				drawOutline( gr, e.getBounds(), Color.white );
			}

			drawOutline( gr, query, Color.red );

			gr.setColor( Color.white );
			gr.drawString( "found: " + found.total, 10, 26 );
		}
	}

	private void drawOutline( Graphics2D gr, Bound2 b, Color c )
	{
		gr.setColor( c );
		gr.setStroke( new BasicStroke( 1.0f ) );

		gr.draw( new Rectangle2D.Float(
			b.left, b.top, b.getWidth(), b.getHeight()
		) );
	}

	@Override
	public void destroy()
	{
		map.onExpire();
	}

	@Override
	public boolean isPlaying()
	{
		return playing;
	}

	// Collision for two non-static entities of the same mass
	public static void handleCollision( MapEntity a, MapEntity b )
	{
		float mass0 = a.getMass();
		float mass1 = b.getMass();

		if (mass0 != 0.0f && mass1 != 0.0f)
		{
			Vector2 vel0 = a.getVelocity();
			Vector2 vel1 = b.getVelocity();

			float massFactor0 = (mass0 == 0.0f ? 0.0f : mass1 / mass0);
			float massFactor1 = (mass1 == 0.0f ? 0.0f : mass0 / mass1);

//			float restitution = a.getRestitution() + b.getRestitution();

			float speed0 = vel0.norm();
			float speed1 = vel1.norm();

			Vector2 towards = new Vector2();
			towards.set( a.getPosition() );
			towards.sub( b.getPosition() );
			towards.norm();

			vel0.set( towards );
			vel0.scale( speed1 );
			vel0.scale( massFactor0 );
//			vel0.scale( restitution );

			vel1.set( towards );
			vel1.scale( -speed0 );
			vel1.scale( massFactor1 );
//			vel1.scale( restitution );
		}
	}

}
