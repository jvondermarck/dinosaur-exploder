package com.dinosaur.dinosaurexploder.utils;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getDialogService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import java.io.InputStream;
import java.util.Objects;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class MenuHelper {
  public static void showDialog(String title, String message) {
    LanguageManager lm = LanguageManager.getInstance();
    Button okButton = getUIFactoryService().newButton(lm.getTranslation("ok"));
    okButton.setMinWidth(250);
    okButton.setPrefWidth(300);

    var textNode =
        getUIFactoryService().newText(message, Color.LIME, GameConstants.TEXT_SUB_DETAILS);
    textNode.setWrappingWidth(450);
    textNode.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
    textNode.setLineSpacing(10);

    VBox content = new VBox(textNode);
    content.setAlignment(Pos.CENTER);
    content.setSpacing(25);
    content.setPadding(new javafx.geometry.Insets(20));
    content.setMinWidth(500);

    getDialogService().showBox(title, content, okButton);
  }

  public static ImageView createItemImageView(Image image, double size, boolean isLocked) {
    ImageView imageView = new ImageView(image);
    imageView.setFitHeight(size);
    imageView.setFitWidth(size);

    if (isLocked) {
      ColorAdjust darkFilter = new ColorAdjust();
      darkFilter.setBrightness(-0.5);
      imageView.setEffect(darkFilter);
    }
    return imageView;
  }

  public static ImageView createLockIcon(boolean isLocked) {
    Image lockImage =
        new Image(
            Objects.requireNonNull(
                MenuHelper.class.getResourceAsStream("/assets/textures/lock.png")));

    ImageView lockIcon = new ImageView(lockImage);
    lockIcon.setFitWidth(30);
    lockIcon.setFitHeight(30);
    lockIcon.setMouseTransparent(true);
    lockIcon.setOpacity(0.6);
    lockIcon.setVisible(isLocked);
    return lockIcon;
  }

  public static TextFlow createTitleFlow(String text, double maxWidth) {
    var title =
        getUIFactoryService()
            .newText(text.toUpperCase(), Color.LIME, FontType.MONO, GameConstants.MAIN_TITLES);
    TextFlow titleFlow = new TextFlow(title);
    titleFlow.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
    titleFlow.setMaxWidth(maxWidth);
    titleFlow.setLineSpacing(5);
    return titleFlow;
  }

  public static ImageView createAnimatedBackground(double appWidth, double appHeight) {
    InputStream stream =
        MenuHelper.class.getClassLoader().getResourceAsStream(GameConstants.BACKGROUND_IMAGE_PATH);
    Image image = new Image(stream);
    ImageView view = new ImageView(image);
    view.setFitHeight(appHeight);
    view.setPreserveRatio(true);

    TranslateTransition tt = new TranslateTransition(Duration.seconds(50), view);
    tt.setFromX(0);
    tt.setToX(-image.getWidth() + appWidth * 3.8);
    tt.setCycleCount(TranslateTransition.INDEFINITE);
    tt.setInterpolator(javafx.animation.Interpolator.LINEAR);
    tt.setAutoReverse(true);
    tt.play();
    return view;
  }

  public static void setupSelectionMenu(
      com.almasb.fxgl.app.scene.FXGLMenu menu,
      javafx.scene.Node header,
      javafx.scene.Node grid,
      javafx.scene.Node backButton,
      double spacing,
      double menuAppWidth,
      double menuAppHeight) {
    javafx.scene.image.ImageView background = createAnimatedBackground(menuAppWidth, menuAppHeight);

    javafx.scene.layout.VBox layout =
        new javafx.scene.layout.VBox(spacing, header, grid, backButton);
    layout.setAlignment(javafx.geometry.Pos.CENTER);

    javafx.scene.layout.StackPane container = new javafx.scene.layout.StackPane(layout);
    container.setPrefSize(menuAppWidth, menuAppHeight);
    container.setAlignment(javafx.geometry.Pos.CENTER);

    menu.getContentRoot().getChildren().addAll(background, container);
  }

  public static Text createSubtitle(String text, double wrapWidth, boolean animated) {
    Text subtitle =
        getUIFactoryService()
            .newText(text, Color.rgb(0, 255, 100), FontType.MONO, GameConstants.TEXT_SUB_DETAILS);

    subtitle.setWrappingWidth(wrapWidth);
    subtitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

    DropShadow glow = new DropShadow();
    glow.setColor(Color.rgb(0, 255, 100));
    glow.setRadius(6);
    glow.setSpread(0.25);
    subtitle.setEffect(glow);

    if (animated) {
      FadeTransition pulse = new FadeTransition(Duration.seconds(2.2), subtitle);
      pulse.setFromValue(0.8);
      pulse.setToValue(1.0);
      pulse.setCycleCount(FadeTransition.INDEFINITE);
      pulse.setAutoReverse(true);
      pulse.setInterpolator(Interpolator.EASE_BOTH);
      pulse.play();
    }

    return subtitle;
  }

  public static Button createStyledButton(String text) {
    Button button = new Button(text.toUpperCase());
    button.setMinSize(140, 60);
    applyButtonStyle(button);
    return button;
  }

  public static void applyButtonStyle(Button button) {
    button
        .getStylesheets()
        .add(
            Objects.requireNonNull(MenuHelper.class.getResource(GameConstants.STYLESHEET_PATH))
                .toExternalForm());
  }
}
