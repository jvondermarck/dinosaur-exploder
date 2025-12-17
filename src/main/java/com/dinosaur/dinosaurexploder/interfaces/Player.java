package com.dinosaur.dinosaurexploder.interfaces;

/** Summary : This interface is to implement in new playerClass for clean Understanding */
public interface Player {
  /** Summary : This method limit the upSide movement. */
  void moveUp();

  /** Summary : This method limit the downSide movement. */
  void moveDown();

  /** Summary : This method limit the rightSide movement. */
  void moveRight();

  /** Summary : This method limit the leftSide movement. */
  void moveLeft();

  /** Summary : This handles with the shooting from the player and spawning of the new bullet */
  void shoot();
}
