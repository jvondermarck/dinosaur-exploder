/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;

import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/** UI component that updates a Text node to show Shield status (READY / cooldown / time left). */
public class ShieldUIComponent extends Component {

  private final Text text;
  private final LanguageManager languageManager = LanguageManager.getInstance();
  private PlayerComponent playerComp;

  public ShieldUIComponent(Text text) {
    this.text = text;
  }

  @Override
  public void onAdded() {
    // find player component
    var playerEntity = getGameWorld().getEntitiesByComponent(PlayerComponent.class).getFirst();
    playerComp = playerEntity.getComponent(PlayerComponent.class);
  }

  @Override
  public void onUpdate(double tpf) {
    if (playerComp == null) return;

    if (playerComp.isShieldActive()) {
      text.setFill(Color.AQUA);
      text.setText(
          String.format(
                  "%s: %.1fs",
                  languageManager.getTranslation(GameConstants.SHIELD),
                  playerComp.getShieldTimeLeft())
              .toUpperCase());
    } else if (playerComp.getShieldCooldownLeft() > 0) {
      text.setFill(Color.ORANGE);
      text.setText(
          String.format(
                  "%s: CD %.1fs",
                  languageManager.getTranslation(GameConstants.SHIELD),
                  playerComp.getShieldCooldownLeft())
              .toUpperCase());
    } else {
      text.setFill(Color.LIME);
      text.setText(
          String.format(
                  "%s: %s",
                  languageManager.getTranslation(GameConstants.SHIELD),
                  languageManager.getTranslation("ready"))
              .toUpperCase());
    }
  }
}
