/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;

import com.dinosaur.dinosaurexploder.constants.GameConstants;
import java.io.*;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AchievementManager {
  private static final String CLASS_EXTENSION = ".class";
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

  // Finds annotated classes, sorts them for deterministic order, and builds the catalog.
  @SuppressWarnings("unchecked")
  private static AchievementCatalog buildCatalogFromAnnotations() {
    List<Class<? extends Achievement>> classes = findAnnotatedClasses();
    classes.sort(Comparator.comparing(Class::getSimpleName));
    return AchievementCatalog.of(buildFactories(classes).toArray(Supplier[]::new));
  }

  // Converts a sorted list of annotated classes into one factory per @RegisterAchievement entry.
  private static List<Supplier<Achievement>> buildFactories(
      List<Class<? extends Achievement>> classes) {
    List<Supplier<Achievement>> factories = new ArrayList<>();
    for (Class<? extends Achievement> clazz : classes) {
      for (RegisterAchievement cfg : clazz.getAnnotationsByType(RegisterAchievement.class)) {
        factories.add(() -> instantiate(clazz, cfg));
      }
    }
    return factories;
  }

  // Scans the classpath for concrete Achievement subclasses in this package that carry
  // @RegisterAchievement. Works with both exploded-directory and fat-JAR layouts.
  private static List<Class<? extends Achievement>> findAnnotatedClasses() {
    String packageName = AchievementManager.class.getPackageName();
    String packagePath = packageName.replace('.', '/');
    List<Class<? extends Achievement>> result = new ArrayList<>();

    ClassLoader cl = AchievementManager.class.getClassLoader();
    if (cl == null) {
      cl = ClassLoader.getSystemClassLoader();
    }

    try {
      Enumeration<URL> resources = cl.getResources(packagePath);
      while (resources.hasMoreElements()) {
        collectClasses(resources.nextElement(), packageName, cl, result);
      }
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Achievement auto-registration scan failed: {0}", e.getMessage());
    }
    return result;
  }

  private static void collectClasses(
      URL resource, String packageName, ClassLoader cl, List<Class<? extends Achievement>> result) {

    if ("file".equals(resource.getProtocol())) {
      collectFromDirectory(resource, packageName, cl, result);
    } else if ("jar".equals(resource.getProtocol())) {
      collectFromJar(resource, packageName, cl, result);
    }
  }

  private static void collectFromDirectory(
      URL resource, String packageName, ClassLoader cl, List<Class<? extends Achievement>> result) {

    File dir;
    try {
      dir = new File(resource.toURI());
    } catch (URISyntaxException e) {
      dir = new File(resource.getPath());
    }

    File[] files =
        dir.listFiles(
            f -> f.isFile() && f.getName().endsWith(CLASS_EXTENSION) && !f.getName().contains("$"));
    if (files == null) {
      return;
    }

    for (File file : files) {
      loadIfAnnotated(packageName + "." + file.getName().replace(CLASS_EXTENSION, ""), cl, result);
    }
  }

  private static void collectFromJar(
      URL resource, String packageName, ClassLoader cl, List<Class<? extends Achievement>> result) {

    String prefix = packageName.replace('.', '/') + "/";
    try {
      JarURLConnection conn = (JarURLConnection) resource.openConnection();
      try (JarFile jar = conn.getJarFile()) {
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
          String name = entries.nextElement().getName();
          if (name.startsWith(prefix) && name.endsWith(CLASS_EXTENSION) && !name.contains("$")) {
            loadIfAnnotated(name.replace('/', '.').replace(CLASS_EXTENSION, ""), cl, result);
          }
        }
      }
    } catch (IOException e) {
      LOGGER.log(
          Level.WARNING, "Failed to scan JAR {0}: {1}", new Object[] {resource, e.getMessage()});
    }
  }

  private static void loadIfAnnotated(
      String className, ClassLoader cl, List<Class<? extends Achievement>> result) {

    try {
      Class<?> clazz = Class.forName(className, false, cl);
      if (Achievement.class.isAssignableFrom(clazz)
          && !Modifier.isAbstract(clazz.getModifiers())
          && clazz.getAnnotationsByType(RegisterAchievement.class).length > 0) {
        result.add(clazz.asSubclass(Achievement.class));
      }
    } catch (ClassNotFoundException e) {
      LOGGER.log(
          Level.WARNING, "Could not load class {0}: {1}", new Object[] {className, e.getMessage()});
    }
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
