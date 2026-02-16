package com.dinosaur.dinosaurexploder.achievements;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AchievementPersistence {

  private static final String FILE_NAME = "achievements.properties";

  public static void save(String achievementId) {
    Properties props = new Properties();
    try (FileInputStream in = new FileInputStream(FILE_NAME)) {
      props.load(in);
    } catch (IOException ignored) {
    }

    props.setProperty(achievementId, "true");

    try (FileOutputStream out = new FileOutputStream(FILE_NAME)) {
      props.store(out, "Unlocked Achievements");
      System.out.println("Saved achievement: " + achievementId);
    } catch (IOException e) {
      System.err.println("Failed to save achievement: " + achievementId + " - " + e.getMessage());
    }
  }

  public static boolean isUnlocked(String achievementId) {
    Properties props = new Properties();
    try (FileInputStream in = new FileInputStream(FILE_NAME)) {
      props.load(in);
      return "true".equals(props.getProperty(achievementId));
    } catch (IOException e) {
      return false;
    }
  }
}
