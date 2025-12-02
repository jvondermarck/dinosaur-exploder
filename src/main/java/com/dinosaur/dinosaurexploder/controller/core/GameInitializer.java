package com.dinosaur.dinosaurexploder.controller.core;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.dinosaur.dinosaurexploder.components.BombComponent;
import com.dinosaur.dinosaurexploder.components.CollectedCoinsComponent;
import com.dinosaur.dinosaurexploder.components.PlayerComponent;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.controller.BossSpawner;
import com.dinosaur.dinosaurexploder.controller.CoinSpawner;
import com.dinosaur.dinosaurexploder.controller.CountdownAnimation;
import com.dinosaur.dinosaurexploder.model.CollisionHandler;
import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import com.dinosaur.dinosaurexploder.utils.SettingsProvider;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.getAppCenter;
import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class GameInitializer {

    private final Settings settings = SettingsProvider.loadSettings();
    private final LanguageManager languageManager = LanguageManager.getInstance();

    private EnemySpawner enemySpawner;
    private CollisionHandler collisionHandler;
    private LevelManager levelManager;
    private BossSpawner bossSpawner;
    private CollectedCoinsComponent collectedCoinsComponent;
    private Entity score;
    private Entity life;
    private Entity bomb;
    private Entity player;
    private Entity levelDisplay;
    private Entity levelProgressBar;

    /**
     * Summary :
     * To move the space shuttle in forward , backward , right , left directions
     */

    public void initInput() {
        onKey(KeyCode.UP, () -> player.getComponent(PlayerComponent.class).moveUp());
        onKey(KeyCode.DOWN, () -> player.getComponent(PlayerComponent.class).moveDown());
        onKey(KeyCode.LEFT, () -> player.getComponent(PlayerComponent.class).moveLeft());
        onKey(KeyCode.RIGHT, () -> player.getComponent(PlayerComponent.class).moveRight());

        onKeyDown(KeyCode.SPACE, () -> player.getComponent(PlayerComponent.class).shoot());

        onKey(KeyCode.W, () -> player.getComponent(PlayerComponent.class).moveUp());
        onKey(KeyCode.S, () -> player.getComponent(PlayerComponent.class).moveDown());
        onKey(KeyCode.A, () -> player.getComponent(PlayerComponent.class).moveLeft());
        onKey(KeyCode.D, () -> player.getComponent(PlayerComponent.class).moveRight());

        onKeyDown(KeyCode.B, () -> bomb.getComponent(BombComponent.class).useBomb(player));
    }

    public void initGame() {
        levelManager = new LevelManager();
        FXGL.set("levelManager", levelManager);
        initGameEntities();
        collisionHandler = new CollisionHandler(levelManager);
        bossSpawner = new BossSpawner(settings, levelManager);
        CoinSpawner coinSpawner = new CoinSpawner(10, 1.0);

        AudioManager.getInstance().playMusic(GameConstants.BACKGROUND_SOUND);

        new CountdownAnimation(3).startCountdown(() -> {
            enemySpawner.resumeEnemySpawning();
            enemySpawner.spawnEnemies();
            coinSpawner.startSpawning();
        });
        enemySpawner = new EnemySpawner(this);
    }

    private void initGameEntities() {
        spawn("background", 0, 0);
        player = spawn("player", getAppCenter().getX() - 45, getAppHeight() - 200);
        levelDisplay = spawn("Level", getAppCenter().getX() - 270, getAppCenter().getY() + 350);
        score = spawn("Score", getAppCenter().getX() - 270, getAppCenter().getY() - 350);
        life = spawn("Life", getAppCenter().getX() - 260, getAppCenter().getY() + 290);
        bomb = spawn("Bomb", getAppCenter().getX() - 260, getAppCenter().getY() - 280);
        Entity coin = spawn("Coins", getAppCenter().getX() - 260, getAppCenter().getY() - 235);
        collectedCoinsComponent = coin.getComponent(CollectedCoinsComponent.class);
        bomb.addComponent(new BombComponent());
        levelProgressBar = spawn("levelProgressBar", new SpawnData(getAppCenter().getX() - 170, getAppCenter().getY() + 340).put("levelManager", levelManager));
        spawn("weaponHeat", new SpawnData(getAppCenter().getX() + 170, getAppCenter().getY() + 340).put("playerComponent", player.getComponent(PlayerComponent.class)));
    }

    public EnemySpawner getEnemySpawner() {
        return enemySpawner;
    }

    public CollisionHandler getCollisionHandler() {
        return collisionHandler;
    }

    public void setCollisionHandler(CollisionHandler collisionHandler) {
        this.collisionHandler = collisionHandler;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public void setLevelManager(LevelManager levelManager) {
        this.levelManager = levelManager;
    }

    public BossSpawner getBossSpawner() {
        return bossSpawner;
    }

    public void setBossSpawner(BossSpawner bossSpawner) {
        this.bossSpawner = bossSpawner;
    }

    public CollectedCoinsComponent getCollectedCoinsComponent() {
        return collectedCoinsComponent;
    }

    public void setCollectedCoinsComponent(CollectedCoinsComponent collectedCoinsComponent) {
        this.collectedCoinsComponent = collectedCoinsComponent;
    }

    public Entity getScore() {
        return score;
    }

    public void setScore(Entity score) {
        this.score = score;
    }

    public Entity getLife() {
        return life;
    }

    public void setLife(Entity life) {
        this.life = life;
    }

    public Entity getBomb() {
        return bomb;
    }

    public void setBomb(Entity bomb) {
        this.bomb = bomb;
    }

    public Entity getPlayer() {
        return player;
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }

    public Entity getLevelDisplay() {
        return levelDisplay;
    }

    public void setLevelDisplay(Entity levelDisplay) {
        this.levelDisplay = levelDisplay;
    }

    public Entity getLevelProgressBar() {
        return levelProgressBar;
    }

    public void setLevelProgressBar(Entity levelProgressBar) {
        this.levelProgressBar = levelProgressBar;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

}
