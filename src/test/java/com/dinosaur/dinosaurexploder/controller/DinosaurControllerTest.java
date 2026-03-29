/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.dinosaur.dinosaurexploder.controller.core.CollisionRegistry;
import com.dinosaur.dinosaurexploder.controller.core.GameActions;
import com.dinosaur.dinosaurexploder.controller.core.GameInitializer;
import com.dinosaur.dinosaurexploder.controller.core.collisions.*;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

class DinosaurControllerTest {

  @Test
  public void initPhysics_ifInitGameWasNotCalled() {

    DinosaurController controller = new DinosaurController();

    IllegalStateException ex = assertThrows(IllegalStateException.class, controller::initPhysics);

    assertTrue(ex.getMessage().contains("GameActions must be initialized"));
  }

  @Test
  public void initPhysics_andRegisterAll() {
    try (MockedConstruction<GameInitializer> initCons = mockConstruction(GameInitializer.class);
        MockedConstruction<CollisionRegistry> registryCons =
            mockConstruction(CollisionRegistry.class);
        MockedConstruction<GameActions> actionsCons = mockConstruction(GameActions.class);
        MockedConstruction<EnemyProjectilePlayerCollision> c1 =
            mockConstruction(EnemyProjectilePlayerCollision.class);
        MockedConstruction<PlayerAsteroidsCollision> c2 =
            mockConstruction(PlayerAsteroidsCollision.class);
        MockedConstruction<PlayerCoinCollision> c3 = mockConstruction(PlayerCoinCollision.class);
        MockedConstruction<PlayerGreenDinoCollision> c4 =
            mockConstruction(PlayerGreenDinoCollision.class);
        MockedConstruction<PlayerHeartCollision> c5 = mockConstruction(PlayerHeartCollision.class);
        MockedConstruction<PlayerOrangeDinoCollision> c6 =
            mockConstruction(PlayerOrangeDinoCollision.class);
        MockedConstruction<PlayerRedDinoCollision> c7 =
            mockConstruction(PlayerRedDinoCollision.class);
        MockedConstruction<ProjectileAsteroidsCollision> c8 =
            mockConstruction(ProjectileAsteroidsCollision.class);
        MockedConstruction<ProjectileEnemyProjectileCollision> c9 =
            mockConstruction(ProjectileEnemyProjectileCollision.class);
        MockedConstruction<ProjectileGreenDinoCollision> c10 =
            mockConstruction(ProjectileGreenDinoCollision.class);
        MockedConstruction<ProjectileOrangeDinoCollision> c11 =
            mockConstruction(ProjectileOrangeDinoCollision.class);
        MockedConstruction<ProjectileRedDinoCollision> c12 =
            mockConstruction(ProjectileRedDinoCollision.class)) {

      DinosaurController controller = new DinosaurController();

      GameInitializer initializer = initCons.constructed().get(0);
      CollisionRegistry registry = registryCons.constructed().get(0);

      controller.initGame(null);
      GameActions actions = actionsCons.constructed().get(0);

      controller.initPhysics();

      verify(registry, times(12)).addCollision(any());

      verify(registry).registerAll();

      verify(registry).addCollision(c1.constructed().get(0));
      verify(registry).addCollision(c2.constructed().get(0));
      verify(registry).addCollision(c3.constructed().get(0));
      verify(registry).addCollision(c4.constructed().get(0));
      verify(registry).addCollision(c5.constructed().get(0));
      verify(registry).addCollision(c6.constructed().get(0));
      verify(registry).addCollision(c7.constructed().get(0));
      verify(registry).addCollision(c8.constructed().get(0));
      verify(registry).addCollision(c9.constructed().get(0));
      verify(registry).addCollision(c10.constructed().get(0));
      verify(registry).addCollision(c11.constructed().get(0));
      verify(registry).addCollision(c12.constructed().get(0));
    }
  }
}
