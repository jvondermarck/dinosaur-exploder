package com.dinosaur.dinosaurexploder.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.time.TimerAction;
import javafx.util.Duration;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class CoinSpawnerTest {

  @Test
  public void expiresOldTimerWhenRestarted() {
    CoinSpawner spawner = new CoinSpawner(50, 1);
    TimerAction timer = mock(TimerAction.class);

    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      fxgl.when(() -> FXGL.run(any(Runnable.class), any(Duration.class))).thenReturn(timer);

      spawner.startSpawning();
      spawner.startSpawning();

      verify(timer).expire();
    }
  }
}
