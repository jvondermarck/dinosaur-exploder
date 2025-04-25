package com.dinosaur.dinosaurexploder.interfaces;
/**
 * Summary :
 *      This interface is to implement in  new playerClass for clean Understanding
 */
public interface Life {
    /**
     * Summary :
     *      This method runs for every frame like a continues flow , without any stop until we put stop to it.
     * Parameters :
     *      double ptf
     */
    void onUpdate(double tpf);
    /**
     * Summary :
     *      This method sets the life to the current life
     */
    int decreaseLife(int i);
}