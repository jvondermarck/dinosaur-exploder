package com.dinosaur.dinosaurexploder.model;

import com.almasb.fxgl.entity.component.Component;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class CoinComponent extends Component {
    Integer coin = 0;
    private final Integer COIN_VALUE = 1;


    private final LanguageManager languageManager = LanguageManager.getInstance();

    private Text coinText;
    private Node coinUI;

    @Override
    public void onAdded() {


        // Create UI elements
        coinText = new Text();


        coinText.setFill(Color.GOLDENROD);
        coinText.setFont(Font.font(GameConstants.ARCADECLASSIC_FONTNAME, 20));

        coinText.setLayoutX(0);
        coinText.setLayoutY(0);




        coinUI = createCoinUI();
        entity.getViewComponent().addChild(coinUI);
    }


    private void updateText(){
        coinText.setText("Coin: "+coin);
    }

    private Node createCoinUI(){
        var container = new HBox(5);
        Image image = new Image(GameConstants.COIN_IMAGEPATH, 25, 20, false, false);
        ImageView imageView = new ImageView(image);
        container.getChildren().addAll(coinText, imageView);
        return container;

    }

    @Override
    public void onUpdate(double tpf) {
//        update coin UI text
        coinText.setText("coins:\t" + coin);
    }


    public Integer getCoin() {
        return coin;
    }

    public void setCoin(Integer coin) {
        this.coin = coin;
        updateText();
    }

    public void incrementCoin(){
        coin += COIN_VALUE;
        updateText();

    }
}