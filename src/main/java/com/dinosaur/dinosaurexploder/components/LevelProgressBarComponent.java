package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import javafx.scene.shape.Rectangle;

/**
 * Summary:
 * This class extends Component and handles filling the level progress bar
 */
public class LevelProgressBarComponent extends Component {
    private final Rectangle fill;
    private LevelManager levelManager;

    public LevelProgressBarComponent(Rectangle fill, LevelManager levelManager) {
        this.fill = fill;
        this.levelManager = levelManager;
    }

    @Override
    public void onUpdate(double tpf) {
        float progress = levelManager.getLevelProgress();
        fill.setWidth(148 * progress);
    }
}
