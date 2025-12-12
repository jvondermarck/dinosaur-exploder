package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.interfaces.Coin;

public class CoinComponent extends Component implements Coin {
  private static final double COIN_SPEED = 100.0;

  @Override
  public void onUpdate(double tpf) {
    // Move coin downward
    entity.translateY(COIN_SPEED * tpf);
  }
}
