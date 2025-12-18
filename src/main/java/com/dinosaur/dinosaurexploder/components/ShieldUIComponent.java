package com.dinosaur.dinosaurexploder.components;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;

import com.almasb.fxgl.entity.component.Component;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/** UI component that updates a Text node to show Shield status (READY / cooldown / time left). */
public class ShieldUIComponent extends Component {

  private Text text;
  private PlayerComponent playerComp;

  public ShieldUIComponent(Text text) {
    this.text = text;
  }

  @Override
  public void onAdded() {
    // find player component
    var playerEntity = getGameWorld().getEntitiesByComponent(PlayerComponent.class).get(0);
    playerComp = playerEntity.getComponent(PlayerComponent.class);
  }

  @Override
  public void onUpdate(double tpf) {
    if (playerComp == null) return;

    if (playerComp.isShieldActive()) {
      text.setFill(Color.AQUA);
      text.setText(String.format("Shield: %.1fs", playerComp.getShieldTimeLeft()).toUpperCase());
    } else if (playerComp.getShieldCooldownLeft() > 0) {
      text.setFill(Color.ORANGE);
      text.setText(
          String.format("Shield: CD %.1fs", playerComp.getShieldCooldownLeft()).toUpperCase());
    } else {
      text.setFill(Color.LIME);
      text.setText("Shield: READY".toUpperCase());
    }
  }
}
