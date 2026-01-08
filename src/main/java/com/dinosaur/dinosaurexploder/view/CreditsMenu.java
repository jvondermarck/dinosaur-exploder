package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.model.GitHubContributor;
import com.dinosaur.dinosaurexploder.utils.GitHubProvider;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * Credits menu displaying GitHub contributors.
 */
public class CreditsMenu extends FXGLMenu {

    private static final int CONTRIBUTORS_PER_ROW = 2;
    private static final double AVATAR_SIZE = 90;
    private static final double GRID_HGAP = 40;
    private static final double GRID_VGAP = 25;

    private final LanguageManager languageManager;
    private final GitHubProvider gitHubProvider;

    private GridPane contributorsGrid;
    private VBox loadingIndicator;
    private Text errorText;
    private ScrollPane scrollPane;

    public CreditsMenu() {
        super(MenuType.MAIN_MENU);
        this.languageManager = LanguageManager.getInstance();
        this.gitHubProvider = new GitHubProvider();

        try {
            buildMenu();
            loadContributors();
        } catch (FileNotFoundException e) {
            System.err.println("Error building credits menu: " + e.getMessage());
        }
    }

    private void buildMenu() throws FileNotFoundException {
        ImageView background = MenuHelper.createAnimatedBackground(getAppWidth(), getAppHeight());
        TextFlow title = MenuHelper.createTitleFlow(
                            languageManager.getTranslation("credits"),
                            getAppWidth());
        Text subtitle = MenuHelper.createSubtitle(
                            "★ " + languageManager.getTranslation("credits_subtitle") + " ★",
                            getAppWidth() * 0.75,
                            true);
        VBox content = createContent();
        Button backButton = createBackButton();

        VBox mainLayout = new VBox(20, title, subtitle, content, backButton);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(30, 40, 30, 40));
        mainLayout.setPrefWidth(getAppWidth());
        mainLayout.setPrefHeight(getAppHeight());

        getContentRoot().getChildren().addAll(background, mainLayout);
    }

  private VBox createContent() {
    loadingIndicator = createLoadingIndicator();
    errorText = createErrorText();
    contributorsGrid = createContributorsGrid();
    scrollPane = createScrollPane();

    // Initially, only loading indicator is visible and managed
    errorText. setVisible(false);
    errorText.setManaged(false); // ← Don't take up space

    scrollPane.setVisible(false);
    scrollPane.setManaged(false);  // ← Don't take up space

    VBox contentBox = new VBox(20, loadingIndicator, errorText, scrollPane);
    contentBox.setAlignment(Pos.CENTER);
    contentBox.setPrefWidth(getAppWidth());

    return contentBox;
  }

    private VBox createLoadingIndicator() {
        Text loadingText = getUIFactoryService()
                .newText(
                        languageManager.getTranslation("loading") + "...",
                        Color.LIME,
                        GameConstants.TEXT_SUB_DETAILS);
        loadingText.setTextAlignment(TextAlignment.CENTER);

        // Add glow
        DropShadow glow = new DropShadow();
        glow.setColor(Color.LIME);
        glow.setRadius(10);
        glow.setSpread(0.5);
        loadingText.setEffect(glow);

        VBox loading = new VBox(10, loadingText);
        loading.setAlignment(Pos.CENTER);
        loading.setPadding(new Insets(40));

        return loading;
    }

    private Text createErrorText() {
        Text error = getUIFactoryService()
                .newText(
                        languageManager.getTranslation("error_loading_contributors"),
                        Color.rgb(255, 100, 100),
                        GameConstants.TEXT_SUB_DETAILS);
        error.setVisible(false);
        error.setTextAlignment(TextAlignment.CENTER);
        error.setWrappingWidth(400);

        // Add red glow to error
        DropShadow glow = new DropShadow();
        glow.setColor(Color.rgb(255, 100, 100));
        glow.setRadius(10);
        glow.setSpread(0.5);
        error.setEffect(glow);

        return error;
    }

    private GridPane createContributorsGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(GRID_HGAP);
        grid.setVgap(GRID_VGAP);
        grid.setPadding(new Insets(20));
        grid.setVisible(false);
        return grid;
    }

    private ScrollPane createScrollPane() {
        ScrollPane scrollPane = new ScrollPane(contributorsGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefWidth(480);
        scrollPane.setMaxWidth(480);
        scrollPane.setPrefViewportHeight(400);

        // Dark semi-transparent background with green border
        scrollPane.setStyle(
                "-fx-background: rgba(0, 0, 0, 0.8);"
                        + "-fx-background-color: rgba(0, 0, 0, 0.8);"
                        + "-fx-border-color: rgba(0, 220, 0, 0.7);"
                        + "-fx-border-width: 2;"
                        + "-fx-border-radius: 10;"
                        + "-fx-background-radius: 10;");

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        scrollPane
        .getStylesheets()
        .add(Objects.requireNonNull(getClass().getResource("/styles/scrollbar.css")).toExternalForm());

    return scrollPane;
  }

  private Button createBackButton() {
    // Use the new MenuHelper method to create a styled button
    Button backButton = MenuHelper.createStyledButton(languageManager.getTranslation("back"));
    backButton.setOnAction(event -> fireResume());
    return backButton;
  }

  private void loadContributors() {
    CompletableFuture<List<GitHubContributor>> future = gitHubProvider.fetchContributorsAsync();

    future.thenAccept(
        contributors -> Platform.runLater(
            () -> {
              if (contributors != null && !contributors.isEmpty()) {
                displayContributors(contributors);
              } else {
                showError();
              }
            }));

    future.exceptionally(
        throwable -> {
          Platform.runLater(this::showError);
          return null;
        });
  }

  private void displayContributors(List<GitHubContributor> contributors) {
    // Hide and remove from layout
    loadingIndicator.setVisible(false);
    loadingIndicator.setManaged(false);

    errorText.setVisible(false);
    errorText.setManaged(false);

    // Show ScrollPane and grid
    scrollPane.setVisible(true);  // ← Show ScrollPane
    scrollPane.setManaged(true);  // ← Take up space

    contributorsGrid.setVisible(true);
    contributorsGrid.setManaged(true);

    contributorsGrid.getChildren().clear();

    for (int i = 0; i < contributors. size(); i++) {
      GitHubContributor contributor = contributors.get(i);
      VBox contributorBox = createContributorBox(contributor);

      int row = i / CONTRIBUTORS_PER_ROW;
      int col = i % CONTRIBUTORS_PER_ROW;

      contributorsGrid.add(contributorBox, col, row);
    }
  }
  
  private void showError() {
    // Hide and remove from layout
    loadingIndicator.setVisible(false);
    loadingIndicator.setManaged(false);

    scrollPane.setVisible(false);  // ← Hide ScrollPane
    scrollPane.setManaged(false);  // ← Don't take up space

    contributorsGrid.setVisible(false);
    contributorsGrid.setManaged(false);

    // Show and add to layout
    errorText. setVisible(true);
    errorText.setManaged(true);
  }

    private VBox createContributorBox(GitHubContributor contributor) {
        ImageView avatar = createAvatar(contributor.getAvatarUrl());
        Label username = createUsernameLabel(contributor.getLogin());
        Label contributions = createContributionsLabel(contributor.getContributions());

        VBox box = new VBox(8, avatar, username, contributions);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setPrefWidth(220);

        // Dark box with green border
        box.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.8);"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-color: rgba(0, 220, 0, 0.5);"
                        + "-fx-border-width: 1;"
                        + "-fx-border-radius: 10;");

        setupHoverEffect(box);

        return box;
    }

    private ImageView createAvatar(String avatarUrl) {
        ImageView avatarView = new ImageView();
        avatarView.setFitWidth(AVATAR_SIZE);
        avatarView.setFitHeight(AVATAR_SIZE);
        avatarView.setPreserveRatio(true);

        Circle clip = new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2);
        avatarView.setClip(clip);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.rgb(0, 220, 0, 0.4));
        glow.setRadius(8);
        avatarView.setEffect(glow);

        loadAvatarAsync(avatarUrl, avatarView);

        return avatarView;
    }

    private Label createUsernameLabel(String username) {
    Label label = new Label(username);
    label.setStyle(
            "-fx-font-family: 'Public Pixel';"
                    + "-fx-font-size: 14px;"
                    + "-fx-text-fill: #00FF00;"
                    + "-fx-font-weight: bold;"
                    + "-fx-effect: dropshadow(gaussian, black, 4, 1.0, 0, 0);");
    label.setMaxWidth(180);
    label.setWrapText(true);
    label.setAlignment(Pos.CENTER);
    label.setTextAlignment(TextAlignment.CENTER);
    return label;
    }

    private Label createContributionsLabel(int contributions) {
        String text = contributions + " " + languageManager.getTranslation("commits");
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-family: 'Public Pixel';"
                        + "-fx-font-size: 12px;"
                        + "-fx-text-fill: #FFFF00;"
                        + "-fx-font-weight: bold;"
                        + "-fx-effect: dropshadow(gaussian, black, 3, 1.0, 0, 0);");
        return label;
    }

    private void setupHoverEffect(VBox box) {
        box.setOnMouseEntered(
                e -> {
                    box.setStyle(
                "-fx-background-color: rgba(0, 80, 0, 0.98);"
                + "-fx-background-radius: 10;"
                + "-fx-border-color: rgba(0, 255, 0, 1);"
                + "-fx-border-width: 3;"
                + "-fx-border-radius: 10;"
                + "-fx-effect: dropshadow(gaussian, rgba(0, 255, 0, 0.9), 15, 0.8, 0, 0);");
                    box.setCursor(javafx.scene.Cursor.HAND);
                });

        box.setOnMouseExited(
                e -> box.setStyle(
                        "-fx-background-color: rgba(0, 0, 0, 0.8);"
                                + "-fx-background-radius: 10;"
                                + "-fx-border-color: rgba(0, 220, 0, 0.5);"
                                + "-fx-border-width: 1;"
                                + "-fx-border-radius: 10;"));
    }

    private void loadAvatarAsync(String avatarUrl, ImageView avatarView) {
        CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return new Image(avatarUrl, AVATAR_SIZE, AVATAR_SIZE, true, true, true);
                    } catch (Exception e) {
                        return createPlaceholderAvatar();
                    }
                })
                .thenAccept(image -> Platform.runLater(() -> avatarView.setImage(image)));
    }

    private Image createPlaceholderAvatar() {
        try {
            InputStream stream = getClass().getResourceAsStream(GameConstants.GAME_LOGO_DINOSAUR);
            if (stream != null) {
                return new Image(stream, AVATAR_SIZE, AVATAR_SIZE, true, true);
            }
        } catch (Exception e) {
            System.err.println("Error loading placeholder: " + e.getMessage());
        }
        return null;
    }
  
}
