/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.achievements.Achievement;
import com.dinosaur.dinosaurexploder.achievements.AchievementManager;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Objects;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

/**
 * Achievements screen styled to match the game's pixel/sci-fi aesthetic. Shows all achievements
 * with locked/unlocked status, coin rewards and a global progress bar. Accessible from both the
 * main menu and the pause menu (ESC).
 */
public class AchievementsMenu extends FXGLMenu {

  // ── Layout ────────────────────────────────────────────────────────────────
  private static final double CARD_SPACING = 10;
  private static final double SECTION_SPACING = 14;
  private static final double SCROLL_HEIGHT_RATIO = 0.57;
  private static final double CARD_WIDTH_RATIO = 0.82;
  private static final double CARD_HEIGHT = 68;
  private static final double PROGRESS_H = 14;

  // ── Palette (matches styles.css) ──────────────────────────────────────────
  private static final Color C_GREEN = Color.web("#00dc00");
  private static final Color C_CYAN = Color.web("#00c8ff");
  private static final Color C_LIME = Color.web("#7DD858");
  private static final Color C_GOLD = Color.GOLD;
  private static final Color C_LOCKED_TEXT = Color.web("#4a4a4a");
  private static final Color C_LOCKED_BORDER = Color.web("#2a2a2a");
  private static final Color C_CARD_BG_DARK = Color.web("#0d0d0d");

  private final LanguageManager languageManager = LanguageManager.getInstance();

  // Cached textures
  private Image coinImage;
  private Image lockImage;
  private Image starImage;
  private AchievementManager manager;

  public AchievementsMenu() {
    super(MenuType.MAIN_MENU);
    loadTextures();
    buildMenu();
  }

  // ── Texture loading ────────────────────────────────────────────────────────

  private void loadTextures() {
    coinImage = loadImage("assets/textures/coin.png");
    lockImage = loadImage("assets/textures/lock.png");
    starImage = loadImage("assets/textures/star_badge.png");
  }

  private Image loadImage(String path) {
    InputStream is = getClass().getClassLoader().getResourceAsStream(path);
    return (is != null) ? new Image(is) : null;
  }

  // ── Menu building ──────────────────────────────────────────────────────────

  private void buildMenu() {
    manager = new AchievementManager();
    List<Achievement> achievements = manager.loadAchievement();
    if (achievements.isEmpty()) achievements = manager.getAllAchievements();

    long unlocked = achievements.stream().filter(Achievement::isCompleted).count();

    MenuHelper.setupSelectionMenu(
        this,
        createHeader(unlocked, achievements.size()),
        createScrollPane(achievements),
        createBackButton(),
        SECTION_SPACING,
        getAppWidth(),
        getAppHeight());
  }

  // ── Zone 1 · Header ───────────────────────────────────────────────────────

  private VBox createHeader(long unlocked, int total) {

    // Star badge icon
    StackPane starIcon = new StackPane();
    if (starImage != null) {
      ImageView iv = new ImageView(starImage);
      iv.setFitHeight(38);
      iv.setFitWidth(38);
      iv.setPreserveRatio(true);
      DropShadow starGlow = new DropShadow(14, C_GREEN);
      starGlow.setSpread(0.3);
      iv.setEffect(starGlow);
      starIcon.getChildren().add(iv);
    }

    // Title — pixel font + double-glow matching the CSS button effect
    Text titleText =
        getUIFactoryService()
            .newText(
                languageManager.getTranslation("achievements").toUpperCase(),
                C_GREEN,
                FontType.MONO,
                GameConstants.MAIN_TITLES);
    DropShadow titleGlow = new DropShadow(18, C_GREEN);
    titleGlow.setSpread(0.35);
    DropShadow titleGlow2 = new DropShadow(32, C_CYAN);
    titleGlow2.setSpread(0.10);
    titleGlow.setInput(titleGlow2);
    titleText.setEffect(titleGlow);

    // Pulsing animation
    FadeTransition pulse = new FadeTransition(Duration.seconds(2.4), titleText);
    pulse.setFromValue(0.82);
    pulse.setToValue(1.0);
    pulse.setCycleCount(FadeTransition.INDEFINITE);
    pulse.setAutoReverse(true);
    pulse.setInterpolator(Interpolator.EASE_BOTH);
    pulse.play();

    HBox titleRow = new HBox(10, starIcon, titleText);
    titleRow.setAlignment(Pos.CENTER);

    // Separator line — cyan/green gradient
    Rectangle sep = new Rectangle(getAppWidth() * 0.72, 2);
    sep.setFill(
        new LinearGradient(
            0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.TRANSPARENT),
            new Stop(0.2, C_CYAN),
            new Stop(0.8, C_GREEN),
            new Stop(1, Color.TRANSPARENT)));
    DropShadow sepGlow = new DropShadow(6, C_CYAN);
    sep.setEffect(sepGlow);

    // Summary text
    Text summaryText =
        getUIFactoryService()
            .newText(
                unlocked + " / " + total + "  unlocked",
                C_LIME,
                FontType.MONO,
                GameConstants.TEXT_SIZE_GAME_INFO);
    summaryText.setTextAlignment(TextAlignment.CENTER);

    // Progress bar
    StackPane bar = buildProgressBar(unlocked, total);

    VBox header = new VBox(6, titleRow, sep, summaryText, bar);
    header.setAlignment(Pos.CENTER);
    return header;
  }

  private StackPane buildProgressBar(long unlocked, int total) {
    double barW = getAppWidth() * 0.72;
    double ratio = (total == 0) ? 0 : (double) unlocked / total;

    // Track
    Rectangle track = new Rectangle(barW, PROGRESS_H);
    track.setArcWidth(PROGRESS_H);
    track.setArcHeight(PROGRESS_H);
    track.setFill(Color.web("#111111"));
    track.setStroke(Color.web("#333333"));
    track.setStrokeWidth(1);

    // Scan-line overlay on track for pixel feel
    Rectangle scanlines = new Rectangle(barW, PROGRESS_H);
    scanlines.setArcWidth(PROGRESS_H);
    scanlines.setArcHeight(PROGRESS_H);
    scanlines.setFill(Color.TRANSPARENT);
    scanlines.setStyle(
        "-fx-fill: repeating-linear-gradient(to bottom,"
            + " rgba(0,0,0,0.18) 0px, rgba(0,0,0,0.18) 1px,"
            + " transparent 1px, transparent 3px);");

    // Fill — green→cyan gradient matching CSS slider
    double fillW = Math.max(barW * ratio, 0);
    Rectangle fill = new Rectangle(fillW, PROGRESS_H);
    fill.setArcWidth(PROGRESS_H);
    fill.setArcHeight(PROGRESS_H);
    fill.setFill(
        new LinearGradient(
            0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0.0, C_GREEN),
            new Stop(0.5, C_LIME),
            new Stop(1.0, C_CYAN)));
    DropShadow fillGlow = new DropShadow(10, C_GREEN);
    fillGlow.setSpread(0.25);
    fill.setEffect(fillGlow);

    StackPane fillWrap = new StackPane(fill);
    fillWrap.setMaxWidth(barW);
    fillWrap.setAlignment(Pos.CENTER_LEFT);

    StackPane bar = new StackPane(track, fillWrap, scanlines);
    bar.setMaxWidth(barW);
    bar.setAlignment(Pos.CENTER_LEFT);
    return bar;
  }

  // ── Zone 2 · Card list ────────────────────────────────────────────────────

  private ScrollPane createScrollPane(List<Achievement> achievements) {
    VBox list = new VBox(CARD_SPACING);
    list.setAlignment(Pos.CENTER);
    list.setPadding(new Insets(8, 16, 12, 16));

    for (Achievement a : achievements) {
      list.getChildren().add(buildCard(a, list, achievements));
    }

    ScrollPane sp = new ScrollPane(list);
    sp.setFitToWidth(true);
    sp.setPrefWidth(getAppWidth() * 0.88);
    sp.setPrefHeight(getAppHeight() * SCROLL_HEIGHT_RATIO);
    sp.setStyle(
        "-fx-background: transparent;"
            + " -fx-background-color: transparent;"
            + " -fx-border-color: transparent;");
    sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    String scrollbarCss = Objects.requireNonNull(
        getClass().getResource("/styles/scrollbar.css")).toExternalForm();
    sp.getStylesheets().add(scrollbarCss);
    return sp;
  }

  private StackPane buildCard(
      Achievement achievement, VBox parentList, List<Achievement> allAchievements) {
    boolean done = achievement.isCompleted();
    boolean claimed = achievement.isClaimed();
    // claimable = completed but not yet claimed
    boolean claimable = done && !claimed;
    double cardW = getAppWidth() * CARD_WIDTH_RATIO;

    // ── Card background ────────────────────────────────────────────────────
    Rectangle bg = new Rectangle(cardW, CARD_HEIGHT);
    bg.setArcWidth(6);
    bg.setArcHeight(6);

    if (claimed) {
      // Already claimed — muted gold tint
      bg.setFill(
          new LinearGradient(
              0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
              new Stop(0.0, Color.web("#1a1200", 0.95)),
              new Stop(1.0, Color.web("#0d0a00", 0.95))));
      bg.setStroke(Color.web("#5a4500"));
      bg.setStrokeWidth(1.2);
    } else if (done) {
      // Claimable — bright green, pulsing invitation
      bg.setFill(
          new LinearGradient(
              0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
              new Stop(0.0, Color.web("#0a1a0a", 0.97)),
              new Stop(0.6, Color.web("#001500", 0.97)),
              new Stop(1.0, Color.web("#001020", 0.97))));
      DropShadow cardGlow = new DropShadow(14, C_GREEN);
      cardGlow.setSpread(0.12);
      DropShadow cardGlow2 = new DropShadow(4, C_CYAN);
      cardGlow2.setSpread(0.05);
      cardGlow.setInput(cardGlow2);
      bg.setEffect(cardGlow);
      bg.setStroke(
          new LinearGradient(
              0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
              new Stop(0, C_GREEN),
              new Stop(1, C_CYAN)));
      bg.setStrokeWidth(1.5);
    } else {
      bg.setFill(Color.web("#0d0d0d", 0.92));
      bg.setStroke(C_LOCKED_BORDER);
      bg.setStrokeWidth(1);
    }

    // ── Corner pixel accents ────────────────────────────────────────────────
    Color accentColor = claimed ? Color.web("#5a4500") : (done ? C_CYAN : C_LOCKED_BORDER);
    Rectangle tl = cornerAccent(accentColor);
    tl.setTranslateX(-cardW / 2.0 + 4);
    tl.setTranslateY(-CARD_HEIGHT / 2.0 + 4);
    Rectangle br = cornerAccent(accentColor);
    br.setTranslateX(cardW / 2.0 - 4);
    br.setTranslateY(CARD_HEIGHT / 2.0 - 4);

    // ── Left badge ──────────────────────────────────────────────────────────
    StackPane badgeBox = buildBadge(done, claimed);

    // ── Description ────────────────────────────────────────────────────────
    Color descColor = claimed ? Color.web("#a08030") : (done ? C_LIME : C_LOCKED_TEXT);
    Text desc =
        getUIFactoryService()
            .newText(
                achievement.getDescription(),
                descColor,
                FontType.MONO,
                GameConstants.TEXT_SIZE_GAME_INFO);
    desc.setWrappingWidth(cardW * 0.48);
    desc.setTextAlignment(TextAlignment.LEFT);
    if (done && !claimed) {
      DropShadow dg = new DropShadow(5, C_GREEN);
      dg.setSpread(0.10);
      desc.setEffect(dg);
    }

    // ── Right area: coin pill OR "CLAIMED" badge OR locked ──────────────────
    javafx.scene.Node rightNode;
    if (claimed) {
      // Show a "CLAIMED" pill in gold
      Text claimedText =
          getUIFactoryService()
              .newText("CLAIMED", Color.web("#a08030"), FontType.MONO, 10);
      javafx.scene.layout.HBox claimedPill = new javafx.scene.layout.HBox(claimedText);
      claimedPill.setAlignment(Pos.CENTER);
      claimedPill.setPadding(new Insets(4, 10, 4, 10));
      claimedPill.setStyle(
          "-fx-background-color: #1a1200;"
              + " -fx-background-radius: 4;"
              + " -fx-border-radius: 4;"
              + " -fx-border-color: #5a4500;"
              + " -fx-border-width: 1;");
      StackPane claimedBox = new StackPane(claimedPill);
      claimedBox.setMinWidth(90);
      claimedBox.setAlignment(Pos.CENTER_RIGHT);
      rightNode = claimedBox;
    } else {
      rightNode = buildCoinPill(achievement.getRewardCoins(), done);
    }

    // ── "CLICK TO CLAIM" hint label under description ───────────────────────
    VBox descBox = new VBox(2, desc);
    if (claimable) {
      Text hint =
          getUIFactoryService()
              .newText("click to claim!", C_CYAN, FontType.MONO, 9);
      FadeTransition ft = new FadeTransition(Duration.seconds(1.2), hint);
      ft.setFromValue(0.4);
      ft.setToValue(1.0);
      ft.setCycleCount(FadeTransition.INDEFINITE);
      ft.setAutoReverse(true);
      ft.play();
      descBox.getChildren().add(hint);
    }
    descBox.setAlignment(Pos.CENTER_LEFT);

    // ── Inner row ───────────────────────────────────────────────────────────
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    HBox inner = new HBox(10, badgeBox, descBox, spacer, rightNode);
    inner.setAlignment(Pos.CENTER_LEFT);
    inner.setPadding(new Insets(0, 14, 0, 8));
    inner.setMaxWidth(cardW - 4);

    // ── Assemble card ───────────────────────────────────────────────────────
    StackPane card = new StackPane(bg, tl, br, inner);
    card.setMaxWidth(cardW);
    card.setAlignment(Pos.CENTER_LEFT);

    if (claimable) {
      card.setStyle("-fx-cursor: hand;");
      card.setOnMouseEntered(
          e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(110), card);
            st.setToX(1.028);
            st.setToY(1.028);
            st.play();
          });
      card.setOnMouseExited(
          e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(110), card);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
          });
      card.setOnMouseClicked(
          e -> {
            int earned = achievement.claim();
            if (earned > 0) {
              addCoins(earned);
              manager.saveAchievement(allAchievements);
              // Rebuild the list with updated state
              int idx = allAchievements.indexOf(achievement);
              if (idx >= 0) {
                parentList.getChildren().set(
                    idx, buildCard(achievement, parentList, allAchievements));
              }
            }
          });
    } else if (done) {
      // Already claimed: subtle hover only
      card.setOnMouseEntered(
          e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(110), card);
            st.setToX(1.012);
            st.setToY(1.012);
            st.play();
          });
      card.setOnMouseExited(
          e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(110), card);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
          });
    }
    return card;
  }

  /** 3×3 pixel corner accent square */
  private Rectangle cornerAccent(Color color) {
    Rectangle r = new Rectangle(5, 5);
    r.setFill(color);
    if (!color.equals(C_LOCKED_BORDER)) {
      Glow g = new Glow(0.8);
      r.setEffect(g);
    }
    return r;
  }

  private StackPane buildBadge(boolean done, boolean claimed) {
    // Background circle
    javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(22);
    circle.setFill(
        done
            ? (claimed
                ? Color.web("#1a1200", 0.9)
                : new LinearGradient(
                    0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#003300", 0.9)),
                    new Stop(1, Color.web("#001520", 0.9))))
            : Color.web("#1a1a1a", 0.8));
    circle.setStroke(claimed ? Color.web("#5a4500") : (done ? C_GREEN : C_LOCKED_BORDER));
    circle.setStrokeWidth(done ? 1.5 : 1.0);
    if (done && !claimed) {
      DropShadow cg = new DropShadow(8, C_GREEN);
      cg.setSpread(0.2);
      circle.setEffect(cg);
    }

    // Icon image
    javafx.scene.Node icon;
    if (claimed) {
      // Coin image to show reward was taken
      if (coinImage != null) {
        ImageView iv = new ImageView(coinImage);
        iv.setFitWidth(20); iv.setFitHeight(20); iv.setPreserveRatio(true);
        javafx.scene.effect.ColorAdjust ca = new javafx.scene.effect.ColorAdjust();
        ca.setBrightness(-0.2);
        iv.setEffect(ca);
        icon = iv;
      } else {
        icon = getUIFactoryService().newText("✓", Color.web("#a08030"), 16);
      }
    } else if (done && starImage != null) {
      ImageView iv = new ImageView(starImage);
      iv.setFitWidth(22); iv.setFitHeight(22); iv.setPreserveRatio(true);
      Glow glow = new Glow(0.9);
      iv.setEffect(glow);
      icon = iv;
    } else if (!done && lockImage != null) {
      ImageView iv = new ImageView(lockImage);
      iv.setFitWidth(18); iv.setFitHeight(18); iv.setPreserveRatio(true);
      ColorAdjust dark = new ColorAdjust();
      dark.setBrightness(-0.5);
      iv.setEffect(dark);
      icon = iv;
    } else {
      icon = getUIFactoryService()
          .newText(done ? "★" : "?", done ? C_GREEN : C_LOCKED_TEXT, 18);
    }

    StackPane badge = new StackPane(circle, icon);
    badge.setMinWidth(52);
    badge.setAlignment(Pos.CENTER);
    return badge;
  }

  private StackPane buildCoinPill(int coins, boolean done) {
    // Coin icon
    javafx.scene.Node coinNode;
    if (coinImage != null) {
      ImageView iv = new ImageView(coinImage);
      iv.setFitWidth(18);
      iv.setFitHeight(18);
      iv.setPreserveRatio(true);
      if (!done) {
        ColorAdjust ca = new ColorAdjust();
        ca.setSaturation(-1);
        ca.setBrightness(-0.4);
        iv.setEffect(ca);
      }
      coinNode = iv;
    } else {
      coinNode = getUIFactoryService()
          .newText("🪙", done ? C_GOLD : C_LOCKED_TEXT, GameConstants.TEXT_SIZE_GAME_INFO);
    }

    Text amount =
        getUIFactoryService()
            .newText(
                "+" + coins,
                done ? C_GOLD : C_LOCKED_TEXT,
                FontType.MONO,
                GameConstants.TEXT_SIZE_GAME_INFO);
    if (done) {
      DropShadow goldGlow = new DropShadow(6, C_GOLD);
      goldGlow.setSpread(0.15);
      amount.setEffect(goldGlow);
    }

    HBox pill = new HBox(5, coinNode, amount);
    pill.setAlignment(Pos.CENTER);
    pill.setPadding(new Insets(4, 10, 4, 8));

    // Pill background — styled like a mini CSS button
    String pillStyle;
    if (done) {
      pillStyle =
          "-fx-background-color: linear-gradient(to bottom, #1a1200, #0d0a00);"
              + " -fx-background-radius: 4;"
              + " -fx-border-color: rgba(218,165,32,0.75), rgba(0,200,255,0.35);"
              + " -fx-border-width: 1.5;"
              + " -fx-border-radius: 4;"
              + " -fx-effect: dropshadow(gaussian, rgba(218,165,32,0.5), 8, 0.3, 0, 0);";
    } else {
      pillStyle =
          "-fx-background-color: #111111;"
              + " -fx-background-radius: 4;"
              + " -fx-border-color: #2a2a2a;"
              + " -fx-border-width: 1;"
              + " -fx-border-radius: 4;";
    }
    pill.setStyle(pillStyle);

    StackPane wrapper = new StackPane(pill);
    wrapper.setMinWidth(90);
    wrapper.setAlignment(Pos.CENTER_RIGHT);
    return wrapper;
  }

  // ── Coin persistence ───────────────────────────────────────────────────────

  private void addCoins(int amount) {
    com.dinosaur.dinosaurexploder.model.TotalCoins totalCoins =
        new com.dinosaur.dinosaurexploder.model.TotalCoins();
    // Load current total
    try (ObjectInputStream in =
        new ObjectInputStream(new FileInputStream(GameConstants.TOTAL_COINS_FILE))) {
      totalCoins = (com.dinosaur.dinosaurexploder.model.TotalCoins) in.readObject();
    } catch (IOException | ClassNotFoundException ignored) {
      // file missing → start at 0
    }
    totalCoins.setTotal(totalCoins.getTotal() + amount);
    // Save updated total
    try (ObjectOutputStream out =
        new ObjectOutputStream(new FileOutputStream(GameConstants.TOTAL_COINS_FILE))) {
      out.writeObject(totalCoins);
    } catch (IOException ignored) {
      // best-effort save
    }
  }

  // ── Zone 3 · Back button ──────────────────────────────────────────────────

  private Button createBackButton() {
    Button back = new Button(languageManager.getTranslation("back").toUpperCase());
    back.getStylesheets()
        .add(
            Objects.requireNonNull(getClass().getResource(GameConstants.STYLESHEET_PATH))
                .toExternalForm());
    back.setMinSize(140, 60);
    back.setOnAction(e -> fireResume());
    return back;
  }
}
