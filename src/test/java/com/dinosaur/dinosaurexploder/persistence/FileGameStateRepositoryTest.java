/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.constants.GameMode;
import com.dinosaur.dinosaurexploder.model.GameState;
import com.dinosaur.dinosaurexploder.model.HighScore;
import com.dinosaur.dinosaurexploder.model.TotalCoins;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * The debug-editable game state lives in ONE human-readable JSON file (savegame.json), not in
 * separate Java-serialized files (#543).
 */
public class FileGameStateRepositoryTest {

  @TempDir Path tempDir;

  private final ObjectMapper mapper = new ObjectMapper();

  private FileGameStateRepository repository() {
    return new FileGameStateRepository(tempDir.resolve(GameConstants.GAME_STATE_FILE));
  }

  @Test
  void saveThenLoad_roundTripsHighScoresAndCoins() {
    FileGameStateRepository repository = repository();
    GameState state = new GameState();
    state.setHighScores(new HighScore().getAllScores());
    state.getHighScores().put(GameMode.EASY.name(), 1200);
    state.setTotalCoins(340);

    repository.save(state);
    GameState loaded = repository.load();

    assertEquals(1200, loaded.getHighScores().get(GameMode.EASY.name()));
    assertEquals(340, loaded.getTotalCoins());
  }

  @Test
  void save_writesOneHumanReadableJsonFile() throws Exception {
    FileGameStateRepository repository = repository();
    GameState state = new GameState(new HighScore(), new TotalCoins(7));

    repository.save(state);

    try (Stream<Path> files = Files.list(tempDir)) {
      List<Path> written = files.filter(Files::isRegularFile).toList();
      assertEquals(1, written.size(), "exactly one save file, got " + written);
      assertEquals(GameConstants.GAME_STATE_FILE, written.get(0).getFileName().toString());
    }
    String content = Files.readString(tempDir.resolve(GameConstants.GAME_STATE_FILE));
    JsonNode json = mapper.readTree(content);
    assertTrue(json.has("highScores"), "human-readable JSON names the high scores: " + content);
    assertTrue(json.has("totalCoins"), "human-readable JSON names the coins: " + content);
    assertEquals(7, json.path("totalCoins").asInt());
    assertFalse(content.startsWith("\uACED"), "not Java serialization anymore");
  }

  @Test
  void load_withoutFile_returnsDefaultsWithoutWriting() {
    GameState loaded = repository().load();

    assertEquals(0, loaded.getTotalCoins());
    for (GameMode mode : GameMode.values()) {
      assertEquals(0, loaded.getHighScores().get(mode.name()));
    }
    assertFalse(Files.exists(tempDir.resolve(GameConstants.GAME_STATE_FILE)));
  }

  @Test
  void load_withCorruptJson_returnsDefaults() throws Exception {
    Files.writeString(tempDir.resolve(GameConstants.GAME_STATE_FILE), "{ not json !!!");

    GameState loaded = repository().load();

    assertEquals(0, loaded.getTotalCoins());
    assertEquals(0, loaded.getHighScores().get(GameMode.EXPERT.name()));
  }

  @Test
  void load_withLegacySerializedFiles_migratesThemIntoTheOneJsonFile() throws Exception {
    HighScore legacyHighScore = new HighScore();
    legacyHighScore.setHigh(GameMode.NORMAL.name(), 777);
    try (ObjectOutputStream out =
        new ObjectOutputStream(
            new FileOutputStream(tempDir.resolve(GameConstants.HIGH_SCORE_FILE).toFile()))) {
      out.writeObject(legacyHighScore);
    }
    try (ObjectOutputStream out =
        new ObjectOutputStream(
            new FileOutputStream(tempDir.resolve(GameConstants.TOTAL_COINS_FILE).toFile()))) {
      out.writeObject(new TotalCoins(55));
    }

    GameState loaded = repository().load();

    assertEquals(777, loaded.getHighScores().get(GameMode.NORMAL.name()));
    assertEquals(55, loaded.getTotalCoins());
    Path json = tempDir.resolve(GameConstants.GAME_STATE_FILE);
    assertTrue(Files.isRegularFile(json), "the one JSON file was written");
    assertFalse(
        Files.exists(tempDir.resolve(GameConstants.HIGH_SCORE_FILE)), "legacy .ser removed");
    assertFalse(
        Files.exists(tempDir.resolve(GameConstants.TOTAL_COINS_FILE)), "legacy .ser removed");
    assertEquals(55, mapper.readTree(Files.readString(json)).path("totalCoins").asInt());
  }

  @Test
  void save_overwritesPreviousStateAtomically() throws Exception {
    FileGameStateRepository repository = repository();
    GameState first = new GameState(new HighScore(), new TotalCoins(1));
    repository.save(first);
    GameState second = new GameState(new HighScore(), new TotalCoins(2));
    repository.save(second);

    assertEquals(2, repository.load().getTotalCoins());
    try (Stream<Path> files = Files.list(tempDir)) {
      assertEquals(1, files.filter(Files::isRegularFile).count(), "no temp file left behind");
    }
  }

  @Test
  void highScoreRepository_delegatesToTheOneFileKeepingCoins() {
    FileGameStateRepository gameState = repository();
    FileHighScoreRepository highScores = new FileHighScoreRepository(gameState);
    FileTotalCoinsRepository coins = new FileTotalCoinsRepository(gameState);

    coins.save(new TotalCoins(90));
    HighScore highScore = new HighScore();
    highScore.setHigh(GameMode.EXPERT.name(), 4321);
    highScores.save(highScore);

    assertEquals(4321, highScores.load().getHigh(GameMode.EXPERT.name()));
    assertEquals(90, coins.load().getTotal());
    assertEquals(4321, gameState.load().toHighScore().getHigh(GameMode.EXPERT.name()));
    assertEquals(90, gameState.load().toTotalCoins().getTotal());
  }

  @Test
  void coinsRepository_delegatesToTheOneFileKeepingHighScores() {
    FileGameStateRepository gameState = repository();
    FileHighScoreRepository highScores = new FileHighScoreRepository(gameState);
    FileTotalCoinsRepository coins = new FileTotalCoinsRepository(gameState);

    HighScore highScore = new HighScore();
    highScore.setHigh(GameMode.EASY.name(), 12);
    highScores.save(highScore);
    coins.save(new TotalCoins(66));

    assertEquals(12, highScores.load().getHigh(GameMode.EASY.name()));
    assertEquals(66, coins.load().getTotal());
  }

  @Test
  void legacyRead_withMissingClassStillYieldsDefaults() throws Exception {
    // a .ser file that cannot be deserialized must not crash the game
    Files.write(tempDir.resolve(GameConstants.HIGH_SCORE_FILE), new byte[] {1, 2, 3});

    GameState loaded = repository().load();

    assertEquals(0, loaded.getTotalCoins());
  }

  @Test
  void gameState_convertsToAndFromTheDomainObjects() {
    HighScore highScore = new HighScore();
    highScore.setHigh(GameMode.NORMAL.name(), 9);
    GameState state = new GameState(highScore, new TotalCoins(8));

    assertEquals(9, state.toHighScore().getHigh(GameMode.NORMAL.name()));
    assertEquals(8, state.toTotalCoins().getTotal());
    assertEquals(
        9,
        new GameState(state.toHighScore(), state.toTotalCoins())
            .toHighScore()
            .getHigh(GameMode.NORMAL.name()));
  }
}
