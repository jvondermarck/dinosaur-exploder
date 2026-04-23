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
import com.dinosaur.dinosaurexploder.controller.core.collisions.ally.AllyAsteroidsCollision;
import com.dinosaur.dinosaurexploder.controller.core.collisions.ally.AllyGreenDinoCollision;
import com.dinosaur.dinosaurexploder.controller.core.collisions.ally.AllyOrangeDinoCollision;
import com.dinosaur.dinosaurexploder.controller.core.collisions.ally.AllyRedDinoCollision;
import com.dinosaur.dinosaurexploder.controller.core.collisions.player.*;
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
            mockConstruction(ProjectileRedDinoCollision.class);
        MockedConstruction<PlayerAllyDropCollision> c13 =
            mockConstruction(PlayerAllyDropCollision.class);
        MockedConstruction<AllyAsteroidsCollision> c14 =
            mockConstruction(AllyAsteroidsCollision.class);
        MockedConstruction<AllyRedDinoCollision> c15 =
            mockConstruction(AllyRedDinoCollision.class);
        MockedConstruction<AllyOrangeDinoCollision> c16 =
            mockConstruction(AllyOrangeDinoCollision.class);
        MockedConstruction<AllyGreenDinoCollision> c17 =
            mockConstruction(AllyGreenDinoCollision.class); ) {

      DinosaurController controller = new DinosaurController();

      GameInitializer initializer = initCons.constructed().get(0);
      CollisionRegistry registry = registryCons.constructed().get(0);

      controller.initGame(null);
      GameActions actions = actionsCons.constructed().get(0);

      controller.initPhysics();

      verify(registry, times(17)).addCollision(any());

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
      verify(registry).addCollision(c13.constructed().get(0));
      verify(registry).addCollision(c14.constructed().get(0));
      verify(registry).addCollision(c15.constructed().get(0));
      verify(registry).addCollision(c16.constructed().get(0));
      verify(registry).addCollision(c17.constructed().get(0));
    }
  }
}
