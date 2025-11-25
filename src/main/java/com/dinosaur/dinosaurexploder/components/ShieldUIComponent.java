package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.components.PlayerComponent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;

/**
 * UI component that updates a Text node to show Shield status (READY / cooldown / time left).
 */
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
            text.setText(String.format("Shield: %.1fs", playerComp.getShieldTimeLeft()));
        } else if (playerComp.getShieldCooldownLeft() > 0) {
            text.setFill(Color.ORANGE);
            text.setText(String.format("Shield: CD %.1fs", playerComp.getShieldCooldownLeft()));
        } else {
            text.setFill(Color.LIME);
            text.setText("Shield: READY");
        }
    }
}
