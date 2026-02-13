/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller.core;

import static org.mockito.Mockito.mock;

import com.dinosaur.dinosaurexploder.controller.core.collisions.CollisionHandlerInterface;
import org.junit.jupiter.api.Test;

public class CollisionRegistryTest {

  @Test
  public void testCollison() {
    CollisionRegistry collisionRegistry = new CollisionRegistry();

    CollisionHandlerInterface collisionHandlerInterface = mock(CollisionHandlerInterface.class);

    collisionRegistry.addCollision(collisionHandlerInterface);

    collisionRegistry.registerAll();
  }
}
