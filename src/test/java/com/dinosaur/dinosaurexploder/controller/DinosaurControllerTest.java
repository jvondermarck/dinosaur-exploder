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
        MockedConstruction<PlayerCoinCollision> c2 = mockConstruction(PlayerCoinCollision.class);
        MockedConstruction<PlayerGreenDinoCollision> c3 =
            mockConstruction(PlayerGreenDinoCollision.class);
        MockedConstruction<PlayerHeartCollision> c4 = mockConstruction(PlayerHeartCollision.class);
        MockedConstruction<PlayerOrangeDinoCollision> c5 =
            mockConstruction(PlayerOrangeDinoCollision.class);
        MockedConstruction<PlayerRedDinoCollision> c6 =
            mockConstruction(PlayerRedDinoCollision.class);
        MockedConstruction<ProjectileEnemyProjectileCollision> c7 =
            mockConstruction(ProjectileEnemyProjectileCollision.class);
        MockedConstruction<ProjectileGreenDinoCollision> c8 =
            mockConstruction(ProjectileGreenDinoCollision.class);
        MockedConstruction<ProjectileOrangeDinoCollision> c9 =
            mockConstruction(ProjectileOrangeDinoCollision.class);
        MockedConstruction<ProjectileRedDinoCollision> c10 =
            mockConstruction(ProjectileRedDinoCollision.class)) {

      DinosaurController controller = new DinosaurController();

      GameInitializer initializer = initCons.constructed().get(0);
      CollisionRegistry registry = registryCons.constructed().get(0);

      controller.initGame(null);
      GameActions actions = actionsCons.constructed().get(0);

      controller.initPhysics();

      verify(registry, times(10)).addCollision(any());

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
    }
  }
}
