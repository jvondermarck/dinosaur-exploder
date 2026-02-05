package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.achievements.Achievement;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.*;

/// This class is responsible for getting and saving the achievement in the achievements
/// .properties file

public class AchievementProvider {

	public static final String ACHIVEMENT_FILE = "achievements.properties";


	public static Properties loadProperties() {
		Properties properties = new Properties();

		try {
			FileInputStream in = new FileInputStream(ACHIVEMENT_FILE);
			properties.load(in);
			in.close();
		} catch (Exception ex) {
			return null;

		}
		return properties;
	}


	public static void loadAchievements(List<Achievement> allAchievements) {
		List<Achievement> achievementsNotDone = new ArrayList<Achievement>();
		Properties properties = loadProperties();
		assert properties != null;
		properties.forEach((AchivementFilename, IsComplete)->{
			for (Achievement achievement : allAchievements) {
				if (achievement.getFileName() == AchivementFilename){
					achievementsNotDone.add(achievement);
				}
			}
		});
		for (Achievement achivement : achievementsNotDone) {
			System.out.println("achievement :" + achivement.getFileName());
		}
	}




	public static void saveAchivement(Achievement achievement) {
		Properties properties = loadProperties();
		if (properties.contains(achievement.getFileName()))
		{
			properties.setProperty(achievement.getFileName(), String.valueOf(achievement.isCompleted()));
		}else {
			properties.put(achievement.getFileName(),String.valueOf(achievement.isCompleted()));
		}

		try (FileWriter writer = new FileWriter(ACHIVEMENT_FILE)) {
			properties.store(writer, "store properties");
		} catch (Exception ex) {
			System.err.println("Error saving settings " + ex.getMessage());
		}
	}
}
