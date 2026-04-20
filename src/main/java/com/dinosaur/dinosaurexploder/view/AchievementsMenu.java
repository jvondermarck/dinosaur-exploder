/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.dinosaur.dinosaurexploder.achievements.AchievementManager;
import com.dinosaur.dinosaurexploder.components.AchievementsComponent;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class AchievementsMenu extends FXGLMenu {

  private final LanguageManager languageManager = LanguageManager.getInstance();
  private final AchievementManager achievementManager = AchievementsComponent.loadAchievements();

  public AchievementsMenu() {
    super(MenuType.MAIN_MENU);
    buildMenu();
  }

  private void buildMenu() {
    var background = MenuHelper.createAnimatedBackground(getAppWidth(), getAppHeight());
    TextFlow title =
        MenuHelper.createTitleFlow(languageManager.getTranslation("achievements"), getAppWidth());
    Text subtitle =
        MenuHelper.createSubtitle(
            languageManager.getTranslation("achievements_subtitle"), getAppWidth() * 0.75, false);

    ScrollPane content = createContent();
    Button backButton = MenuHelper.createStyledButton(languageManager.getTranslation("back"));
    backButton.setOnAction(event -> fireResume());

    VBox layout = new VBox(20, title, subtitle, content, backButton);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(30, 40, 30, 40));
    layout.setPrefWidth(getAppWidth());
    layout.setPrefHeight(getAppHeight());

    getContentRoot().getChildren().addAll(background, layout);
  }

  private ScrollPane createContent() {
    return AchievementsComponent.createContent(achievementManager);
  }
}
