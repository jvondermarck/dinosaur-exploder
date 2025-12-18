package com.dinosaur.dinosaurexploder.utils;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getDialogService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.dinosaur.dinosaurexploder.constants.GameConstants;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.util.Objects;

public class MenuHelper {
    public static void showLockedDialog(String message) {
        LanguageManager lm = LanguageManager.getInstance();
        Button okButton = getUIFactoryService().newButton(lm.getTranslation("ok"));
        okButton.setMinWidth(250);
        okButton.setPrefWidth(300);

        var textNode = getUIFactoryService().newText(message, Color.LIME, GameConstants.TEXT_SUB_DETAILS);
        textNode.setWrappingWidth(450);
        textNode.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        textNode.setLineSpacing(10);

        VBox content = new VBox(textNode);
        content.setAlignment(Pos.CENTER);
        content.setSpacing(25);
        content.setPadding(new javafx.geometry.Insets(20));
        content.setMinWidth(500);

        getDialogService().showBox(lm.getTranslation("locked"), content, okButton);
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
        Image lockImage = new Image(Objects.requireNonNull(
                MenuHelper.class.getResourceAsStream("/assets/textures/lock.png")));

        ImageView lockIcon = new ImageView(lockImage);
        lockIcon.setFitWidth(30);
        lockIcon.setFitHeight(30);
        lockIcon.setMouseTransparent(true);
        lockIcon.setOpacity(0.6);
        lockIcon.setVisible(isLocked);
        return lockIcon;
    }
}