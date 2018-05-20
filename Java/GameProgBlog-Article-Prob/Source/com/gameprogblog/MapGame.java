
package com.gameprogblog;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import com.gameprogblog.engine.Game;
import com.gameprogblog.engine.GameLoop;
import com.gameprogblog.engine.GameLoopVariable;
import com.gameprogblog.engine.GameScreen;
import com.gameprogblog.engine.GameState;
import com.gameprogblog.engine.Scene;
import com.gameprogblog.engine.core.Bound2;
import com.gameprogblog.engine.core.Index2;
import com.gameprogblog.engine.core.Vector2;
import com.gameprogblog.engine.input.GameInput;
import com.gameprogblog.engine.noise.NoiseGenerator;
import com.gameprogblog.engine.noise.PerlinNoise;
import com.gameprogblog.engine.noise.SimplexNoise;
import com.gameprogblog.engine.prob.IterativeProbabilityMatrix;
import com.gameprogblog.engine.prob.ProbabilityMatrix;


public class MapGame implements Game
{

	public enum Biome 
	{
		/*0*/ Water, 
		/*1*/ Shore, 
		/*2*/ Grassland, 
		/*3*/ SandDune, 
		/*4*/ Hill, 
		/*5*/ Mountains, 
		/*6*/ Sand, 
		/*7*/ Plateau,
		/*8*/ Canyon, 
		/*9*/ Fields, 
		/*A*/ Woods, 
		/*B*/ Forest, 
		/*C*/ Rainforest
	}
	
	public ProbabilityMatrix<Biome, Biome> BiomeMatrix = new IterativeProbabilityMatrix<Biome, Biome>( Biome.class, Biome.class, new int[][] {
		/*      0  1  2  3  4  5  6  7  8  9  A  B  C  D */
		/*0*/ {99, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		/*1*/ { 1,99, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		/*2*/ { 1, 1,99, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		/*3*/ { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		/*4*/ { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		/*5*/ { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		/*6*/ { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		/*7*/ { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		/*8*/ { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		/*9*/ { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		/*A*/ { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		/*B*/ { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		/*C*/ { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		/*D*/ { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
	});
	
	public static void main( String[] args )
	{
		Game game = new MapGame();
		GameLoop loop = new GameLoopVariable( 0.1f );
//		GameLoop loop = new GameLoopInterpolated( 3, 20, TimeUnit.MILLISECONDS, false );
		GameScreen screen = new GameScreen( WIDTH, HEIGHT, ANTIALIASING, loop, game );
		screen.setBackground( Color.black );
		GameScreen.showWindow( screen, "GameProgBlog - Article 4 - Tile Map" );
	}

	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final int GRID_SIZE = 1;
	public static final boolean ANTIALIASING = false;

	public boolean playing;
	public Vector2 direction;
	public NoiseGenerator noise1;
	public NoiseGenerator noise2;
	public Map<Index2, Biome> biomes;
	
	public MapGame()
	{
	}

	@Override
	public void start( Scene scene )
	{
		noise1 = new NoiseGenerator( 0.005f, 0.005f, 0.0f, 1, 1.0, 10.0, false, PerlinNoise.getInstance() );
		noise2 = new NoiseGenerator( 0.001f, 0.001f, 0.0f, 1, 1.0, 10.0, false, SimplexNoise.getInstance() );
		
		biomes = new HashMap<Index2, Biome>();
		biomes.put( new Index2( 0, 0 ), Biome.Shore );
		
		direction = new Vector2();
		
		playing = true;
	}

	@Override
	public void input( GameInput input )
	{
		if (input.keyDown[KeyEvent.VK_ESCAPE])
		{
			playing = false;
		}
		
		direction.x = input.keyDown[KeyEvent.VK_LEFT] ? -1 : (input.keyDown[KeyEvent.VK_RIGHT] ? 1 : 0);
		direction.y = input.keyDown[KeyEvent.VK_UP] ? -1 : (input.keyDown[KeyEvent.VK_DOWN] ? 1 : 0);
	}

	@Override
	public void update( GameState state, Scene scene )
	{
//		Bound2 cameraBounds = scene.camera.bounds;
//
//		int landL = (int)Math.floor( cameraBounds.left / GRID_SIZE );
//		int landR = (int)Math.ceil( cameraBounds.right / GRID_SIZE );
//		int landT = (int)Math.floor( cameraBounds.top / GRID_SIZE );
//		int landB = (int)Math.ceil( cameraBounds.bottom / GRID_SIZE );
//		
//		Index2 i = new Index2();
//		
//		for (i.y = landT; i.y <= landB; i.y++)
//		{
//			for (i.x = landL; i.x <= landR; i.x++)
//			{
//				if (biomes.get( i ) == null)
//				{
//					biomes.put( new Index2( i ), determineBiome( i ) );
//				}
//			}
//		}
		
		scene.camera.center.add( direction, 300 * state.seconds );
	}
//	
//	private Biome determineBiome(Index2 i)
//	{
//		i.x--;
//		Biome l = biomes.get( i );
//		i.x++;
//		i.y++;
//		Biome b = biomes.get( i );
//		i.y--;
//		i.x++;
//		Biome r = biomes.get( i );
//		i.x--;
//		i.y--;
//		Biome t = biomes.get( i );
//		i.y++;
//		
//		int nonNull = 0;
//		
//		Biome[] neighbors = new Biome[4];
//		
//		if (l != null) neighbors[nonNull++] = l;
//		if (t != null) neighbors[nonNull++] = t;
//		if (r != null) neighbors[nonNull++] = r;
//		if (b != null) neighbors[nonNull++] = b;
//		
//		if (nonNull != 0)
//		{
//			return BiomeMatrix.random( Arrays.copyOf( neighbors, nonNull ) );
//		}
//		
//		return Biome.Water;
//	}

	public static final Color SEA_DEEP = new Color( 27, 17, 107 );
	public static final Color SEA_SHALLOW = new Color( 124, 113, 245 );
	public static final Color SAND = new Color( 227, 214, 177 );
	public static final Color GRASS = new Color( 46, 156, 26 );
	public static final Color ROCK = new Color( 55, 46, 47 );
	public static final Color SNOW = new Color( 235, 235, 240 );
	
	@Override
	public void draw( GameState state, final Graphics2D gr, final Scene scene )
	{
		Bound2 cameraBounds = scene.camera.bounds;

		int landL = (int)Math.floor( cameraBounds.left / GRID_SIZE );
		int landR = (int)Math.ceil( cameraBounds.right / GRID_SIZE );
		int landT = (int)Math.floor( cameraBounds.top / GRID_SIZE );
		int landB = (int)Math.ceil( cameraBounds.bottom / GRID_SIZE );
		
		Index2 i = new Index2();
		
		for (i.y = landT; i.y <= landB; i.y++)
		{
			for (i.x = landL; i.x <= landR; i.x++)
			{
				float a = noise1.noisef( 0.0f, 0.2f, i.x, i.y );
				float b = noise2.noisef( 0.0f, 0.8f, i.x, i.y );
				float c = a + b;
				
//				Biome b = biomes.get( i );
				
//				if ( b != null )
//				{
					Color bcolor = null;
					
					if (c < 0.5f) {
						bcolor = interpolate( SEA_DEEP, SEA_SHALLOW, delta(0.0f, 0.5f, c) );
					} else if (c < 0.55f) {
						bcolor = interpolate( SEA_SHALLOW, SAND, delta(0.5f, 0.55f, c) );
					} else if (c < 0.6f) {
						bcolor = SAND;
					} else if (c < 0.65f) {
						bcolor = interpolate( SAND, GRASS, delta( 0.6f, 0.65f, c) );
					} else if (c < 0.8f) {
						bcolor = GRASS;
					} else if (c < 0.85f) {
						bcolor = interpolate( GRASS, ROCK, delta( 0.8f, 0.85f, c) );
					} else {
						bcolor = interpolate( ROCK, SNOW, delta( 0.85f, 1.0f, c) );
					}
					
//					switch(b) {
//					case Water:
//						bcolor = new Color( 20, 175, 227 );
//						break;
//					case Shore:
//						bcolor = new Color( 235, 217, 164 );
//						break;
//					case Grassland:
//						bcolor = new Color( 29, 145, 39 );
//						break;
//					}
					
//					if (bcolor != null)
//					{
						gr.setColor( bcolor );
						gr.fillRect( i.x * GRID_SIZE, i.y * GRID_SIZE, GRID_SIZE, GRID_SIZE );
//					}
//				}
			}
		}
	}

	@Override
	public void destroy()
	{
	}

	@Override
	public boolean isPlaying()
	{
		return playing;
	}
	
	public static float delta(float min, float max, float d)
	{
		return (d - min) / (max - min);
	}
	
	public static Color interpolate(Color start, Color end, float d)
	{
		return new Color( 
			(start.getRed()   * (1 - d) + end.getRed()   * d) / 255f,
			(start.getGreen() * (1 - d) + end.getGreen() * d) / 255f,
			(start.getBlue()  * (1 - d) + end.getBlue()  * d) / 255f
		);
	}

}
