package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import static com.almasb.fxgl.dsl.FXGL.getInput;

public class PlayerRotationComponent extends Component {

    @Override
    public void onUpdate(double tpf) {
        // Get mouse position in the game world
        Point2D mouse = getInput().getMousePositionWorld();
        Point2D playerCenter = entity.getCenter();

        // Calculate angle
        double dx = mouse.getX() - playerCenter.getX();
        double dy = mouse.getY() - playerCenter.getY();
        double angle = Math.atan2(dy, dx);

        // Convert and set rotation (+90 to align with typical 'up' facing sprites)
        entity.setRotation(Math.toDegrees(angle) + 90);
    }
}