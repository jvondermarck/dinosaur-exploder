package com.dinosaur.dinosaurexploder.model;

import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.dsl.views.SelfScrollingBackgroundView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.dinosaur.dinosaurexploder.components.*;

import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.utils.FXGLGameTimer;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import com.dinosaur.dinosaurexploder.components.PlayerComponent;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

/**
 * Summary :
 * The Factory handles the creation of Background , Player , Score , Life ,
 * Dino, Explosion
 */
public class GameEntityFactory implements EntityFactory {
    /**
     * Summary :
     * New Background creation will be handled in below Entity
     */
    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        Image img = new Image(Objects.requireNonNull(
                Objects.requireNonNull(getClass().getResource(GameConstants.BACKGROUND_IMAGE_PATH))
                        .toString()));

        return FXGL.entityBuilder()
                .view(new SelfScrollingBackgroundView(img, 3000, 1500, Orientation.VERTICAL, -50))
                .zIndex(-1)
                .buildAndAttach();
    }

    /**
     * Summary :
     * New Players creation will be handled in below Entity
     */
    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        // Get the selected ship
        int selectedShip = GameData.getSelectedShip();
        String shipImagePath = "assets/textures/spaceship" + selectedShip + ".png";
        System.out.println("Nave seleccionada en newPlayer: " + selectedShip);

        // Set Ship Image
        Image shipImage = new Image(getClass().getResourceAsStream("/" + shipImagePath));

        // Ship dimension
        double width = shipImage.getWidth();
        double height = shipImage.getHeight();

        return entityBuilderBase(data, EntityType.PLAYER)
                .view(new ImageView(shipImage))
                .bbox(new HitBox(new Point2D(0, 0), BoundingShape.box(width, height))) // la nave
                .collidable()
                .with(new PlayerComponent())
                .build();
    }

    /**
     * Summary :
     * New BasicProjectile creation will be handled in below Entity
     */
    @Spawns("basicProjectile")
    public Entity newBasicProjectile(SpawnData data) {
        Point2D direction = data.get("direction");
        int selectedShip = GameData.getSelectedShip();
        int selectedWeapon = GameData.getSelectedWeapon();
        int speed = 600 * (selectedWeapon);
        String weaponImagePath = "assets/textures/projectiles/projectile" + selectedShip + "_" + selectedWeapon
                + ".png";

        Image projectileImage = new Image(getClass().getResourceAsStream("/" + weaponImagePath));
        return entityBuilderBase(data, EntityType.PROJECTILE)
                // The OffscreenCleanComponent is used because when the projectiles move, if
                // they
                // move outside the screen we want them deleted.
                .with(new OffscreenCleanComponent())
                .view(new ImageView(projectileImage))
                .bbox(new HitBox(BoundingShape.box(50, 50)))
                .collidable()
                .with(new ProjectileComponent(direction, speed))
                .build();

    }

    /**
     * Summary :
     * New Enemy BasicProjectile creation will be handled in below Entity
     */
    @Spawns("basicEnemyProjectile")
    public Entity newBasicEnemyProjectile(SpawnData data) {
        Point2D direction = data.get("direction");
        return entityBuilderBase(data, EntityType.ENEMY_PROJECTILE)
                .with(new OffscreenCleanComponent())
                .view(texture(GameConstants.ENEMY_PROJECTILE_IMAGE_FILE, 30, 17))
                .bbox(new HitBox(BoundingShape.box(20, 20)))
                .collidable()
                .with(new ProjectileComponent(direction, 300))
                .build();

    }

    /**
     * Summary :
     * New Green Dino creation will be handled in below Entity
     */
    @Spawns("greenDino")
    public Entity newGreenDino(SpawnData data) {
        return entityBuilderBase(data, EntityType.GREEN_DINO)
                .with(new OffscreenCleanComponent())
                .view(texture(GameConstants.GREEN_DINO_IMAGE_FILE, 80, 60))
                .bbox(new HitBox(BoundingShape.box(65, 55)))
                .collidable()
                .with(new GreenDinoComponent())
                .build();
    }

    /**
     * spawn random coin on the window
     */

    @Spawns("coin")
    public Entity newCoin(SpawnData data) {
        System.out.println("Loading coin texture: " + GameConstants.COIN_IMAGE_FILE);
        return entityBuilderBase(data, EntityType.COIN)
                .with(new OffscreenCleanComponent())
                .view(texture(GameConstants.COIN_IMAGE_FILE, 40, 40))
                .bbox(new HitBox(BoundingShape.box(40, 40)))
                .collidable()
                .with(new CoinComponent())
                .build();
    }

    @Spawns("redDino")
    public Entity newRedDino(SpawnData data) {
        return entityBuilderBase(data, EntityType.RED_DINO)
                .with(new OffscreenCleanComponent())
                .view(texture(GameConstants.RED_DINO_IMAGE_FILE, 100, 80))
                .bbox(new HitBox(BoundingShape.box(65, 55)))
                .collidable()
                .with(new RedDinoComponent(new FXGLGameTimer()))
                .build();
    }

    @Spawns("orangeDino")
    public Entity newOrangeDino(SpawnData data) {
        PlayerComponent player;
        player = getGameWorld().getEntitiesByComponent(PlayerComponent.class).get(0).getComponent(PlayerComponent.class);

        return entityBuilderBase(data, EntityType.ORANGE_DINO)
                .with(new OffscreenCleanComponent())
                .view(texture(GameConstants.ORANGE_DINO_IMAGE_FILE, 100, 150))
                .bbox(new HitBox(BoundingShape.box(65, 55)))
                .collidable()
                .with(new OrangeDinoComponent(new FXGLGameTimer(), player))
                .build();
    }

    @Spawns("healthBar")
    public Entity newHealthbar(SpawnData data) {
        Rectangle healthbar = new Rectangle(200.0, 25.0, Color.RED);
        return entityBuilderBase(data, EntityType.HEALTHBAR)
                .with(new OffscreenCleanComponent())
                .with(new HealthbarComponent())
                .view(healthbar)
                .build();
    }

    /**
     * Summary :
     * Spawn of a heart in the window will be handled in below Entity
     */
    @Spawns("heart")
    public Entity newHeart(SpawnData data) {
        System.out.println("Loading heart texture: " + GameConstants.HEART_IMAGE_FILE);
        return entityBuilderBase(data, EntityType.HEART)
                .with(new OffscreenCleanComponent())
                .view(texture(GameConstants.HEART_IMAGE_FILE))
                .bbox(new HitBox(BoundingShape.box(22, 22)))
                .collidable()
                .with(new Heart())
                .build();
    }

    /**
     * Summary :
     * Setting up the Score will be handled in below Entity
     */
    @Spawns("Score")
    public Entity newScore(SpawnData data) {
        Text scoreText = new Text("");
        scoreText.setFill(Color.GREEN);
        scoreText.setFont(Font.font(GameConstants.ARCADE_CLASSIC_FONTNAME, 20));
        return entityBuilderBase(data, EntityType.SCORE)
                .view(scoreText)
                .with(new ScoreComponent())
                .with(new OffscreenCleanComponent()).build();
    }

    /**
     * Summary :
     * Life text will be handled in below Entity
     */
    @Spawns("Life")
    public Entity newLife(SpawnData data) {
        Text lifeText = new Text("Lives: 3");
        return entityBuilderBase(data, EntityType.LIFE)
                .from(data)
                .view(lifeText)
                .with(new LifeComponent())
                .with(new OffscreenCleanComponent()).build();
    }

    @Spawns("Bomb")
    public Entity newBomb(SpawnData data) {
        Text bombText = new Text("Bombs: 3");
        return entityBuilderBase(data, EntityType.BOMB)
                .from(data)
                .view(bombText)
                .with(new BombComponent())
                .with(new OffscreenCleanComponent()).build();
    }

    /**
     * spawn total earned coin view on the window
     */
    @Spawns("Coins")
    public Entity newCoins(SpawnData data) {
        Text coinText = new Text("Coins: 0");
        return entityBuilderBase(data, EntityType.COIN)
                .from(data)
                .view(coinText)
                .with(new CollectedCoinsComponent())
                .with(new OffscreenCleanComponent()).build();
    }

    /**
     * Summary :
     * Animation of an explosion will be handled in below Entity
     */
    @Spawns("explosion")
    public Entity newExplosion(SpawnData data) {
        Duration seconds = Duration.seconds(0.4);
        AnimationChannel ac = new AnimationChannel(
                FXGL.image("explosion.png"),
                seconds, 16);

        AnimatedTexture at = new AnimatedTexture(ac);
        at.play();
        return FXGL.entityBuilder(data)
                .view(at)
                .with(new ExpireCleanComponent(seconds))
                .build();
    }

    /**
     * Summary :
     * Creates level text that shows the current level of the game.
     */
    @Spawns("Level")
    public Entity newLevel(SpawnData data) {
        Text levelText = new Text("Level: 1");
        levelText.setFill(Color.LIGHTBLUE);
        levelText.setTranslateX(10);
        levelText.setFont(
                Font.font(GameConstants.ARCADE_CLASSIC_FONTNAME, GameConstants.TEXT_SIZE_GAME_DETAILS));
        return entityBuilderBase(data, EntityType.LEVEL)
                .view(levelText)
                .build();
    }

    /**
     * Summary :
     * Creates level progress bar
     */
    @Spawns("levelProgressBar")
    public Entity newLevelProgressBar(SpawnData data) {
        LevelManager levelManager = data.get("levelManager");

        Rectangle background = new Rectangle(150, 10, Color.DARKGRAY);
        background.setStroke(Color.GRAY);
        background.setStrokeWidth(1);

        Rectangle filled = new Rectangle(0, 8, Color.LIMEGREEN);
        filled.setLayoutX(1);
        filled.setLayoutY(1);

        Group progressBar = new Group(background, filled);

        return entityBuilderBase(data, EntityType.LEVEL_PROGRESS_BAR)
                .view(progressBar)
                .with(new LevelProgressBarComponent(filled, levelManager))
                .build();
    }
    @Spawns("weaponHeat")
    public Entity newWeaponHeat(SpawnData data) {
        PlayerComponent playerComponent = data.get("playerComponent");

        Rectangle background = new Rectangle(100, 15, Color.DARKGRAY);
        background.setStroke(Color.GRAY);
        background.setStrokeWidth(1);

        Rectangle fill = new Rectangle(0, 13, Color.LIMEGREEN);  // ← Start at 0 width
        fill.setLayoutX(1);
        fill.setLayoutY(1);
        Group heatBar = new Group(background, fill);

        return entityBuilderBase(data, EntityType.WEAPON_HEAT)
                .view(heatBar)
                .with(new WeaponHeatComponent(fill, playerComponent))  // ← Add component
                .build();
}

    /**
     * Summary :
     * Reusable part of every entity
     */
    private EntityBuilder entityBuilderBase(SpawnData data, EntityType type) {
        return FXGL.entityBuilder()
                .type(type)
                .from(data);
    }
}
