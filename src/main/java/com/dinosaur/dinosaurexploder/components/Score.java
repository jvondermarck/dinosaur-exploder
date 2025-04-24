package com.dinosaur.dinosaurexploder.components;
/**
 * Summary :
 *      This interface is to implement in  new playerClass for clean Understanding
 */
public interface Score {
    /**
     * Summary :
     *      This method runs for every frame like a continues flow , without any stop until we put stop to it.
     * Parameters :
     *      double ptf
     */
    public void onUpdate(double tpf);
    /**
     * Summary :
     *      This method return the Score to the current Score
     */
    public int getScore();
    /**
     * Summary :
     *      This method sets the Score to the current Score
     */
    public void setScore(int i);
    /**
     * Summary :
     *      This method increment the Score to the current Score
     */
    public void incrementScore(int i);

}