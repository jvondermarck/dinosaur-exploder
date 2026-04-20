/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;

import java.io.Serializable;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Base class for all achievements in the game. Provides common functionality and contract for
 * achievement implementations.
 */
public abstract class Achievement implements Serializable {

  private static final long serialVersionUID = 1L;

  protected boolean completed = false;
  protected boolean claimed = false;
  protected final int rewardCoins;

  protected Achievement(int rewardCoins) {
    this.rewardCoins = rewardCoins;
  }

  public boolean isCompleted() {
    return completed;
  }

  public boolean isClaimed() {
    return claimed;
  }

  /**
   * Marks the reward as claimed. Returns the number of coins earned, or 0 if already claimed.
   * Persisting the state is the caller's responsibility.
   */
  public int claim() {
    if (!completed || claimed) return 0;
    claimed = true;
    return rewardCoins;
  }

  public int getRewardCoins() {
    return rewardCoins;
  }

  /**
   * Returns a human-readable description of the achievement. Example: "Kill 10 dinosaurs", "Reach
   * 5000 points"
   */
  public abstract String getDescription();

  /**
   * Called every frame to update achievement progress.
   *
   * @param tpf Time per frame in seconds
   */
  public abstract void update(double tpf);

  /** Called when a dinosaur is killed. Override this in achievements that track kills. */
  public void onDinosaurKilled() {
    // Default: do nothing. Override in subclasses if needed.
  }

  /** Called when score changes. Override this in achievements that track score. */
  public void onScoreChanged(int newScore) {
    // Default: do nothing. Override in subclasses if needed.
  }

  /** Called when coins are collected. Override this in achievements that track coins. */
  public void onCoinCollected(int totalCoins) {
    // Default: do nothing. Override in subclasses if needed.
  }

  /** Called when a boss is defeated. Override this in achievements that track boss kills. */
  public void onBossDefeated() {
    // Default: do nothing. Override in subclasses if needed.
  }

  /**
   * Shows a styled pixel-art achievement banner at the top of the screen.
   * Slides in from above, holds for 3 s, then fades out.
   */
  protected void showBanner() {
    try {
      com.dinosaur.dinosaurexploder.utils.AudioManager.getInstance()
          .playSound(com.dinosaur.dinosaurexploder.constants.GameConstants.ACHIEVEMENT_SOUND);
    } catch (Exception ignored) {}
    try {
      com.almasb.fxgl.dsl.FXGL.runOnce(() -> {
        double appW = com.almasb.fxgl.dsl.FXGL.getAppWidth();

        // ── Palette ────────────────────────────────────────────────────────
        Color cGreen = Color.web("#00dc00");
        Color cCyan  = Color.web("#00c8ff");
        Color cGold  = Color.GOLD;

        // ── Background panel ───────────────────────────────────────────────
        double panelW = Math.min(appW * 0.72, 520);
        double panelH = 72;
        Rectangle bg = new Rectangle(panelW, panelH);
        bg.setArcWidth(8); bg.setArcHeight(8);
        bg.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.web("#000d00", 0.97)),
            new Stop(0.5, Color.web("#001a00", 0.97)),
            new Stop(1.0, Color.web("#000d14", 0.97))));
        bg.setStroke(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, cGreen),
            new Stop(1.0, cCyan)));
        bg.setStrokeWidth(1.8);
        DropShadow panelGlow = new DropShadow(22, cGreen);
        panelGlow.setSpread(0.18);
        DropShadow panelGlow2 = new DropShadow(40, cCyan);
        panelGlow2.setSpread(0.06);
        panelGlow.setInput(panelGlow2);
        bg.setEffect(panelGlow);

        // ── Corner accents ─────────────────────────────────────────────────
        Rectangle tl = new Rectangle(5, 5, cCyan);
        tl.setEffect(new Glow(0.9));
        tl.setTranslateX(-panelW / 2.0 + 4); tl.setTranslateY(-panelH / 2.0 + 4);
        Rectangle br = new Rectangle(5, 5, cCyan);
        br.setEffect(new Glow(0.9));
        br.setTranslateX(panelW / 2.0 - 4);  br.setTranslateY(panelH / 2.0 - 4);

        // ── Star icon (or texture) ─────────────────────────────────────────
        javafx.scene.Node starNode;
        try {
          java.io.InputStream is = getClass().getClassLoader()
              .getResourceAsStream("assets/textures/star_badge.png");
          if (is != null) {
            ImageView iv = new ImageView(new Image(is));
            iv.setFitWidth(30); iv.setFitHeight(30); iv.setPreserveRatio(true);
            iv.setEffect(new Glow(1.0));
            starNode = iv;
          } else {
            Text t = new Text("★");
            t.setFill(cGold); t.setStyle("-fx-font-size:26;");
            t.setEffect(new DropShadow(8, cGold));
            starNode = t;
          }
        } catch (Exception ex) {
          Text t = new Text("★");
          t.setFill(cGold); t.setStyle("-fx-font-size:26;");
          starNode = t;
        }

        // ── Texts ──────────────────────────────────────────────────────────
        Text label = com.almasb.fxgl.dsl.FXGL.getUIFactoryService()
            .newText("ACHIEVEMENT UNLOCKED", cCyan,
                com.almasb.fxgl.ui.FontType.MONO, 10);
        DropShadow lg = new DropShadow(6, cCyan); lg.setSpread(0.2);
        label.setEffect(lg);

        Text desc = com.almasb.fxgl.dsl.FXGL.getUIFactoryService()
            .newText(getDescription(), cGreen,
                com.almasb.fxgl.ui.FontType.MONO, 13);
        DropShadow dg = new DropShadow(8, cGreen); dg.setSpread(0.15);
        desc.setEffect(dg);

        // Coin reward pill
        Text coinText = com.almasb.fxgl.dsl.FXGL.getUIFactoryService()
            .newText("+" + rewardCoins, cGold,
                com.almasb.fxgl.ui.FontType.MONO, 11);
        DropShadow coinGlow = new DropShadow(6, cGold); coinGlow.setSpread(0.2);
        coinText.setEffect(coinGlow);

        javafx.scene.Node coinIcon;
        try {
          java.io.InputStream ci = getClass().getClassLoader()
              .getResourceAsStream("assets/textures/coin.png");
          if (ci != null) {
            ImageView civ = new ImageView(new Image(ci));
            civ.setFitWidth(14); civ.setFitHeight(14); civ.setPreserveRatio(true);
            coinIcon = civ;
          } else { coinIcon = new Text("●"); ((Text)coinIcon).setFill(cGold); }
        } catch (Exception ex) { coinIcon = new Text("●"); ((Text)coinIcon).setFill(cGold); }

        HBox coinPill = new HBox(4, coinIcon, coinText);
        coinPill.setAlignment(Pos.CENTER);
        coinPill.setPadding(new Insets(2, 7, 2, 6));
        coinPill.setStyle(
            "-fx-background-color: linear-gradient(to bottom,#1a1200,#0d0800);"
            + " -fx-background-radius:4; -fx-border-radius:4;"
            + " -fx-border-color:rgba(218,165,32,0.8); -fx-border-width:1.2;");

        VBox textCol = new VBox(3, label, desc, coinPill);
        textCol.setAlignment(Pos.CENTER_LEFT);

        HBox inner = new HBox(12, starNode, textCol);
        inner.setAlignment(Pos.CENTER_LEFT);
        inner.setPadding(new Insets(0, 16, 0, 14));
        inner.setMaxWidth(panelW - 4);

        StackPane card = new StackPane(bg, tl, br, inner);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(panelW);

        // ── Outer container (centered horizontally, positioned at top) ─────
        StackPane root = new StackPane(card);
        root.setPrefWidth(appW);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(14, 0, 0, 0));

        // ── Add to UI layer ────────────────────────────────────────────────
        com.almasb.fxgl.dsl.FXGL.addUINode(root);

        // ── Slide-in from above ────────────────────────────────────────────
        root.setTranslateY(-(panelH + 30));
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(380), root);
        slideIn.setToY(0);
        slideIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        // ── Pulsing glow on the border ─────────────────────────────────────
        Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO,     new KeyValue(panelGlow.radiusProperty(), 22)),
            new KeyFrame(Duration.seconds(1), new KeyValue(panelGlow.radiusProperty(), 36)),
            new KeyFrame(Duration.seconds(2), new KeyValue(panelGlow.radiusProperty(), 22)));
        pulse.setCycleCount(Timeline.INDEFINITE);

        // ── Fade-out after 3.2 s ──────────────────────────────────────────
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
        fadeOut.setFromValue(1.0); fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.seconds(3.2));
        fadeOut.setOnFinished(e -> {
          pulse.stop();
          com.almasb.fxgl.dsl.FXGL.removeUINode(root);
        });

        slideIn.setOnFinished(e -> pulse.play());
        ParallelTransition anim = new ParallelTransition(slideIn, fadeOut);
        anim.play();

      }, Duration.ZERO);
    } catch (Exception ex) {
      // FXGL not initialized (e.g., in tests) — skip
    }
  }

  /** Called when the achievement is completed. Shows notification to the player. */
  protected abstract void onComplete();
}
