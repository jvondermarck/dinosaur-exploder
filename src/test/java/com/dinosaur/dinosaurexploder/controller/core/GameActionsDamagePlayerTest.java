/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller.core;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import com.almasb.fxgl.app.scene.GameScene;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.FXGLForKtKt;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.ui.UIFactoryService;
import com.dinosaur.dinosaurexploder.components.BombComponent;
import com.dinosaur.dinosaurexploder.components.LifeComponent;
import com.dinosaur.dinosaurexploder.components.PlayerComponent;
import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.constants.GameMode;
import com.dinosaur.dinosaurexploder.model.CollisionHandler;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.utils.FXGLGameTimer;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import java.util.Collections;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.util.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;

/**
 * Behaviour tests for {@link GameActions#damagePlayer()}.
 *
 * <p>The suite covers the four rules that govern what happens when the player is hit:
 *
 * <ul>
 *   <li>An already-invincible player takes no damage and does not start a new protection timer.
 *   <li>A lethal hit consumes the last life, hides the player, spawns the explosion, and pauses all
 *       enemy and asteroid spawners so the world stops producing threats.
 *   <li>A second hit after the game-over sequence has already fired is a no-op, so the death
 *       pipeline cannot stack across frames.
 *   <li>Higher difficulties shorten the post-hit invincibility window ({@code NORMAL} = 1.5 s,
 *       {@code EXPERT} = 1.0 s).
 * </ul>
 *
 * <p>{@code damagePlayer()} reaches into static FXGL calls (timers, spawning, scene mutation), so
 * each test installs static mocks to observe one behaviour in isolation. The fixture is split into
 * small helpers below, each one wires a single concern (dependencies, entity components, view
 * layer, player geometry, timer construction, static mocks, default state), so a failing test
 * points at one setup concern rather than a 40-line {@code @BeforeEach}.
 *
 * @see GameActions#damagePlayer()
 * @see PlayerComponent
 * @see LifeComponent
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Player Damage Lifecycle Invariants")
public class GameActionsDamagePlayerTest {
  @Mock private UIFactoryService uiFactoryService;

  @Mock private ViewComponent playerViewComponent;

  @Mock private ViewComponent levelDisplayViewComponent;

  @Mock private Entity levelDisplay;

  @Mock private Entity player;

  @Mock private Entity life;

  @Mock private Entity levelProgressBar;

  @Mock private Entity bomb;

  @Mock private GameScene gameScene;

  @Mock private GameWorld gameWorld;

  @Mock private GameInitializer gameInitializer;

  @Mock private EnemySpawner enemySpawner;

  @Mock private AsteroidsSpawner asteroidsSpawner;

  @Mock private PlayerComponent playerComponent;

  @Mock private LifeComponent lifeComponent;

  @Mock private BombComponent bombComponent;

  @Mock private CollisionHandler collisionHandler;

  @Mock private LevelManager levelManager;

  @Mock private LanguageManager languageManager;

  private GameActions gameActions;
  private MockedStatic<FXGL> fxglMock;
  private MockedStatic<FXGLForKtKt> fxglKtMock;
  private MockedStatic<GameData> gameDataMock;
  private MockedConstruction<FXGLGameTimer> timerConstructionMock;

  /**
   * Points the mocked {@link GameInitializer} at the mocked sub-spawners, entities and managers
   * that {@link GameActions} reads during construction.
   */
  private void wireGameInitializerDependencies() {
    // Wire sub-spawners and dependencies
    lenient().when(gameInitializer.getEnemySpawner()).thenReturn(enemySpawner);
    lenient().when(gameInitializer.getAsteroidsSpawner()).thenReturn(asteroidsSpawner);
    lenient().when(gameInitializer.getCollisionHandler()).thenReturn(collisionHandler);
    lenient().when(gameInitializer.getLevelManager()).thenReturn(levelManager);
    lenient().when(gameInitializer.getLanguageManager()).thenReturn(languageManager);

    // Wire entities
    lenient().when(gameInitializer.getLevelDisplay()).thenReturn(levelDisplay);
    lenient().when(gameInitializer.getPlayer()).thenReturn(player);
    lenient().when(gameInitializer.getLife()).thenReturn(life);
    lenient().when(gameInitializer.getLevelProgressBar()).thenReturn(levelProgressBar);
    lenient().when(gameInitializer.getBomb()).thenReturn(bomb);
  }

  /**
   * Wires the components attached to each entity so that {@link GameActions}'s internal calls like
   * {@code life.getComponent(LifeComponent.class)} resolve to the mocks instead of {@code null}.
   */
  private void wireEntityComponents() {
    // Wire components to avoid internal NullPointerExceptions during setup
    lenient().when(life.getComponent(LifeComponent.class)).thenReturn(lifeComponent);
    lenient().when(player.getComponent(PlayerComponent.class)).thenReturn(playerComponent);
    lenient().when(bomb.hasComponent(BombComponent.class)).thenReturn(true);
    lenient().when(bomb.getComponent(BombComponent.class)).thenReturn(bombComponent);
  }

  /**
   * Wires the view layers that {@code damagePlayer()} mutates: the flash overlay, the player
   * opacity change, and the level display text.
   *
   * <p>The {@link Group} exists so that {@code updateLevelDisplay()} can reach a real {@link
   * javafx.scene.text.Text} child if a test ever exercises that path.
   */
  private void wireViewLayer() {
    // Wire the view layers for UI mutations
    lenient().when(player.getViewComponent()).thenReturn(playerViewComponent);

    Group mockChildren = new Group();
    mockChildren.getChildren().add(new javafx.scene.text.Text("LEVEL 1"));
    lenient().when(levelDisplay.getViewComponent()).thenReturn(levelDisplayViewComponent);
    lenient().when(levelDisplayViewComponent.getChildren()).thenReturn(mockChildren.getChildren());
  }

  /**
   * Stubs the player geometry read by the 'fire-particle emitter' and by the explosion spawn
   * position in {@code startGameOverSequence()}.
   */
  private void stubPlayerGeometry() {
    lenient().when(player.getWidth()).thenReturn(90.0);
    lenient().when(player.getHeight()).thenReturn(90.0);
    lenient().when(player.getCenter()).thenReturn(new Point2D(400, 500));
  }

  /**
   * Intercepts {@link FXGLGameTimer} construction so {@link GameActions} can create its session
   * timer without touching the FXGL runtime. {@code capture()} is stubbed as a no-op because the
   * real implementation relies on a JavaFX clock that is not available in unit tests.
   */
  private void stubTimerConstruction() {
    timerConstructionMock =
        mockConstruction(FXGLGameTimer.class, (mock, context) -> doNothing().when(mock).capture());
  }

  /**
   * Installs the static mocks that {@code GameActions} relies on: the FXGL DSL, the FXGL Kotlin
   * bridge, and {@link GameData}. The world context and RNG are stubbed leniently because different
   * tests reach different parts of the code, an early-exit test never touches the game world, while
   * the lethal-hit test does.
   */
  private void installStaticMocks() {
    // Set open mocks for static tool environments
    fxglMock = mockStatic(FXGL.class, withSettings().strictness(Strictness.LENIENT));
    fxglKtMock = mockStatic(FXGLForKtKt.class, withSettings().strictness(Strictness.LENIENT));
    gameDataMock = mockStatic(GameData.class);

    // Mock static world context defaults
    fxglMock.when(FXGL::getGameScene).thenReturn(gameScene);
    fxglMock.when(FXGL::getGameWorld).thenReturn(gameWorld);
    fxglKtMock.when(FXGLForKtKt::getUIFactoryService).thenReturn(uiFactoryService);

    fxglMock.when(() -> FXGL.random(anyDouble(), anyDouble())).thenReturn(1.0);
    lenient()
        .when(gameWorld.getEntitiesByType(any(EntityType.class)))
        .thenReturn(Collections.emptyList());
  }

  /**
   * Stubs the default player state for tests: three lives remaining. This avoids the initial
   * emission-rate adjustment that {@link GameActions} applies when the player starts at full
   * health, keeping the 'fire-particle' setup deterministic across tests.
   */
  private void stubDefaultPlayerState() {
    lenient().when(lifeComponent.getLife()).thenReturn(3);
  }

  @BeforeEach
  public void setup() {
    wireGameInitializerDependencies();
    wireEntityComponents();
    wireViewLayer();
    stubPlayerGeometry();
    stubTimerConstruction();
    installStaticMocks();
    stubDefaultPlayerState();

    gameActions = new GameActions(gameInitializer);
  }

  /**
   * Structural clean-up guarantees that no open static mocks or intercepted constructions cross
   * test boundaries. Each handle is closed in reverse order of creation.
   */
  @AfterEach
  public void tearDown() {
    // Structural clean-up guarantees that no open handles cross over test boundaries
    if (fxglMock != null) fxglMock.close();
    if (fxglKtMock != null) fxglKtMock.close();
    if (gameDataMock != null) gameDataMock.close();
    if (timerConstructionMock != null) timerConstructionMock.close();
  }

  /**
   * A player who is still within their post-hit invincibility window ignores further damage. The
   * collision handler is not consulted and no new protection timer is scheduled, so a second
   * collision cannot stack on an already-protected player.
   */
  @Test
  @DisplayName("Damage is skipped if the player entity is already invincible")
  public void damagePlayer_playerAlreadyInvincible_doesNotApplyDamage() {
    when(playerComponent.isInvincible()).thenReturn(true);

    gameActions.damagePlayer();

    verify(collisionHandler, never()).getDamagedPlayerLife(any());
    fxglMock.verify(() -> FXGL.runOnce(any(Runnable.class), any(Duration.class)), never());
  }

  /**
   * The invincibility window after a hit is shorter on higher difficulties. On {@code NORMAL} the
   * player is protected for 1.5 s; on {@code EXPERT} only 1.0 s. This parameterised test exercises
   * both modes so a regression in the difficulty scaling cannot go unverified.
   */
  @ParameterizedTest(name = "{0} difficulty applies the expected invincibility window")
  @EnumSource(
      value = GameMode.class,
      names = {"NORMAL", "EXPERT"})
  @DisplayName("Difficulty scales invincibility duration correctly")
  void damagePlayer_scalesInvincibilityDurationByDifficulty(GameMode mode) {
    when(playerComponent.isInvincible()).thenReturn(false);
    gameDataMock.when(GameData::getSelectedDifficulty).thenReturn(mode);
    when(collisionHandler.getDamagedPlayerLife(lifeComponent)).thenReturn(2);

    ArgumentCaptor<Duration> durationCaptor = ArgumentCaptor.forClass(Duration.class);

    gameActions.damagePlayer();

    verify(playerComponent).setInvincible(true);
    fxglMock.verify(() -> FXGL.runOnce(any(Runnable.class), durationCaptor.capture()), times(2));

    double expectedSeconds = (mode == GameMode.EXPERT) ? 1.0 : 1.5;
    boolean containsExpectedWindow =
        durationCaptor.getAllValues().stream()
            .anyMatch(duration -> duration.toSeconds() == expectedSeconds);
    assertTrue(
        containsExpectedWindow,
        "Expected an invincibility window of " + expectedSeconds + "s for " + mode);
  }

  /**
   * A lethal hit ends the game: the last life is consumed, the player disappears from the screen,
   * an explosion is spawned at their position, and every enemy and asteroid spawner is paused. The
   * world stops producing new threats the moment the player dies.
   */
  @Test
  @DisplayName("Lethal damage drops player health to 0 and suspends game world spawner updates")
  public void damagePlayer_lethalHit_triggersGameOverSequenceAndSuspendsWorld() {
    when(playerComponent.isInvincible()).thenReturn(false);
    gameDataMock.when(GameData::getSelectedDifficulty).thenReturn(GameMode.NORMAL);
    when(collisionHandler.getDamagedPlayerLife(lifeComponent)).thenReturn(0);

    gameActions.damagePlayer();

    verify(lifeComponent).onUpdate(0);
    verify(playerComponent, atLeastOnce()).setInvincible(true);
    verify(playerViewComponent).setOpacity(0.0);

    verify(enemySpawner).pauseEnemySpawning();
    verify(asteroidsSpawner).pauseAsteroidsSpawning();

    fxglMock.verify(() -> FXGL.spawn(eq("explosion"), any(Point2D.class)));
  }

  /**
   * After a lethal hit has started the game-over sequence, subsequent damage calls are no-ops. The
   * collision handler is not consulted again, so the death pipeline cannot stack and multiple
   * game-over dialogues cannot spawn on subsequent frames.
   */
  @Test
  @DisplayName(
      "Subsequent hits exit immediately if the game-over sequence has already been triggered")
  public void damagePlayer_subsequentHitsBlocked_whenGameOverAlreadyTriggered() {
    when(playerComponent.isInvincible()).thenReturn(false);
    gameDataMock.when(GameData::getSelectedDifficulty).thenReturn(GameMode.NORMAL);
    when(collisionHandler.getDamagedPlayerLife(lifeComponent)).thenReturn(0);

    // First call walks the death pipeline and sets gameOverTriggered = true.
    gameActions.damagePlayer();
    reset(collisionHandler);

    // Second call on the next frame must short-circuit before touching the collision handler.
    gameActions.damagePlayer();

    verify(collisionHandler, never()).getDamagedPlayerLife(any());
  }
}
