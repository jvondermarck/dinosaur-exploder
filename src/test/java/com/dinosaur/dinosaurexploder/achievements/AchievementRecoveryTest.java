/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dinosaur.dinosaurexploder.constants.GameConstants;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Verifies that {@link AchievementManager#loadAchievement()} is defensive against corrupted,
 * truncated, or type-incompatible save files, and that {@link AchievementManager#init()} self-heals
 * by regenerating defaults instead of propagating the failure to the player's next startup.
 *
 * <p>Each test writes raw bytes via {@link ObjectOutputStream} straight to {@link
 * GameConstants#ACHIEVEMENTS_FILE}. The local save file is preserved by {@link
 * #backupAndClearSaveFile()} and restored by {@link #restoreSaveFile()}.
 */
public class AchievementRecoveryTest {

  private List<Achievement> saveFileBackupSnapshot;

  /**
   * Backs up the current on-disk achievement state so that it can be restored later and clears the
   * file so each test starts from a known baseline
   */
  @BeforeEach
  void backupAndClearSaveFile() {
    AchievementManager manager = new AchievementManager();
    saveFileBackupSnapshot = manager.loadAchievement();
    manager.saveAchievement(new ArrayList<>());

    // Fixes the Windows OS handle lock so the test can safely delete/manipulate the file later on
    System.gc();
  }

  /** Restores the state after the tests have run */
  @AfterEach
  void restoreSaveFile() {
    new AchievementManager().saveAchievement(saveFileBackupSnapshot);
  }

  /**
   * Writes {@code payload} to the achievements file via {@link ObjectOutputStream}.
   *
   * @param payload any {@link java.io.Serializable} (The test intentionally passes bad types)
   * @throws IOException if output operation failed or was interrupted
   */
  private static void writeRawObject(Object payload) throws IOException {
    try (ObjectOutputStream out =
        new ObjectOutputStream(new FileOutputStream(GameConstants.ACHIEVEMENTS_FILE))) {
      out.writeObject(payload);
    }
  }

  private static Stream<Arguments> provideIncompatiblePayloads() {
    return Stream.of(
        Arguments.of("Not a list of achievements"),
        Arguments.of(1234),
        Arguments.of(List.of("a", "b", "c")));
  }

  /**
   * A save file whose serialized object is not a list of achievements must be rejected cleanly.
   *
   * <p>Pre-fix: the unchecked {@code (List<Achievement>)} cast in {@link
   * AchievementManager#loadAchievement()} lets the call succeed and the failure will surface as a
   * {@code ClassCastException} inside {@link AchievementManager#init()}.
   *
   * @param incompatiblePayload the incompatible object written to the raw save file
   * @throws IOException if I/O operation failed or was interrupted
   */
  @ParameterizedTest
  @MethodSource("provideIncompatiblePayloads")
  void loadAchievementReturnsEmptyListWhenFileContainsIncompatiblePayload(
      Object incompatiblePayload) throws IOException {
    writeRawObject(incompatiblePayload);

    List<Achievement> loadedAchievements = new AchievementManager().loadAchievement();

    assertNotNull(loadedAchievements, "loadAchievement must never return null.");
    assertTrue(
        loadedAchievements.isEmpty(),
        "Incompatible payload [" + incompatiblePayload + "] must produce an empty list.");
  }

  /**
   * A zero-byte file has no {@link ObjectOutputStream} header, so the internal {@link
   * java.io.ObjectInputStream} throws {@link java.io.EOFException}. The {@link
   * AchievementManager#loadAchievement()} method must swallow it and return an empty list.
   *
   * @throws IOException if I/O operation failed or was interrupted
   */
  @Test
  void loadAchievementReturnsEmptyListWhenFileIsEmpty() throws IOException {
    Files.write(Path.of(GameConstants.ACHIEVEMENTS_FILE), new byte[0]);

    List<Achievement> loadedAchievements = new AchievementManager().loadAchievement();

    assertTrue(loadedAchievements.isEmpty());
  }

  /**
   * On first launch, there is no save file on disk. Thus, the internal {@link
   * java.io.FileNotFoundException} must be swallowed and an empty list must be returned.
   *
   * @throws IOException if I/O operation failed or was interrupted
   */
  @Test
  void loadAchievementReturnsEmptyListWhenFileDoesNotExist() throws IOException {
    Files.deleteIfExists(Path.of(GameConstants.ACHIEVEMENTS_FILE));

    List<Achievement> loadedAchievements = new AchievementManager().loadAchievement();

    assertTrue(loadedAchievements.isEmpty());
  }

  /**
   * With a corrupt file on disk, {@link AchievementManager#init()} must:
   *
   * <ol>
   *   <li>Fail to load the corrupt payload -> {@link AchievementManager#loadAchievement()} returns
   *       an empty list
   *   <li>Take the "No persisted state" branch of {@link AchievementManager#init()}
   *   <li>Populate {@code activeAchievements} with the default catalogue ({@link
   *       RegisterAchievement} annotations from {@code ACHIEVEMENT_CLASSES})
   *   <li>Persist the default catalogue on local disk
   * </ol>
   *
   * @throws IOException if I/O operation failed or was interrupted
   */
  @Test
  void initSelfHealsAfterCorruptedFile() throws IOException {
    writeRawObject("corrupted");

    AchievementManager manager = new AchievementManager();
    manager.init();

    assertEquals(
        10,
        manager.getActiveAchievements().size(),
        "defaults must be regenerated after corrupt file");

    assertEquals(
        10, manager.getAllAchievements().size(), "defaults must be regenerated after corrupt file");

    assertFalse(manager.loadAchievement().isEmpty(), "init() must have re-written a valid file");
  }
}
