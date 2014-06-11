Managing Entities
============

1. Summary
2. Definitions
3. Objectives
4. Article
  1. Theory
  2. The Entity
  3. The EntityList
  4. Making EntityList extensible
  5. EntityLayer & EntityLayers
  6. Alternative Data Structures
5. Expanding
6. Code Download
7. Applet
8. Next Article


## Summary
Managing the vast number of objects in your game could be time consuming. Especially when you have short-lived objects in your game like particles, bullets, and animations. This article discusses a method which utilizes arrays to store Entities and storing Entities in layers.

## Definitions
* Entity – something that could be drawn, is updated, and can expire.

## Objectives
* To design a simple Entity list which handles updating, drawing, and pruning expired entities.
* To design a class for storing layers of entities to control drawing and update order.

## Article

#### Theory
The idea here is to build a simple data structure that handles drawing, updating, adding new entities, and removing old entities. Using the built in data structures of whatever language you use could be fine, but there is always room for improvement. When you start dealing with thousands to hundreds of thousands of objects, how you manage updating, drawing, adding, and removing is very important.

This article presents a method that uses an array that prunes out expired entities while it’s updating entities.

The positives of using a self-pruning array:
* No copying (or iteration) needs to be done to remove an entity, it’s removed when the list is being updated and realizes one of it’s entities have expired.
* Arrays are more efficient to iterate over (opposed to linked structures), the processor is tuned to handle arrays better (with caching and prefetching to mention a few)
* Arrays are simpler to understand than linked structures.
* Insertion order is maintained

The negatives:
* An array can take up more memory than a singly linked list (however, you can add in simple logic to an array to say “over x seconds, if x% of the array is unused, shrink the array” – if memory is limited)
* A developer might unknowningly use an expired entity, it’s not removed from the list immediately upon expiration (easily fixed by checking the expiration flag, or ordering when updates are made of certain things)
* Must be resized if the underlying array is not big enough (however, a game could allocate it’s maximum amount of space required. This methodology ensures more controlled memory usage and avoids unnecessary calls)
* As you can see the positives outweigh the negatives greatly (the negatives are situational).

At the end of the article I go over the alternative structures I’ve used aside from the self-pruning array.

#### The Entity

The Entity interface is the core to any game or game engine. We might as well go straight to the code, it’s simple enough based on our objectives.

```java
public interface Entity {
  public void draw( GameState state, Graphics2D gr );
  public void update( GameState state );
  public boolean isExpired();
  public void expire();
  public void onExpire();
}
```

Lets break it down:

```java
public void draw( GameState state, Graphics2D gr );
```
*Draw* needs to be called every frame, we pass in the frame information (GameState) and the graphics used to draw then Entity.

```java
public void update( GameState state );
```
*Update* will be called whenever the game is ready to progress it’s game state to the next step. This is dependent on the game loop implementation you are using.

```java
public boolean isExpired();
```
Whether or not this Entity is expired and is ready to be removed from the game. This should return true if expire() was called, or if the implementing Entity is ready to go.

```java
public void expire();
```
Tells the Entity implementation that it needs to be removed from the game, whether it wants to or not. The implementation needs to assume that this method could be called many times over a single update, and needs to be coded accordingly.

```java
public void onExpire();
```
This method is called once by the EntityList when it is being pruned. This is essentially the deconstructor for an Entity.

#### The EntityList

The EntityList is the data structure which manages the array of Entites and prunes out the expired ones. Here’s what the initial version of this class looks like:

```java
public class EntityList implements Entity
{
  public EntityList()
  public EntityList( int initialCapacity )
  public EntityList( Entity[] entities )
  public void draw( GameState state, Graphics2D gr )
  public void update( GameState state )
  public boolean isExpired()
  public void expire()
  public void onExpire()
  public int size()
  public int capacity()
  public int available()
  public void pad( int count )
  public void add(Entity[] entityArray, int offset, int length)
  public void add(Entity[] entityArray)
  public void add(EntityList entityList)
  public void add(Entity entity)
}
```

As you can see we made EntityList implement Entity! This means we could have an EntityList within another EntityList within another EntityList. EntityList inception to the third degree one might say.

EntityList is also given typical collection methods like add() and size(). Note that there is no remove, that auto-magically happens when an entity expires.

You could also make this an interface, so you could provide a specific implementation depending on the scenario.

No onto the implementation! Firstly, the class level variables:

```java
protected E[] entities;
protected int size;
protected boolean expired;
```

Now method implementations, with the least complex first:

*Constructors*
```java
public EntityList() {
  this( DEFAULT_CAPACITY );
}
public EntityList( int initialCapacity ) {
  this( new Entity[ initialCapacity ] );
}
public EntityList( Entity[] entities ) {
  this.entities = entities;
}
```

*State*
```java
public int size() {
  return size;
}
public int capacity() {
  return entities.length;
}
public int available() {
  return (entities.length - size);
}
```

*Utility Method*
```java
public void pad( int count ) {
  final int capacity = entities.length;
  if ( size + count >= capacity ) {
    int nextSize = capacity + (capacity >> 1);
    int minimumSize = size + count;
    entities = Arrays.copyOf( entities, Math.max( nextSize, minimumSize ) );
  }
}
```
The pad method grows the capacity of the array by 50% (unless that’s not enough to add count entities)

*Add Methods*
```java
public void add(Entity entity) {
  pad( 1 );
  entities[size++] = entity;
}
public void add(Entity[] entityArray, int offset, int length) {
  pad( length );
  System.arraycopy( entityArray, offset, entities, size, length );
  size += length;
}
public void add(Entity[] entityArray) {
  add( entityArray, 0, entityArray.length );
}
public void add(EntityList entityList) {
  add( entityList.entities, 0, entityList.size );
}
```

*Expiration Methods*
```java
public boolean isExpired() {
  return expired;
}
public void expire() {
  expired = true;
}
public void onExpire() {
  for ( int i = 0; i < size; i++ ) {
    entities[i].onExpire();
    entities[i] = null;
  }
  size = 0;
}
```

*Draw Method*
```java
public void draw( GameState state, Graphics2D gr ) {
  for ( int i = 0; i < size; i++ ) {
    entities[i].draw( state, gr );
  }
}
```

*Update Method*

Finally, the code you’ve all been waiting for!

```java
public void update( GameState state ) {
  int alive = 0;
  for ( int i = 0; i < size; i++ ) {
    E entity = entities[i];
    // Don’t want to update an expired entity! This check could be removed if you don’t care.
    if ( !entity.isExpired() ) {
      entity.update( state );
    }
    if ( entity.isExpired() ) {
      // Call the “deconstructor” for the entity
      entity.onExpire();
    }
    else {
      // Keep the live entity in the array. As entities expire they leave gaps, the
      // live entities will be copied back to take their space.
      entities[alive++] = entity;
    }
  }
  // Clear the spaces between the last live entity and the previous size of the list so
  // we don’t have any dangling references to objects the GC might want to collect.
  while ( size > alive ) {
    entities[--size] = null;
  }
}
```
Given the comments, it should be easy to follow. Now that we have the basic implementation, there’s more we can do with this.

#### Making EntityList extensible

We can make the EntityList a little more extensible. Here’s functionality I typically add to my EntityList, most of you could benefit from it as well.

* Make the entity list generic (EntityList<T>) so you can optionally specify an Entity implementation (ex: Particle) and anything using the EntityList doesn’t have to cast it. If you want an EntityList with any implementation of Entity, just create a EntityList<Entity>.
* Add a getter to the EntityList: given an index return an entity.
* Add a clear method
* Add protected methods that are called during certain events, so a class could extend EntityList and be notified of those events
  * `onUpdate( Entity, GameState, int )` – notify extending class when an entity was updated
  * `onExpired( Entity )` – notify extending class when an entity has expired or removed
  * `onAdd( Entity, int )` – notify extending class when an entity was added to the list
* Add an Iterator to iterate over the entities in this list. This is typically frowned upon in Java games (it creates a lot of garbage, trust me). Typically an Iterable class returns a new Iterator each time, we’re going to cache it and reuse an iterator.
* Shrink the array if there’s x% percent free for y seconds.

Instead of stepping through how to do these things, I’m just going to dump the code here. It should be easy enough to follow.

```java
public class EntityList<E extends Entity> implements Entity, Iterable<E> {
  public static int DEFAULT_CAPACITY = 16;
  protected E[] entities;
  protected int size;
  protected boolean expired;
  protected float shrinkPercent; 
  protected float shrinkReadyTime;
  protected float shrinkTime;
  protected EntityListIterator iterator = new EntityListIterator();
 
  public EntityList() {
    this( DEFAULT_CAPACITY );
  }
  public EntityList( int initialCapacity ) {
    this( (E[]) new Entity[ initialCapacity ] );
  }
  public EntityList( E[] entities ) {
    this.entities = entities;
  }
 
  protected void onUpdated( E entity, GameState state, int index ) {}
  protected void onExpired( E entity ) {}
  protected void onAdd( E entity, int index ) {}
 
  public void draw( GameState state, Graphics2D gr ) {
    for ( int i = 0; i < size; i++ ) {
      entities[i].draw( state, gr );
    }
  }
 
  public void update( GameState state ) {
    int alive = 0;
    for ( int i = 0; i < size; i++ ) {
      E entity = entities[i];
      // Don’t want to update an expired entity! This check could be removed if you
      // don’t care.
      if ( !entity.isExpired() ) {
        entity.update( state );
      }
      if ( entity.isExpired() ) {
        // Call the “deconstructor” for the entity
        entity.onExpire();
        // Notify extending class that entity has expired
        onExpired( entity );
      }
      else {
        // Keep the live entity in the array. As entities expire they leave
        // gaps, the live entities will be copied back to take their space.
        entities[alive] = entity;
        // Notify extending class that an entity has been updated.
        onUpdated( entity, state, alive );
 
        alive++;
      }
    }
 
    // Clear the spaces between the last live entity and the previous size of
    // the list so we don’t have any dangling references to objects the GC
    // might want to collect.
    while ( size > alive ) {
      entities[--size] = null;
    }
 
    handleShrinking( state );
  }
 
  private void handleShrinking( GameState state ) {
    // If the number of live entities has been below "shrinkPercent" relative
    // the the length of the array, and it's been below that percent for
    // "shrinkReadyTime" in seconds: Shrink the array!
    int shrinkLength = (int)(shrinkPercent * entities.length);
    if ( size <= shrinkLength ) {
      shrinkTime += state.seconds;
      if ( shrinkTime >= shrinkReadyTime ) {
        entities = Arrays.copyOf( entities, shrinkLength );
        shrinkTime = 0;
      }
    }
    else {
      shrinkTime = 0;
    }
  }
 
  public void setAutoShrink( float shrinkPercent, float shrinkReadyTime ) {
    this.shrinkPercent = shrinkPercent;
    this.shrinkReadyTime = shrinkReadyTime;
  }
 
  public boolean isExpired() {
    return expired;
  }
  public void expire() {
    expired = true;
  }
  public void onExpire() {
    for ( int i = 0; i < size; i++ ) {
      E entity = entities[i];
      // Notify entity, extending class, and clear from array.
      entity.onExpire();
      onExpired( entity );
      entities[i] = null;
    }
    size = 0;
  }
 
  public void clear() {
    // Simple as calling onExpire!
    onExpire();
  }
  public int size() {
    return size;
  }
  public int capacity() {
    return entities.length;
  }
  public int available() {
    return (entities.length - size);
  }
  public void pad( int count ) {
    final int capacity = entities.length;
    if ( size + count >= capacity ) {
      int nextSize = capacity + (capacity >> 1);
      int minimumSize = size + count;
      entities = Arrays.copyOf( entities, Math.max( nextSize, minimumSize ) );
    }
  }
  public <T extends E> void add(T[] entityArray, int offset, int length) {
    pad( length );
    System.arraycopy( entityArray, offset, entities, size, length );
    for ( int i = 0; i < length; i++ ) {
      onAdd( entityArray[offset + i], size + i );
    }
    size += length;
  }
  public <T extends E> void add(T[] entityArray) {
    add( entityArray, 0, entityArray.length );
  }
  public <T extends E> void add(EntityList<T> entityList) {
    add( entityList.entities, 0, entityList.size );
  }
  public void add(E entity) {
    pad( 1 );
   entities[size] = entity;
    onAdd( entity, size );
    size++;
  }
  public E get( int index ) {
    return entities[ index ];
  }
  public Iterator<E> iterator() {
    // If the cached iterator is currently being used, but another is
    // required - create a new one. This will create garbage that needs to be
    // collected so avoid looping over this list while the current iterator
    // has not finished.
    return ( iterator.hasNext() ? new EntityListIterator() : iterator.reset() );
  }
  private class EntityListIterator implements Iterator<E> {
    public int index;
    public EntityListIterator reset() {
      index = 0;
      return this;
    }
    public boolean hasNext() {
      return (index < size);
    }
    public E next() {
      return entities[index++];
    }
    public void remove() {
      entities[index - 1].expire();
    }
  }
}
```
(checkout the source code for full documentation)

#### EntityLayer and EntityLayers

But wait, there’s more!

Lets organize our Entities into layers! The EntityList already maintains insertion order, but sometimes we want certain types of things updated before other types of things. Sometimes we want things drawn in the background before the things drawn in the foreground!

EntityLayer is just an EntityList that has the following additional properties:
* index – the index of the layer
* visible – whether or not this entire layer is drawn
* enabled – whether or not this entire layer is updated

The implementation is easy enough:

```java
public class EntityLayer extends EntityList<Entity> {
  private final int index;
  private boolean visible;
  private boolean enabled;
 
  public EntityLayer( int index ) {
    this.index = index;
  }
  @Override
  public void draw( GameState state, Graphics2D gr ) {
    if ( visible ) {
      super.draw( state, gr );
    }
  }
  @Override
  public void update( GameState state ) {
    if ( enabled ) {
      super.update( state );
    }
  }
  public boolean isVisible() {
    return visible;
  }
  public void setVisible( boolean visible ) {
    this.visible = visible;
  }
  public boolean isEnabled() {
    return enabled;
  }
  public void setEnabled( boolean enabled ) {
    this.enabled = enabled;
  }
  public int getIndex() {
    return index;
  }
}
```

That was easy, let’s finish it off with EntityLayers. Note the s. You can rename this if it ends up being confusing to you.

```java
public class EntityLayers implements Entity {
  private final EntityLayer[] layers;
  private boolean expired;
 
  public <E extends Enum<E>> EntityLayers( Class<E> layerEnum ) {
    this( layerEnum.getEnumConstants().length );
  }
  public EntityLayers( int layerCount ) {
    layers = new EntityLayer[ layerCount ];
    for ( int i = 0; i < layerCount; i++ ) {
      layers[i] = new EntityLayer( i );
    }
  }
  @Override
  public void draw( GameState state, Graphics2D gr ) {
    for ( int i = 0; i < layers.length; i++ ) {
      layers[i].draw( state, gr );
    }
  }
  @Override
  public void update( GameState state ) {
    for ( int i = 0; i < layers.length; i++ ) {
      layers[i].update( state );
    }
  }
  @Override
  public boolean isExpired() {
    return expired;
  }
  @Override
  public void expire() {
    for ( int i = 0; i < layers.length; i++ ) {
      layers[i].expire();
    }
    expired = true;
  }
  @Override
  public void onExpire() {
    for ( int i = 0; i < layers.length; i++ ) {
      layers[i].onExpire();
    }
  }
  public void add( int index, Entity e ) {
    layers[ index ].add( e );
  }
  public void add( Enum<?> index, Entity e ) {
    layers[ index.ordinal() ].add( e );
  }
  public EntityLayer getLayer( int index ) {
    return layers[ index ];
  }
  public EntityLayer getLayer( Enum<?> index ) {
    return layers[ index.ordinal() ];
  }
}
```

With EntityLayers you can use an enum for the layer ordering. You could pass in LayerEnum.class to the constructor of EntityLayers and there will be as many layers created as there are constants in LayerEnum. When you do that, you can optionally use the add and getLayer methods and pass in a LayerEnum constant like so:

```java
layers.add( LayerEnum.Clouds, cloudEntity );
EntityLayer clouds = layers.getLayer( LayerEnum.Clouds );
```

I hope you realize the usefulness of organizing your world into EntityLayers!

#### Alternative Data Structures

Here are a few data structures commonly used by me in different scenarios. I’ve included notable `+` positives, `-` negatives, and `~` neutral remarks for each one.

**Singly Linked List** (a typical LinkedList implementation like java.util.LinkedList)
* `-` Removal involves iterating over the list until the entity is found.
* `-` Not as processor friendly as an array-based structure.
* `-` Requires constant allocation of nodes for every entity added (could use a node cache).
* `+` Uses only required space.

**Self-Pruning Linked List**
* `-` Not as processor friendly as an array-based structure.
* `-` Requires constant allocation of nodes for every entity added (although could use a node cache).
* `+` Uses only required space.
* `+` Removal is done during iteration, like our EntityList.

**Doubly Linked List**
* `-` Not as processor friendly as an array-based structure.
* `-` Requires constant allocation of nodes for every entity added (although could use a node cache).
* `-` Uses twice required space (since another node reference is required).
* `+` Removal can be done as soon as expire() is called, no need for pruning.
* `~` I use this for index’s in spatial databases. A spatial entity has a single doubly linked node which it uses throughout it’s life. The index’s of the spatial database each are a linked list consisting of the nodes of the entities in that index (and index may be a rectangle in space where it’s entities are entities in the rectangle).

**Self-Pruning Binary Tree** (something I’ve used for implementing a Scene Graph)
* `-` Not as processor friendly as an array-based structure.
* `-` Requires constant allocation of nodes for every entity added (although could use a node cache).
* `-` Uses twice required space (since another node reference is required).
* `+` Has layering built in.
* `+` Great for Scene Graph implementation.
* `~` Insertion is trickier since layer’s are involved. Typically insertion isn’t done on a tree in the same sense. The tree is built in such a way that insertion doesn’t require traversing the tree and finding the appropriate node.
* `~` The “left” node is the sibling to the current node. The “right” node is the first child node. When updating and drawing you iterate over the children and call draw/update. They do the same thing for.

**Array List** (a typical List implementation like java.util.ArrayList)
* `-` Removal involves iterating over the list until the entity is found.
* `-` May have wasted space in the array.
* `+` More processor friendly than a linked structure.

**Self-Pruning List** (hey, that’s what we just implemented! I wanted to re-iterate it)
* `-` May have wasted space in the array.
* `+` More processor friendly than a linked structure.

**Back-Copying List**
* `-` May have wasted space in the array.
* `-` Does not preserve insertion order.
* `+` More processor friendly than a linked structure.
* `+` Less copying than a Self-Pruning List.
* `~` Instead of copying the live entities over the expired ones (like Self-Pruning List), the last entity in the list is copied over the recently expired entity. This results in far less copying but insertion order is not maintained. This is easily implemented by iterating over the entities starting from the back and moving towards the front.

## Expanding

So, what more could you possibly want to do?

- Create an EntityContainer class that has similar method signatures to EntityList.
- Create the most useful implementations of the above mentioned Alternative Data Structures.
- Set a maximum capacity on the EntityList. add(Entity) will return boolean, and the other add methods would return the number of entities able to be added to the list. This is useful if you utilize the EntityList as a list of particles and you don’t want more than X particles on the screen at once.
- Add time modifiers to EntityLayer so time moves slower or faster for everything in that layer.
- Add Iterator to EntityLayers so you can iterate over all the entities in each layer if need be.
- Add more methods to EntityLayers for managing the state of it’s layers.

I hope this article helps you out in some fashion, even if you aren’t making a game from scratch.

## Code Download

The code was written in Java and uses Java2D for drawing. Java2D was chosen because it is part of the Java SDK and it doesn’t require any external libraries. This code is easily translatable to other languages and other graphics libraries.

[Source Code](download/GameProgBlog-Article-Entities.zip)

## Applet

[Applet](download/entity.jar)

## Next Article

The next article I’ll be talking about something far less involved, but useful nonetheless: Probability! When you have 4 items, and each one of them have an x% chance of occuring, how do you quickly pick one at random based on the probability?
