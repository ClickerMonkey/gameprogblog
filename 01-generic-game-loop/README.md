Generic Game Loop
============

1. Summary
2. Objectives
3. Article
  1. What is the game loop?
  2. Variable and Fixed time step
  3. Defining Game, GameState, and GameInput
  4. GameLoop interface
  5. GameLoopVariable implementation
  6. GameLoopInterpolated implementation
  7. GameLoopFixed implementation
  8. Smooth drawing technique
4. Expanding
5. Code Download
6. Applet
7. Next Article

### Summary
Welcome to my first article! This article will discuss game loop theory and implementation. This should be valuable if you’re new to game programming, useful if you’re working on a game engine, and might be interesting even if you’re a seasoned programmer! I’ll touch on the common implementations, add functionality to them, and introduce new concepts! (I think?)

### Objectives
* To design a simple game engine with flexible game loop configuration.
* To have working code with various game loop implementations.
* To implement smooth drawing for fixed time-step game loops.


### Article
#### What is the game loop?
Simple! The game loop is the difference between programming a picture and a game. A game loop does three basic things: notify the game that there’s user input to handle, the game should update the state of it’s objects, and the game should draw those objects to the screen. Here’s an example:

```java
while (playing) {
  handleInput( inputEvents );
  handleUpdate( elapsedTime );
  handleDraw( graphics );
}
```

#### Variable and Fixed time step
This example is a simple and commonly used game loop implementation; It’s referred to as a variable time step game loop. What this means is, it progresses it’s game state through the update call based on the number of seconds that has elapsed since the last update call was made. To move an object’s position with this method would be as simple as:

```java
position = position + velocity * elapsedTime;
```

This is often sufficient for many games, but it has several drawbacks:

* There’s no guarantee how many times the update method will be called. If there’s a spot in the game that is very graphic or update intensive there will be a large delay between updates. These large delays can cause objects to move to quickly, potentially passing through objects they’re not supposed to.
* This method will run as quickly as possible, consuming as much of the processor as it possibly can.

So what alternative is there? Meet fixed time step. Fixed time step will ensure the update method is called X times per second, every second. This leads to more predictable behavior, and can simplify the update logic (no need for elapsedTime if it’s always the same!). A simple fixed time step game loop looks something like this:

