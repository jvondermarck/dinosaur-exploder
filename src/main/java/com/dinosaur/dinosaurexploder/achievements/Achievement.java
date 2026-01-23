package com.dinosaur.dinosaurexploder.achievements;

public abstract class Achievement {

  protected boolean completed = false;

  public boolean isCompleted() {
    return completed;
  }

  public abstract void update(double tpf);

  public abstract void onDinosaurKilled();
}
