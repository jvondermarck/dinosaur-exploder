package com.dinosaur.dinosaurexploder.model;
/**
 * Summary :
 *      This interface is to implement in  new playerClass for clean Understanding
 */
public interface Player {
    /**
     * Summary :
     *      This method limit the upSide movement.
     */
    public void moveUp();
    /**
     * Summary :
     *      This method limit the downSide movement.
     */
    public void moveDown();
    /**
     * Summary :
     *      This method limit the rightSide movement.
     */
    public void moveRight();
    /**
     * Summary :
     *      This method limit the leftSide movement.
     */
    public void moveLeft();
    /**
     * Summary :
     *      This handles with the shooting from the player and spawning of the new bullet
     */
    public void shoot(boolean muted);
}
