package com.dinosaur.dinosaurexploder.interfaces;

import com.almasb.fxgl.entity.Entity;

/** Summary: This interface defines the behavior and properties of a bomb. */
public interface Bomb {
  /** Summary: This method returns the current number of bombs. */
  int getBombCount();

  /**
   * Summary : This method runs for every frame like a continues flow , without any stop until we
   * put stop to it. Parameters : double ptf
   */
  void onUpdate(double tpf);

  /**
   * Summary : This method is used to launch a row of bullets as a bomb. Parameters : Entity player
   * - The player entity using the bomb
   */
  void useBomb(Entity entity);
}
