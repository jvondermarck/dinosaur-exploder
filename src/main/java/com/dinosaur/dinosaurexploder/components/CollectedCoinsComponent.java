/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.interfaces.CollectedCoins;
import com.dinosaur.dinosaurexploder.model.TotalCoins;
import com.dinosaur.dinosaurexploder.persistence.FileTotalCoinsRepository;
import com.dinosaur.dinosaurexploder.persistence.TotalCoinsRepository;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class CollectedCoinsComponent extends Component implements CollectedCoins {
  private int coin = 0;
  private static final int COIN_VALUE = 1;

  private static TotalCoins totalCoins = new TotalCoins();
  private final TotalCoinsRepository totalCoinsRepository;

  private final LanguageManager languageManager = LanguageManager.getInstance();

  private Text coinText;
  private Node coinUI;
  private Image coinImage;

  public CollectedCoinsComponent() {
    this(new FileTotalCoinsRepository());
  }

  // Public: lets tests (in another package) inject a mock instead of touching real files.
  public CollectedCoinsComponent(TotalCoinsRepository totalCoinsRepository) {
    this.totalCoinsRepository = totalCoinsRepository;
  }

  @Override
  public void onAdded() {
    totalCoins = totalCoinsRepository.load();

    coinImage = new Image(GameConstants.COIN_IMAGE_PATH, 25, 20, false, false);

    coinText =
        getUIFactoryService()
            .newText(
                languageManager.getTranslation("coin").toUpperCase() + ": " + coin,
                Color.ORANGE,
                GameConstants.TEXT_SIZE_GAME_INFO);
    coinText.setFill(Color.PURPLE);

    coinText.setLayoutX(0);
    coinText.setLayoutY(0);

    coinUI = createCoinUI();
    entity.getViewComponent().addChild(coinUI);
  }

  protected void updateText() {
    coinText.setText(languageManager.getTranslation("coin").toUpperCase() + ": " + coin);
  }

  private Node createCoinUI() {
    ImageView imageView = new ImageView(coinImage);

    HBox container = new HBox(5, coinText, imageView);
    container.setAlignment(Pos.CENTER_LEFT);

    return container;
  }

  @Override
  public void onUpdate(double tpf) {
    updateText();
  }

  public void incrementCoin() {
    coin += COIN_VALUE;
    totalCoins.setTotal(totalCoins.getTotal() + COIN_VALUE);
    updateText();
    totalCoinsRepository.save(totalCoins);
  }

  public int getCoin() {
    return coin;
  }

  /**
   * Overrides the current session coin count and total saved coins. Persists the new value to disk
   * immediately.
   *
   * @param amount the new coin amount to set
   */
  public void setCoin(int amount) {
    coin = amount;
    totalCoins.setTotal(amount);
    updateText();
    totalCoinsRepository.save(totalCoins);
  }
}
