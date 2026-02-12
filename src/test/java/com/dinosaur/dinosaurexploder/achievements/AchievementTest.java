package com.dinosaur.dinosaurexploder.achievements;

import com.dinosaur.dinosaurexploder.utils.LevelManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class AchievementTest {

	private LevelManager levelManager;
	private List<Achievement> currentAchievement = new ArrayList<>();
	AchievementManager achievementManager = new AchievementManager();

	@BeforeEach
	void setUp() {

		List<Achievement> emptyList = new ArrayList<>();
		currentAchievement = achievementManager.loadAchievement();
		achievementManager.saveAchievement(emptyList);
		achievementManager.init();

	}

	@Test
	void achievementSaveInFile() {
		List<Achievement> listToCheck;

		achievementManager.getActiveAchievement().completed = true;
		achievementManager.saveAchievement(achievementManager.getActiveAchievements());
		listToCheck = achievementManager.loadAchievement();
		assert listToCheck.getFirst().isCompleted();
	}

	@AfterEach
	void setAchievementBack() {

		achievementManager.saveAchievement(currentAchievement);
	}
}
