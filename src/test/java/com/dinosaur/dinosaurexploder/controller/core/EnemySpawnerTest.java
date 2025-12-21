package com.dinosaur.dinosaurexploder.controller.core;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.time.TimerAction;
import com.dinosaur.dinosaurexploder.controller.BossSpawner;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import javafx.util.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnemySpawnerSpawnEnemiesTest {

    @Mock
    GameInitializer gameInitializer;

    @Mock
    LevelManager levelManager;

    @Mock
    BossSpawner bossSpawner;

    @Mock
    TimerAction timer1;

    @Mock
    TimerAction timer2;

    private EnemySpawner spawner;

    @BeforeEach
    public void setup() {
        when(gameInitializer.getLevelManager()).thenReturn(levelManager);
        when(gameInitializer.getBossSpawner()).thenReturn(bossSpawner);
        when(levelManager.getEnemySpawnRate()).thenReturn(1.0);

        spawner = new EnemySpawner(gameInitializer);
    }

    @Test
    public void spawnEnemies_level10_spawnsOrangeBossAndPauses() {
        when(levelManager.getCurrentLevel()).thenReturn(10);

        ArgumentCaptor<Runnable> tick = ArgumentCaptor.forClass(Runnable.class);

        try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
            fxgl.when(() -> FXGL.run(tick.capture(), any(Duration.class))).thenReturn(timer1);

            spawner.spawnEnemies();
            tick.getValue().run();

            verify(bossSpawner).spawnNewBoss("orange");
            verify(timer1).pause();
        }
    }

    @Test
    public void spawnEnemies_level5_spawnsRedBossAndPauses() {
        when(levelManager.getCurrentLevel()).thenReturn(5);

        ArgumentCaptor<Runnable> tick = ArgumentCaptor.forClass(Runnable.class);

        try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
            fxgl.when(() -> FXGL.run(tick.capture(), any(Duration.class))).thenReturn(timer1);

            spawner.spawnEnemies();
            tick.getValue().run();

            verify(bossSpawner).spawnNewBoss("red");
            verify(timer1).pause();
        }
    }


}
