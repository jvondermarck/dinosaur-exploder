/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.persistence;

import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.model.GameState;
import com.dinosaur.dinosaurexploder.model.HighScore;
import com.dinosaur.dinosaurexploder.model.TotalCoins;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stores the debug-editable game state in ONE human-readable JSON file, {@value
 * GameConstants#GAME_STATE_FILE} (#543), instead of separate Java-serialized files:
 *
 * <pre>{@code
 * {
 *   "highScores" : { "EASY" : 0, "NORMAL" : 0, "EXPERT" : 0 },
 *   "totalCoins" : 0
 * }
 * }</pre>
 *
 * <p>Writes go to a temporary file that is then moved into place, so a crash never leaves half a
 * save file. When the JSON file is absent but the legacy {@code highScore.ser} / {@code
 * totalCoins.ser} exist, they are read once, moved into the JSON file and removed.
 */
public class FileGameStateRepository {

  private static final Logger LOGGER = Logger.getLogger(FileGameStateRepository.class.getName());

  private final Path stateFile;
  private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  public FileGameStateRepository() {
    this(Path.of(GameConstants.GAME_STATE_FILE));
  }

  public FileGameStateRepository(Path stateFile) {
    this.stateFile = stateFile;
  }

  /**
   * The stored state, or defaults when nothing is saved yet; legacy serialized files are migrated
   * once.
   */
  public GameState load() {
    if (Files.isRegularFile(stateFile)) {
      try {
        return mapper.readValue(stateFile.toFile(), GameState.class);
      } catch (IOException e) {
        LOGGER.log(
            Level.INFO,
            "Could not read {0}, using defaults: {1}",
            new Object[] {stateFile, e.getMessage()});
        return new GameState();
      }
    }
    return migrateLegacyFiles();
  }

  /** Writes the state as pretty-printed JSON, atomically. */
  public void save(GameState state) {
    try {
      Path parent = stateFile.toAbsolutePath().getParent();
      if (parent != null) {
        Files.createDirectories(parent);
      }
      Path tmp = stateFile.resolveSibling(stateFile.getFileName() + ".tmp");
      mapper.writeValue(tmp.toFile(), state);
      Files.move(tmp, stateFile, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error saving game state: {0}", e.getMessage());
    }
  }

  // The pre-#543 state was two Java-serialized files; read them once into the JSON file and remove
  // them.
  private GameState migrateLegacyFiles() {
    Path legacyHighScore = legacySibling(GameConstants.HIGH_SCORE_FILE);
    Path legacyCoins = legacySibling(GameConstants.TOTAL_COINS_FILE);
    if (!Files.isRegularFile(legacyHighScore) && !Files.isRegularFile(legacyCoins)) {
      return new GameState();
    }
    HighScore highScore = readLegacy(legacyHighScore, HighScore.class, new HighScore());
    TotalCoins coins = readLegacy(legacyCoins, TotalCoins.class, new TotalCoins());
    GameState state = new GameState(highScore, coins);
    save(state);
    deleteQuietly(legacyHighScore);
    deleteQuietly(legacyCoins);
    LOGGER.log(
        Level.INFO,
        "Migrated {0} and {1} into {2}",
        new Object[] {legacyHighScore, legacyCoins, stateFile});
    return state;
  }

  private Path legacySibling(String name) {
    Path parent = stateFile.toAbsolutePath().getParent();
    return parent == null ? Path.of(name) : parent.resolve(name);
  }

  private static <T> T readLegacy(Path file, Class<T> type, T fallback) {
    if (!Files.isRegularFile(file)) {
      return fallback;
    }
    try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
      return type.cast(in.readObject());
    } catch (IOException | ClassNotFoundException | ClassCastException e) {
      LOGGER.log(Level.INFO, "Could not read legacy {0}: {1}", new Object[] {file, e.getMessage()});
      return fallback;
    }
  }

  private static void deleteQuietly(Path file) {
    try {
      Files.deleteIfExists(file);
    } catch (IOException e) {
      LOGGER.log(
          Level.INFO, "Could not delete legacy {0}: {1}", new Object[] {file, e.getMessage()});
    }
  }
}
