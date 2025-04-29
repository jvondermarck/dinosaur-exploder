package com.dinosaur.dinosaurexploder.controller;

import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.components.HealthbarComponent;
import com.dinosaur.dinosaurexploder.components.RedDinoComponent;
import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.utils.LevelManager;

import static com.almasb.fxgl.dsl.FXGL.getAppCenter;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class BossSpawner {

    private final Settings settings;
    private final LevelManager levelManager;
    private Entity healthBar;
    private Entity redDino;

    public BossSpawner(Settings settings, LevelManager levelManager){
        this.settings = settings;
        this.levelManager = levelManager;
    }

    public void spawnNewBoss(){
        redDino = spawn("redDino", getAppCenter().getX() - 45, 50);
        redDino.getComponent(RedDinoComponent.class).setMuted(settings.isMuted());
        redDino.getComponent(RedDinoComponent.class).setLevelManager(levelManager);

        healthBar = spawn("healthBar",  getAppWidth()-215, 15);
        healthBar.getComponent(HealthbarComponent.class).setRedDinoComponent(redDino.getComponent(RedDinoComponent.class));
    }

    public void updateHealthBar(){
        healthBar.getComponent(HealthbarComponent.class).updateBar();
    }

    public void removeBossEntities(){
        healthBar.removeFromWorld();
        redDino.removeFromWorld();
    }
}
