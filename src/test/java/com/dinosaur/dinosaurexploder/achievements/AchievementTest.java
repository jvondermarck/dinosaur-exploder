package com.dinosaur.dinosaurexploder.achievements;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class AchievementTest {

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

	@Test
	void addAchievementInAlreadyExistingAchievements() {
		List<Achievement> listStartAchievement = achievementManager.loadAchievement();
		int numberOfStartAchievement = listStartAchievement.size();

		List<Achievement> listAchievementsGreater = new ArrayList<>();
		for (int i = 1; i < listStartAchievement.size() + 2; i++) {
			listAchievementsGreater.add(new KillCountAchievement(i, i));
		}
		mockInitFromAchievementManager(listAchievementsGreater, listStartAchievement);
		assert numberOfStartAchievement < achievementManager.loadAchievement().size();
	}

	private void mockInitFromAchievementManager(List<Achievement> allAchievements, List<Achievement> activeAchievements) {

		if (activeAchievements.isEmpty()) {
			achievementManager.saveAchievement(allAchievements);
			activeAchievements.addAll(allAchievements);
		}
		if (allAchievements.size() > activeAchievements.size()) {

			Set<String> activeDescriptions = activeAchievements.stream()
					.map(Achievement::getDescription)
					.collect(Collectors.toSet());

			List<Achievement> toAdd = allAchievements.stream()
					.filter(a -> !activeDescriptions.contains(a.getDescription()))
					.toList();

			activeAchievements.addAll(toAdd);
			achievementManager.saveAchievement(activeAchievements);
		}
	}

	@AfterEach
	void setAchievementBack() {
		achievementManager.saveAchievement(currentAchievement);
	}
}
