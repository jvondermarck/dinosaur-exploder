## Summary

Adds a manual gameplay testing report for issue 394.

The report documents:
- testing environment
- commands used
- manual gameplay test cases
- expected vs. actual results
- pass/fail status
- bug/observation notes

## Manual testing covered

- Game launch
- Player movement
- Shooting
- Shield
- Bomb
- Pause menu
- Enemy collision
- Game over behavior
- Coins/score
- Menus/settings
- Longer gameplay observations

## Findings

During longer manual testing, I noticed:

1. After the player dies, there is a short delay before the game-over menu appears. During that delay, the player can still briefly move/collide with objects.
2. During level transitions, dinos already on screen pause briefly before continuing.
3. One audio distortion issue happened once, but I was not able to reproduce it yet(I assume this was on my end, but I decided to still mention it here).

## Code Areas Worth Reviewing

These are not fixes. They are the main source areas that seemed related during manual findings documentation.

1. src/main/java/com/dinosaur/dinosaurexploder/controller/core/GameInitializer.java
    - initInput()
    - Input is mapped directly to PlayerComponent movement/shooting/shield actions.
    
2. src/main/java/com/dinosaur/dinosaurexploder/controller/core/GameActions.java
    - lives <= 0, startGameOverSequence(), pauseElement()
    - Game-over handling and level transition pause behavior are both handled here.

3. src/main/java/com/dinosaur/dinosaurexploder/controller/core/collisions/PlayerCoinCollision.java
    - onCollisionBegin(EntityType.PLAYER, EntityType.COIN, ...)
    - Coin collection still triggers from PLAYER and COIN collision.

4. src/main/java/com/dinosaur/dinosaurexploder/model/CollisionHandler.java
    - adjustLevel()
    - Level advancement appears to be based on the level manager condition, not on clearing every active enemy currently on screen.


## Notes

This PR does not change gameplay code. It adds a manual testing report so the results and observations from testing issue 394 are documented clearly.