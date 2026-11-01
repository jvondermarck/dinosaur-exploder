/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.almasb.fxgl.app.scene.GameScene;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.FXGLForKtKt;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.scene.SceneService;
import com.almasb.fxgl.ui.UIFactoryService;
import com.dinosaur.dinosaurexploder.components.LifeComponent;
import com.dinosaur.dinosaurexploder.components.PlayerComponent;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.model.GameOverStats;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import com.dinosaur.dinosaurexploder.utils.MockGameTimer;
import com.dinosaur.dinosaurexploder.view.GameOverMenu;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.BoundingBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

/**
 * Regression tests for issue #488 (active gameplay time) through the real game path: GameActions
 * captures, pauses and resumes its session timer, and GameOverDialog turns it into the
 * GameOverStats shown on the game-over screen.
 *
 * <p>The session timer is a {@link MockGameTimer} passed in through the GameActions constructor, so
 * each test controls time exactly. FXGL's static API is stubbed, and actions passed to
 * FXGL.runOnce() are held so each test decides when they run. GameData is stubbed so no save file
 * is read, and GameOverMenu is never really built: the test only records the stats it was given.
 */
class GameActionsSessionTimeTest {

  private final MockGameTimer sessionTimer = new MockGameTimer();
  private final List<Runnable> scheduled = new ArrayList<>();
  private final List<GameOverStats> shownStats = new ArrayList<>();
  private final GameInitializer gameInitializer = mock(GameInitializer.class);

  private MockedStatic<FXGL> fxgl;
  private MockedStatic<FXGLForKtKt> fxglKt;
  private MockedStatic<GameData> gameData;
  private MockedConstruction<GameOverMenu> gameOverMenus;

  @BeforeEach
  void setUp() {
    fxgl = mockStatic(FXGL.class);
    fxgl.when(FXGL::getGameWorld).thenReturn(mock(GameWorld.class));
    fxgl.when(FXGL::getGameScene).thenReturn(mock(GameScene.class));
    fxgl.when(FXGL::getSceneService).thenReturn(mock(SceneService.class));
    fxgl.when(() -> FXGL.runOnce(any(Runnable.class), any(Duration.class)))
        .thenAnswer(
            call -> {
              scheduled.add(call.getArgument(0));
              return null;
            });

    Text levelMessage = mock(Text.class);
    when(levelMessage.getLayoutBounds()).thenReturn(new BoundingBox(0, 0, 0, 0));
    UIFactoryService uiFactory = mock(UIFactoryService.class);
    when(uiFactory.newText(anyString(), any(Color.class), anyDouble())).thenReturn(levelMessage);
    fxglKt = mockStatic(FXGLForKtKt.class);
    fxglKt.when(FXGLForKtKt::getUIFactoryService).thenReturn(uiFactory);

    // GameOverDialog asks GameData for the high score, which would read highScore.ser.
    gameData = mockStatic(GameData.class);

    gameOverMenus =
        mockConstruction(
            GameOverMenu.class,
            (menu, context) -> shownStats.add((GameOverStats) context.arguments().get(1)));

    stubGameInitializer();
  }

  @AfterEach
  void tearDown() {
    gameOverMenus.close();
    gameData.close();
    fxglKt.close();
    fxgl.close();
  }

  /**
   * The #488 regression through the real game path: 10 s of play, a level-up freeze that lasts 57
   * s, 5 s of play, then game over. TIME PLAYED must be 15 s, shown as "0m 15s".
   */
  @Test
  void gameOver_tenActiveFiftySevenFrozenFiveActive_showsFifteenSeconds() {
    GameActions gameActions = new GameActions(gameInitializer, sessionTimer);

    play(10);
    levelUp(gameActions, 57);
    play(5);
    gameActions.gameOver();

    GameOverStats stats = lastShownStats();
    assertEquals(15, stats.survivedSeconds());
    assertEquals("0m 15s", stats.formatElapsedTime());
  }

  @Test
  void gameOver_afterSeveralLevelUps_countsOnlyActivePlay() {
    GameActions gameActions = new GameActions(gameInitializer, sessionTimer);

    for (int level = 0; level < 3; level++) {
      play(4);
      levelUp(gameActions, 2);
    }
    play(4);
    gameActions.gameOver();

    assertEquals(16, lastShownStats().survivedSeconds());
  }

  /** Fails if pauseElement() stops pausing the session timer. */
  @Test
  void gameOver_afterPauseElement_ignoresTimeAfterDeath() {
    GameActions gameActions = new GameActions(gameInitializer, sessionTimer);

    play(7);
    gameActions.pauseElement(); // what the game-over sequence does when the player dies
    play(3); // explosion, then the delay before the dialog opens
    gameActions.gameOver();

    assertEquals(7, lastShownStats().survivedSeconds());
  }

  /** The timer already holds earlier time, and it was left paused by the last game over. */
  @Test
  void constructor_timerWithEarlierPausedTime_startsSessionFromZero() {
    play(30);
    sessionTimer.pause();
    play(1);

    GameActions gameActions = new GameActions(gameInitializer, sessionTimer);
    play(5);
    gameActions.gameOver();

    assertEquals(5, lastShownStats().survivedSeconds());
  }

  private void play(double seconds) {
    sessionTimer.advance(Duration.seconds(seconds));
  }

  /**
   * Shows the "LEVEL n" message, keeps it on screen for the given time, then runs the action that
   * ends it. showLevelMessage() schedules exactly one action: the end of the message.
   */
  private void levelUp(GameActions gameActions, double secondsOnScreen) {
    int alreadyScheduled = scheduled.size();
    gameActions.showLevelMessage();
    Runnable endOfMessage = scheduled.get(alreadyScheduled);

    play(secondsOnScreen);
    endOfMessage.run();
  }

  private GameOverStats lastShownStats() {
    assertEquals(1, gameOverMenus.constructed().size(), "game-over screen shown once");
    return shownStats.get(shownStats.size() - 1);
  }

  private void stubGameInitializer() {
    LevelManager levelManager = mock(LevelManager.class);
    when(levelManager.getCurrentLevel()).thenReturn(2);

    LanguageManager languageManager = mock(LanguageManager.class);
    when(languageManager.getTranslation("level")).thenReturn("Level");

    Entity life = mock(Entity.class);
    when(life.getComponent(LifeComponent.class)).thenReturn(mock(LifeComponent.class));

    Entity player = mock(Entity.class);
    when(player.getComponent(PlayerComponent.class)).thenReturn(mock(PlayerComponent.class));

    Entity levelDisplay = mock(Entity.class);
    ViewComponent levelDisplayView = mock(ViewComponent.class);
    when(levelDisplay.getViewComponent()).thenReturn(levelDisplayView);
    when(levelDisplayView.getChildren()).thenReturn(List.of(mock(Text.class)));

    when(gameInitializer.getEnemySpawner()).thenReturn(mock(EnemySpawner.class));
    when(gameInitializer.getAsteroidsSpawner()).thenReturn(mock(AsteroidsSpawner.class));
    when(gameInitializer.getLevelManager()).thenReturn(levelManager);
    when(gameInitializer.getLanguageManager()).thenReturn(languageManager);
    when(gameInitializer.getLevelDisplay()).thenReturn(levelDisplay);
    when(gameInitializer.getPlayer()).thenReturn(player);
    when(gameInitializer.getLife()).thenReturn(life);
    when(gameInitializer.getLevelProgressBar()).thenReturn(mock(Entity.class));
    when(gameInitializer.getBomb()).thenReturn(mock(Entity.class));
  }
}
