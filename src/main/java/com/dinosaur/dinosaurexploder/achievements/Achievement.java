/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;

public abstract class Achievement {

  protected boolean completed = false;

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public abstract String getId();

  public abstract String getDescription();

  public abstract void update(double tpf);

  public abstract void onDinosaurKilled();
}
