/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an {@link Achievement} subclass for automatic registration by {@link AchievementManager}.
 *
 * <p>Place one annotation per desired instance on the class. Each annotation produces one entry in
 * the achievement list on startup.
 *
 * <p>Instantiation strategy (in order):
 *
 * <ol>
 *   <li>Two-arg constructor {@code (int target, int reward)} — used when {@link #target()} is
 *       provided.
 *   <li>One-arg constructor {@code (int reward)} — fallback for achievements with no target
 *       parameter.
 * </ol>
 */
@Repeatable(RegisterAchievements.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegisterAchievement {

  /** Target value (kills, score, coins, minutes). Ignored when no target parameter exists. */
  int target() default 0;

  /** Coins awarded when the achievement is completed. */
  int reward();
}
