package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class PauseMenu extends FXGLMenu {

  LanguageManager languageManager = LanguageManager.getInstance();
  PauseButton btnBack = new PauseButton(languageManager.getTranslation("back"), this::fireResume);
  PauseButton btnQuitGame = new PauseButton(languageManager.getTranslation("quit"), this::exit);
  ControlButton btnControls = new ControlButton(languageManager.getTranslation("controls"));

  // Options buttons (controls list)
  OptionsButton btnMoveUp =
      new OptionsButton("↑ / W : " + languageManager.getTranslation("move_up"));
  OptionsButton btnMoveDown =
      new OptionsButton("↓ / S :  " + languageManager.getTranslation("move_down"));
  OptionsButton btnMoveRight =
      new OptionsButton("→ / D : " + languageManager.getTranslation("move_right"));
  OptionsButton btnMoveLeft =
      new OptionsButton("← / A : " + languageManager.getTranslation("move_left"));
  OptionsButton btnPauseGame = new OptionsButton(languageManager.getTranslation("pause_game"));
  OptionsButton btnShoot = new OptionsButton(languageManager.getTranslation("shoot"));
  OptionsButton btnBomb = new OptionsButton("B: " + languageManager.getTranslation("bomb"));
  OptionsButton btnShield = new OptionsButton("E: " + languageManager.getTranslation("shield"));

  public PauseMenu() {
    super(MenuType.GAME_MENU);

    updateTexts();

    // Auto-update UI when language changes
    languageManager
        .selectedLanguageProperty()
        .addListener((obs, oldValue, newValue) -> updateTexts());

    // Controls button action → Opens controls overlay
    btnControls.setControlAction(
        () -> {
          // 1. Fond sombre pour le sous-menu
          var controlsBg = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.85));

          // 2. VBox pour aligner les touches verticalement
          var controlsBox = new VBox(10);
          controlsBox.setAlignment(Pos.CENTER);
          controlsBox.setMaxWidth(getAppWidth() * 0.7);

          // 3. Conteneur global pour centrer la box (C'est lui qu'on affichera/supprimera)
          StackPane controlsContainer = new StackPane(controlsBox);
          controlsContainer.setPrefSize(getAppWidth(), getAppHeight());
          controlsContainer.setAlignment(Pos.CENTER);

          // 4. Bouton de retour spécifique au menu contrôles
          PauseButton btnBackFromControls =
              new PauseButton(
                  languageManager.getTranslation("back"),
                  () -> {
                    // ✅ On retire les deux éléments que l'on a ajouté au Root
                    getContentRoot().getChildren().removeAll(controlsBg, controlsContainer);

                    // Réactive le menu principal
                    btnBack.enable();
                    btnQuitGame.enable();
                    btnControls.enable();
                  });

          VBox.setMargin(btnBackFromControls, new Insets(0, 0, 40, 0));

          // 5. Ajout des boutons et textes dans la boîte
          controlsBox
              .getChildren()
              .addAll(
                  btnBackFromControls,
                  btnMoveUp,
                  btnMoveDown,
                  btnMoveRight,
                  btnMoveLeft,
                  btnPauseGame,
                  btnShoot,
                  btnBomb,
                  btnShield);

          // Désactivation temporaire des boutons en arrière-plan
          btnBack.disable();
          btnQuitGame.disable();
          btnControls.disable();

          // ✅ Ajout à l'affichage (le container est par-dessus le reste)
          getContentRoot().getChildren().addAll(controlsBg, controlsContainer);
        });

    // --- MISE EN PAGE DU MENU PRINCIPAL ---

    // Background principal
    var bg = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.8));

    // Titre centré
    var title =
        FXGL.getUIFactoryService()
            .newText(
                GameConstants.GAME_NAME, Color.WHITE, FontType.MONO, GameConstants.MAIN_TITLES);

    javafx.scene.text.TextFlow titleFlow = new javafx.scene.text.TextFlow(title);
    titleFlow.setTextAlignment(TextAlignment.CENTER);
    titleFlow.setMaxWidth(getAppWidth());

    StackPane titleContainer = new StackPane(titleFlow);
    titleContainer.setPrefWidth(getAppWidth());
    titleContainer.setTranslateY(100);

    // Menu principal (les 3 boutons)
    var box = new VBox(15, btnBack, btnControls, btnQuitGame);
    box.setAlignment(Pos.CENTER);

    StackPane buttonContainer = new StackPane(box);
    buttonContainer.setPrefWidth(getAppWidth());
    buttonContainer.setTranslateY(getAppHeight() / 2.0 - 50);

    // Version
    var version =
        FXGL.getUIFactoryService()
            .newText(
                GameConstants.VERSION, Color.WHITE, FontType.MONO, GameConstants.TEXT_SUB_DETAILS);
    version.setTranslateX(10);
    version.setTranslateY(getAppHeight() - 10.0);

    // Assemblage final
    getContentRoot().getChildren().addAll(bg, titleContainer, version, buttonContainer);
  }

  // ------------------- BUTTON CLASSES -----------------------

  private static class OptionsButton extends StackPane {
    private final Text text;

    public OptionsButton(String desc) {
      text = getUIFactoryService().newText(desc, Color.WHITE, GameConstants.TEXT_SUB_DETAILS);

      // ✅ Wrapping avec largeur réduite
      text.setWrappingWidth(350);
      text.setTextAlignment(TextAlignment.CENTER);

      setAlignment(Pos.CENTER); // ✅ Centré
      getChildren().add(text);
    }

    public void setText(String newText) {
      text.setText(newText);
    }

    public Text getTextNode() {
      return text;
    }
  }

  private static class PauseButton extends StackPane {
    private static final Color SELECTED = Color.WHITE;
    private static final Color NOT_SELECTED = Color.GRAY;

    private Runnable action;
    private boolean disabled = false;
    private final Text text;

    public PauseButton(String name, Runnable action) {
      this.action = action;
      text = getUIFactoryService().newText(name, Color.WHITE, GameConstants.TEXT_SUB_DETAILS);

      text.strokeProperty()
          .bind(Bindings.when(focusedProperty()).then(SELECTED).otherwise(NOT_SELECTED));
      text.setStrokeWidth(0.5);

      setAlignment(Pos.CENTER); // ✅ Centré au lieu de CENTER_LEFT
      setFocusTraversable(true);

      setOnKeyPressed(
          e -> {
            if (e.getCode() == KeyCode.ENTER && !disabled) action.run();
          });

      setOnMouseClicked(
          e -> {
            if (!disabled) action.run();
          });

      setOnMouseEntered(e -> text.setFill(Color.RED));
      setOnMouseExited(e -> text.setFill(SELECTED));

      getChildren().add(text);
    }

    public void disable() {
      disabled = true;
    }

    public void enable() {
      disabled = false;
    }

    public void setText(String newText) {
      text.setText(newText);
    }

    public Text getTextNode() {
      return text;
    }
  }

  private static class ControlButton extends PauseButton {
    private Runnable action;

    public ControlButton(String name) {
      super(name, null);
    }

    public void setControlAction(Runnable action) {
      this.action = action;
      setOnMouseClicked(
          e -> {
            if (!super.disabled) action.run();
          });
    }
  }

  // ------------------- UPDATE TEXT FOR LANG CHANGE -----------------------

  private void updateTexts() {
    btnBack.setText(languageManager.getTranslation("back").toUpperCase());
    btnQuitGame.setText(languageManager.getTranslation("quit").toUpperCase());
    btnControls.setText(languageManager.getTranslation("controls").toUpperCase());

    btnMoveUp.setText("↑ / W : " + languageManager.getTranslation("move_up"));
    btnMoveDown.setText("↓ / S : " + languageManager.getTranslation("move_down"));
    btnMoveRight.setText("→ / D : " + languageManager.getTranslation("move_right"));
    btnMoveLeft.setText("← / A : " + languageManager.getTranslation("move_left"));
    btnPauseGame.setText(languageManager.getTranslation("pause_game"));
    btnShoot.setText(languageManager.getTranslation("shoot"));
    btnBomb.setText("B: " + languageManager.getTranslation("bomb"));
    btnShield.setText("E: " + languageManager.getTranslation("shield"));
  }

  // ------------------- QUIT POPUP -----------------------

  public void exit() {
    Button btnYes = getUIFactoryService().newButton(languageManager.getTranslation("yes"));
    btnYes.setPrefWidth(200);
    btnYes.setOnAction(e -> getGameController().gotoMainMenu());

    Button btnNo = getUIFactoryService().newButton(languageManager.getTranslation("no"));
    btnNo.setPrefWidth(200);
    btnNo.setOnAction(e -> getGameController().resumeEngine());

    getDialogService()
        .showBox(languageManager.getTranslation("quit_game"), new VBox(), btnYes, btnNo);
  }
}
