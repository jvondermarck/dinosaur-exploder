package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.component.Component;

public class CoinComponent extends Component implements Coin {
    private static final double COIN_SPEED = 100.0; // Match with other game speeds if needed

    @Override
    public void onUpdate(double tpf) {
        // Move coin downward
        entity.translateY(COIN_SPEED * tpf);
    }
}