package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.component.Component;

/**
 * Summary :
 * This handles the behaviour of dropped hearts in the game, and extends the Component class
 */
public class Heart extends Component {
    private static final double HEART_SPEED = 100.0; // Match with other game speeds if needed

    /**
     * Summary :
     *  This method runs for every frame like a continues flow, and move the heart downward
     */
    @Override
    public void onUpdate(double tpf) {
        entity.translateY(HEART_SPEED * tpf);
    }
}