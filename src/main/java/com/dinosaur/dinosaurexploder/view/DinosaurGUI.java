package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import org.jetbrains.annotations.NotNull;

public class DinosaurGUI {
    public static final int WIDTH = 550;
    public static final int HEIGHT = 750;
    public static final String GAME_FONT = "Geologica-Regular.ttf";

    public void initSettings(GameSettings settings) {
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setMainMenuEnabled(true);

        // Custom main menu
        settings.setSceneFactory(new SceneFactory() {
            @NotNull
            @Override
            public FXGLMenu newMainMenu() {
                return new DinosaurMenu();
            }

            @NotNull
            @Override
            public FXGLMenu newGameMenu() {
                return new PauseMenu();
            }

            
        });

        settings.setVersion(GameConstants.VERSION);
        settings.setTicksPerSecond(60); // check info : settings.setProfilingEnabled(true);
        settings.setFontText(GAME_FONT);
        settings.setFontGame(GAME_FONT);
        settings.setFontMono(GAME_FONT);
        settings.setFontUI(GAME_FONT);
    }
}