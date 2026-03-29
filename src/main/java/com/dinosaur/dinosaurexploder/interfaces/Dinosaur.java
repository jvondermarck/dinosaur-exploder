/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.interfaces;

/** Summary : This interface is to implement in new DinosaurClass for clean Understanding */
public interface Dinosaur {
  /**
   * Summary: This method returns the lives that the dino has currently.
   *
   * @return lives
   */
  int getLives();

  /** Summary: This method damages the dino */
  void damage(int damage);

  /**
   * Summary : This method runs for every frame like a continues flow , without any stop until we
   * put stop to it. Parameters : double ptf
   */
  void onUpdate(double ptf);

  /** Summary : This handles with the shooting from the dinosaur and spawning of the new bullet */
  void shoot();
}
