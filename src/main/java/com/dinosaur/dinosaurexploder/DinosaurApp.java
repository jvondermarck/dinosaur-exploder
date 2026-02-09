package com.dinosaur.dinosaurexploder;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.dinosaur.dinosaurexploder.achievements.AchievementManager;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.controller.DinosaurController;
import com.dinosaur.dinosaurexploder.model.GameEntityFactory;
import com.dinosaur.dinosaurexploder.view.DinosaurGUI;

/**
 * Summary : The Factory handles the DinosaurApp,Physics,Settings and Input of all entities in the
 * game
 */
public class DinosaurApp extends GameApplication {
  DinosaurGUI gui = new DinosaurGUI();
  DinosaurController controller = new DinosaurController();
  private AchievementManager achievementManager;

  /** Summary : This method for the setting the Game GUI Parameters : GameSettings */
  @Override
  protected void initSettings(GameSettings settings) {
    gui.initSettings(settings);
    settings.setAppIcon(GameConstants.GAME_ICON_DINOSAUR);
    settings.setTitle(GameConstants.GAME_NAME);
  }

  /**
   * Summary : This method is overriding the superclass method to EventHandling for the keyboard
   * events
   */
  @Override
  protected void initInput() {
    // If the key pressed is the up arrow key, then call move up from the Player
    // Component etc...
    controller.initInput();
  }

  /** Summary : This method is overriding the superclass method to initialize the game */
  @Override
  protected void initGame() {
    FXGL.getGameWorld().addEntityFactory(new GameEntityFactory());
    achievementManager = new AchievementManager();
    achievementManager.init();
    FXGL.getWorldProperties().setValue("achievementManager", achievementManager);
    controller.initGame(achievementManager);
  }

  /**
   * Summary : This method is overriding the superclass method to initialize the physics to the game
   */
  @Override
  protected void initPhysics() {
    controller.initPhysics();
  }

  @Override
  protected void onUpdate(double tpf) {
    if (achievementManager != null) {
      achievementManager.update(tpf);
    }
  }

  /**
   * Summary : This method launches the game as it is the main method of the class Parameters :
   * Strings[]
   */
  public static void main(String[] args) {
    launch(args);
  }
}
