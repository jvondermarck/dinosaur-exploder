/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;

import com.dinosaur.dinosaurexploder.constants.GameConstants;
import java.io.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AchievementManager {

  // Static list of all achievement classes. To add a new achievement, create the annotated class
  // and add it here.
  private static final List<Class<? extends Achievement>> ACHIEVEMENT_CLASSES =
      List.of(
          BossDefeatAchievement.class,
          CoinCollectionAchievement.class,
          KillCountAchievement.class,
          ScoreAchievement.class,
          SurvivalTimeAchievement.class);

  private final List<Achievement> allAchievements;
  private final List<Achievement> activeAchievements = new ArrayList<>();
  private static final Logger LOGGER = Logger.getLogger(AchievementManager.class.getName());

  /**
   * Default constructor. Discovers all {@link Achievement} subclasses in this package that carry a
   * {@link RegisterAchievement} annotation and registers one instance per annotation entry.
   */
  public AchievementManager() {
    this(buildCatalogFromAnnotations());
  }

  AchievementManager(AchievementCatalog catalog) {
    this.allAchievements = new ArrayList<>(catalog.createAchievements());
  }

  /** Called once when the game starts. Loads achievements from file or creates new ones. */
  public void init() {
    if (allAchievements.isEmpty()) {
      return;
    }

    activeAchievements.addAll(loadAchievement());
    if (activeAchievements.isEmpty()) {
      activeAchievements.addAll(allAchievements);
      saveAchievement(activeAchievements);
      return;
    }

    addMissingAchievements();
  }

  /**
   * Dispatches an event to all active achievements.
   *
   * <p>{@link AchievementEventType#TIME_ELAPSED} events are forwarded only to incomplete
   * achievements and do not trigger a save, avoiding per-frame I/O overhead. All other events are
   * forwarded to every active achievement and the updated state is persisted immediately.
   *
   * @param event the event to dispatch
   */
  public void dispatch(AchievementEvent event) {
    if (event.type() == AchievementEventType.TIME_ELAPSED) {
      for (Achievement achievement : activeAchievements) {
        if (!achievement.isCompleted()) {
          achievement.handleEvent(event);
        }
      }
    } else {
      dispatchAndSave(event);
    }
  }

  /** Advances time-based achievements by one frame. */
  public void update(double tpf) {
    dispatch(AchievementEvent.timeElapsed(tpf));
  }

  private void addMissingAchievements() {
    if (allAchievements.size() <= activeAchievements.size()) {
      return;
    }

    Set<String> activeDescriptions =
        activeAchievements.stream().map(Achievement::getDescription).collect(Collectors.toSet());

    List<Achievement> toAdd =
        allAchievements.stream()
            .filter(a -> !activeDescriptions.contains(a.getDescription()))
            .toList();

    if (toAdd.isEmpty()) {
      return;
    }

    activeAchievements.addAll(toAdd);
    saveAchievement(activeAchievements);
  }

  private void dispatchAndSave(AchievementEvent event) {
    for (Achievement achievement : activeAchievements) {
      achievement.handleEvent(event);
    }
    saveAchievement(activeAchievements);
  }

  /** Returns all achievements currently tracked in the active session. */
  public List<Achievement> getActiveAchievements() {
    return activeAchievements;
  }

  /** Returns every achievement registered at startup, regardless of completion status. */
  public List<Achievement> getAllAchievements() {
    return allAchievements;
  }

  /** Returns active achievements that have not yet been completed. */
  public List<Achievement> getPendingAchievements() {
    return activeAchievements.stream().filter(achievement -> !achievement.isCompleted()).toList();
  }

  /** Returns active achievements that have been completed. */
  public List<Achievement> getCompletedAchievements() {
    return activeAchievements.stream().filter(Achievement::isCompleted).toList();
  }

  /**
   * Returns the first active achievement, or {@code null} if there are none.
   *
   * @return the first active achievement, or {@code null}
   */
  public Achievement getActiveAchievement() {
    if (activeAchievements.isEmpty()) {
      return null;
    }
    return activeAchievements.getFirst();
  }

  /** Get the list of achievements saved in the achievement.ser file */
  public List<Achievement> loadAchievement() {
    List<Achievement> achievementFromFile = new ArrayList<>();
    try (ObjectInputStream in =
        new ObjectInputStream(new FileInputStream(GameConstants.ACHIEVEMENTS_FILE))) {
      achievementFromFile = (List<Achievement>) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      LOGGER.log(Level.INFO, "Failed to load achievements from file");
    }
    return achievementFromFile;
  }

  /** Save activeAchievement in the achievement.ser file */
  public void saveAchievement(List<Achievement> listToSave) {
    try (ObjectOutputStream out =
        new ObjectOutputStream(new FileOutputStream(GameConstants.ACHIEVEMENTS_FILE))) {
      out.writeObject(listToSave);
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Error saving achievement : {0}", e.getMessage());
    }
  }

  // ---------------------------------------------------------------------------
  // Annotation-based auto-registration
  // ---------------------------------------------------------------------------

  // Reads @RegisterAchievement annotations from ACHIEVEMENT_CLASSES and builds the catalog.
  @SuppressWarnings("unchecked")
  private static AchievementCatalog buildCatalogFromAnnotations() {
    List<Supplier<Achievement>> factories = new ArrayList<>();
    for (Class<? extends Achievement> clazz : ACHIEVEMENT_CLASSES) {
      for (RegisterAchievement cfg : clazz.getAnnotationsByType(RegisterAchievement.class)) {
        factories.add(() -> instantiate(clazz, cfg));
      }
    }
    return AchievementCatalog.of(factories.toArray(Supplier[]::new));
  }

  // Tries (int target, int reward) constructor first; falls back to (int reward) for
  // achievements without a target parameter (e.g. BossDefeatAchievement).
  private static Achievement instantiate(
      Class<? extends Achievement> clazz, RegisterAchievement cfg) {
    try {
      return clazz
          .getDeclaredConstructor(int.class, int.class)
          .newInstance(cfg.target(), cfg.reward());
    } catch (NoSuchMethodException e) {
      try {
        return clazz.getDeclaredConstructor(int.class).newInstance(cfg.reward());
      } catch (ReflectiveOperationException ex) {
        throw new IllegalStateException("No suitable constructor on " + clazz.getSimpleName(), ex);
      }
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Failed to create " + clazz.getSimpleName(), e);
    }
  }
}
