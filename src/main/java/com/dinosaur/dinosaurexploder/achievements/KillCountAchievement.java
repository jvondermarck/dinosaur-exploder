/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;


public class KillCountAchievement extends Achievement {

	private final int targetKills;
	private int currentKills = 0;

	public KillCountAchievement(int targetKills, int rewardCoins) {
		this.targetKills = targetKills;
		this.rewardCoins = rewardCoins;
		description = "Kill " + targetKills + " dinosaurs";
	}

	public void onDinosaurKilled() {
		if (completed) return;

		currentKills++;

		if (currentKills >= targetKills) {
			completed = true;
			onComplete();
		}
	}

	@Override
	public void update(double tpf) {
		// Not needed for count-based achievement
	}


}
