package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.interfaces.CollectedCoins;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.model.TotalCoins;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.io.*;

public class CollectedCoinsComponent extends Component implements CollectedCoins {
    private int coin = 0;
    private final int COIN_VALUE = 1;

    private static TotalCoins totalCoins = new TotalCoins();

    private final LanguageManager languageManager = LanguageManager.getInstance();

    private Text coinText;
    private Node coinUI;

    @Override
    public void onAdded() {
        loadTotalCoins(); // Deserialize once when the component is added

        // Create UI elements
        coinText = new Text();
        coinText.setFill(Color.PURPLE);
        coinText.setFont(Font.font(GameConstants.GAME_FONTNAME, 20));
        coinText.setLayoutX(0);
        coinText.setLayoutY(0);

        coinUI = createCoinUI();
        entity.getViewComponent().addChild(coinUI);
    }

    protected void updateText() {
        coinText.setText(languageManager.getTranslation("coin") + ": " + coin);
    }

    private Node createCoinUI() {
        var container = new HBox(5);
        Image image = new Image(GameConstants.COIN_IMAGE_PATH, 25, 20, false, false);
        ImageView imageView = new ImageView(image);
        container.getChildren().addAll(coinText, imageView);
        return container;
    }

    private void loadTotalCoins() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(GameConstants.TOTAL_COINS_FILE))) {
            totalCoins = (TotalCoins) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            totalCoins = new TotalCoins(); // Defaults to 0 if file is missing or corrupted
        }
    }

    private void saveTotalCoins() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(GameConstants.TOTAL_COINS_FILE))) {
            out.writeObject(totalCoins);
        } catch (IOException e) {
            System.err.println("Error saving coins: " + e.getMessage());
        }
    }

    @Override
    public void onUpdate(double tpf) {
        updateText();
    }

    public void incrementCoin() {
        coin += COIN_VALUE;
        totalCoins.setTotal(totalCoins.getTotal() + COIN_VALUE);
        updateText();
        saveTotalCoins();
    }

    public int getCoin() {
        return coin;
    }
}