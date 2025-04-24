package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class CollectedCoinsComponent extends Component implements CollectedCoins {
    private int coin = 0;
    private final int COIN_VALUE = 1;


    private final LanguageManager languageManager = LanguageManager.getInstance();

    private Text coinText;
    private Node coinUI;

    @Override
    public void onAdded() {
        // Create UI elements
        coinText = new Text();
        coinText.setFill(Color.GOLDENROD);
        coinText.setFont(Font.font(GameConstants.ARCADE_CLASSIC_FONTNAME, 20));
        coinText.setLayoutX(0);
        coinText.setLayoutY(0);

        coinUI = createCoinUI();
        entity.getViewComponent().addChild(coinUI);
    }

    private void updateText(){
        coinText.setText(languageManager.getTranslation("coin") + "\t" + coin);
    }

    private Node createCoinUI(){
        var container = new HBox(5);
        Image image = new Image(GameConstants.COIN_IMAGE_PATH, 25, 20, false, false);
        ImageView imageView = new ImageView(image);
        container.getChildren().addAll(coinText, imageView);
        return container;
    }

    @Override
    public void onUpdate(double tpf) {
        updateText();
    }

    public void incrementCoin(){
        coin += COIN_VALUE;
        updateText();
    }
}