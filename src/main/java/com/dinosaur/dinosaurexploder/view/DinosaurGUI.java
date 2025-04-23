package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.dinosaur.dinosaurexploder.model.GameConstants;

public class DinosaurGUI {
    public static final int WIDTH = 550;
    public static final int HEIGHT = 750;
    public static final String ARCADECLASSIC_FONT = "arcade_classic.ttf";

    public void initSettings(GameSettings settings) {
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setMainMenuEnabled(true);

        // Custom main menu
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new DinosaurMenu();
            }

            @Override
            public FXGLMenu newGameMenu() {
                return new PauseMenu();
            }

            
        });

        settings.setVersion(GameConstants.VERSION);
        settings.setTicksPerSecond(60); // check info : settings.setProfilingEnabled(true);
        settings.setFontText(ARCADECLASSIC_FONT);
        settings.setFontGame(ARCADECLASSIC_FONT);
        settings.setFontMono(ARCADECLASSIC_FONT);
    }
}