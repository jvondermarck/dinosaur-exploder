package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.component.Component;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class WeaponHeatComponent extends Component {
    private static final double MAX_WIDTH = 100;
    private static final double SLOWDOWN_THRESHOLD = 90.0;

    private final Rectangle fill;
    private final PlayerComponent playerComponent;

    public WeaponHeatComponent(Rectangle fill, PlayerComponent playerComponent) {
        this.fill = fill;
        this.playerComponent = playerComponent;
    }

    @Override
    public void onUpdate(double tpf) {
        double heatPercent = playerComponent.getWeaponHeatPercentage();
        fill.setWidth((heatPercent / 100.0) * MAX_WIDTH);
        fill.setFill(getColorForHeat(heatPercent));
    }

    private Color getColorForHeat(double percent) {
        if (percent >= SLOWDOWN_THRESHOLD) return Color.RED;
        if (percent >= 75) return Color.ORANGE;
        if (percent >= 50) return Color.YELLOW;
        return Color.LIMEGREEN;
    }
}