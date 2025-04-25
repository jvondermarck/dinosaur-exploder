package com.dinosaur.dinosaurexploder.controller;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.time.TimerAction;
import com.dinosaur.dinosaurexploder.model.*;
import com.dinosaur.dinosaurexploder.utils.SettingsProvider;
import com.dinosaur.dinosaurexploder.view.DinosaurGUI;
import com.dinosaur.dinosaurexploder.model.LanguageManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import com.almasb.fxgl.entity.SpawnData;
import static javafx.util.Duration.seconds;

/**
 * Summary :
 *      The Factory handles the Dinosaur , player controls and collision detection of all entities in the game
 */
public class DinosaurController {
	LanguageManager languageManager = LanguageManager.getInstance();
	private Entity player;
	private Entity score;
	private Entity life;
	private Entity bomb;
	private Entity coin;
	private CoinComponent coinComponent;
	private LevelManager levelManager;
	private Entity levelDisplay;
	private TimerAction enemySpawnTimer;
	private boolean isSpawningPaused = false;

	private TimerAction countDownAction;
	private Text countDownText;
	private int countDown = 3;
	private boolean gameStarted = false;

	private final Settings settings = SettingsProvider.loadSettings();

	int consecutiveDinoCounter = 0; // for dinosaur/asteroid spawning

	/**
	 * Summary :
	 *      Detecting the player damage to decrease the lives and checking if the game is over
	 */

	public void damagePlayer() {
		if(player.getComponent(PlayerComponent.class).isInvincible()){
			return; 
		}
		int lives = life.getComponent(LifeComponent.class).decreaseLife(1);
		var flash = new Rectangle(DinosaurGUI.WIDTH, DinosaurGUI.HEIGHT, Color.rgb(190, 10, 15, 0.5));
		getGameScene().addUINode(flash);
		runOnce(() -> getGameScene().removeUINode(flash), seconds(0.5));

		if (lives <= 0) {
			// Added extra line of code to sync the lives counter after death
			// All hearts disappear after death
			life.getComponent(LifeComponent.class).onUpdate(lives);
			System.out.println("Game Over!");
			gameOver();
		} else {
			System.out.printf("%d lives remaining ! ", lives);
		}
	}
	/**
	 * Summary :
	 *      To move the space shuttle in forward , backward , right , left directions
	 */
	public void initInput() {
		onKey(KeyCode.UP, () -> player.getComponent(PlayerComponent.class).moveUp());
		onKey(KeyCode.DOWN, () -> player.getComponent(PlayerComponent.class).moveDown());
		onKey(KeyCode.LEFT, () -> player.getComponent(PlayerComponent.class).moveLeft());
		onKey(KeyCode.RIGHT, () -> player.getComponent(PlayerComponent.class).moveRight());

		onKeyDown(KeyCode.SPACE, () -> player.getComponent(PlayerComponent.class).shoot(settings.isMuted()));

		onKey(KeyCode.W, () -> player.getComponent(PlayerComponent.class).moveUp());
		onKey(KeyCode.S, () -> player.getComponent(PlayerComponent.class).moveDown());
		onKey(KeyCode.A, () -> player.getComponent(PlayerComponent.class).moveLeft());
		onKey(KeyCode.D, () -> player.getComponent(PlayerComponent.class).moveRight());

		onKeyDown(KeyCode.B, () -> bomb.getComponent(BombComponent.class).useBomb(player));
	}

	public void countdownAnimation(){
		countDownAction = getGameTimer().runAtInterval(() -> {
			if(countDown > 0){
				countDown -= 1;
				countDownText.setText(String.valueOf(countDown));
			}else{
				countDownAction.expire();
				countDownText.setVisible(false);

				gameStarted = true;
			}
			if(countDown == 1) {
				spawnEnemies();
			}

		}, Duration.seconds(1));
	}

	public void initGame() {
		levelManager = new LevelManager();
		spawn("background", 0, 0);
		player = spawn("player", getAppCenter().getX() - 45, getAppHeight() - 200);
		if(!settings.isMuted()) {
			FXGL.play(GameConstants.BACKGROUND_SOUND);
		}

		levelDisplay = spawn("Level", getAppCenter().getX() - 270, getAppCenter().getY() - 350);
		levelDisplay.setZIndex(100);
		score = spawn("Score", getAppCenter().getX() - 270, getAppCenter().getY() - 320);
		life = spawn("Life", getAppCenter().getX() - 260, getAppCenter().getY() - 250);
		bomb = spawn("Bomb", getAppCenter().getX() - 260, getAppCenter().getY() - 180);
		coin = spawn("Coins", getAppCenter().getX() - 260, getAppCenter().getY() - 120);
		System.out.println("Coins at : " + coin.getPosition());
		coinComponent = coin.getComponent(CoinComponent.class);

		bomb.addComponent(new BombComponent());
		updateLevelDisplay();

		gameStarted = false;
		countDown = 3;
		countDownText = getUIFactoryService().newText(String.valueOf(countDown), Color.WHITE, 60);
		getGameScene().addUINode(countDownText);
		FXGL.animationBuilder()
		.interpolator(Interpolators.ELASTIC.EASE_OUT())
		.onCycleFinished(() -> System.out.println("Started countdown animation"))
		.onFinished(() -> System.out.println("Countdown animation finished"))
		.duration(Duration.seconds(1))
		.repeat(4)
		.translate(countDownText)
		.from(new Point2D((getAppWidth() - countDownText.getLayoutBounds().getWidth())/2,getAppHeight() /2.0 - 200))
		.to(new Point2D((getAppWidth() - countDownText.getLayoutBounds().getWidth())/2,getAppHeight() /2.0))
		.buildAndPlay();

		countdownAnimation();

		/*
		 * every 1 sec there is 10% change to span a coin
		 */
		run(() -> {
			if (gameStarted && random(0, 100) < 10) {
				double x = random(0, getAppWidth() - 80);
				spawn("coin", x, 0);
			}
		}, seconds(1.0));
	}

	/**
	 * Summary :
	 *      This method is used to spawn the enemies
	 *      and set the spawn rate of the enemies
	 */

	private void spawnEnemies(){
		if(enemySpawnTimer != null){
			enemySpawnTimer.expire();
		}

		/*
		 * At each second that passes, we have 2 out of 3 chances of spawning a green
		 * dinosaur/asteroid
		 * This spawns dinosaurs randomly
		 * 
		 * after 3 green dinosaurs have been spawned in a row, 
		 * there is a 1/4 chance of each next dino that spawns being an
		 * asteroid
		 */


		enemySpawnTimer = run(() -> {
			if (!isSpawningPaused && random(0, 2) < 2) {
				if (random(0, 3) < 3) { // 2/3 chance to spawn a regular dinosaur
					Entity greenDino = spawn("greenDino", random(0, getAppWidth() - 80), -50);
					greenDino.getComponent(GreenDinoComponent.class).setMuted(settings.isMuted());
					consecutiveDinoCounter++;
				} else if (consecutiveDinoCounter > 3){ // 1/3 chance to spawn asteroid if atleast 3 dinos have spawned in a row
					Entity asteroid = spawn("asteroid", random(0, getAppWidth() - 80), -50);
					asteroid.getComponent(asteroidComponent.class).setMuted(settings.isMuted());
					consecutiveDinoCounter = 0;

				} else {
					Entity greenDino = spawn("greenDino", random(0, getAppWidth() - 80), -50);
					greenDino.getComponent(GreenDinoComponent.class).setMuted(settings.isMuted());
					consecutiveDinoCounter++;
				}

			}
		}, seconds(levelManager.getEnemySpawnRate()));
	}

	/**
	 * Summary :
	 *      This method is used to pause the enemy spawning
	 */
	private void pauseEnemySpawning(){
		isSpawningPaused = true;
		if(enemySpawnTimer != null){
			enemySpawnTimer.pause();
		}
	}

	/**
	 * Summary :
	 *      This method is used to resume the enemy spawning
	 */

	private void resumeEnemySpawning(){
		isSpawningPaused = false;
		if(enemySpawnTimer != null){
			enemySpawnTimer.resume();
		} else{
			spawnEnemies();
		}
	}
	/**
	 * Summary :
	 *      This method is used to update the level display
	 */

	private void updateLevelDisplay(){
		Text levelText = (Text) levelDisplay.getViewComponent().getChildren().get(0);
		levelText.setText(languageManager.getTranslation("level") + ": " + levelManager.getCurrentLevel());

		// Regenerate bombs when level changes
		if (bomb.hasComponent(BombComponent.class)) {
			bomb.getComponent(BombComponent.class).checkLevelForBombRegeneration(levelManager.getCurrentLevel());
		}
	}
	/**
	 * Summary :
	 *      Handles level progression when enemies are defeated
	 *      and shows a message when the level is changed
	 */

	private void showLevelMessage(){
		//Pause game elements during level transition
		FXGL.getGameWorld().getEntitiesByType(EntityType.GREENDINO).forEach(e -> {
			if(e.hasComponent(GreenDinoComponent.class)) {
				e.getComponent(GreenDinoComponent.class).setPaused(true);
			}
		});

		pauseEnemySpawning();

		//Display centered level notification
		Text levelText = getUIFactoryService().newText(languageManager.getTranslation("level") + levelManager.getCurrentLevel(), Color.WHITE, 24);
		levelText.setStroke(Color.BLACK);
		levelText.setStrokeWidth(1.5);
		centerText(levelText);
		getGameScene().addUINode(levelText);

		// Trigger bomb regeneration for level advancement
		if (bomb.hasComponent(BombComponent.class)) {
			bomb.getComponent(BombComponent.class).checkLevelForBombRegeneration(levelManager.getCurrentLevel());
		}

		// Resume gameplay after a delay
		runOnce(() -> {
			getGameScene().removeUINode(levelText);
			updateLevelDisplay();

			FXGL.getGameWorld().getEntitiesByType(EntityType.GREENDINO).forEach(e -> {
				if(e.hasComponent(GreenDinoComponent.class)){
					e.getComponent(GreenDinoComponent.class).setPaused(false);
				}
			});

			resumeEnemySpawning();

			player.getComponent(PlayerComponent.class).setInvincible(true);
			runOnce(() -> {
				if(player != null && player.isActive()){
					player.getComponent(PlayerComponent.class).setInvincible(false);
				}
			}, seconds(3));
		}, seconds(2));
	}

	/**
	 * Summary :
	 *      Center the text on the screen
	 */

	private void centerText(Text text){
		text.setX((getAppWidth() - text.getLayoutBounds().getWidth()) / 2.0);
		text.setY(getAppHeight() / 2.0);
	}


	/**
	 * Summary :
	 *      Detect the collision between the game elements.
	 */

	public void initPhysics() {
		/*
		 * After collision of projectile and greenDino there hava explosion animation
		 * and there have 50% change to spawn a coin
		 */
		onCollisionBegin(EntityType.PROJECTILE, EntityType.GREENDINO, (projectile, greendino) -> {
			spawn("explosion", greendino.getX() - 25, greendino.getY() - 30);
			if (random(0, 100) < 50) {
				spawn("coin", greendino.getX(), greendino.getY());
			}
			if(!settings.isMuted()) {
				FXGL.play(GameConstants.ENEMY_EXPLODE_SOUND);
			}
			projectile.removeFromWorld();
			greendino.removeFromWorld();
			score.getComponent(ScoreComponent.class).incrementScore(1);
			levelManager.incrementDefeatedEnemies();
			if(levelManager.shouldAdvanceLevel()){
				levelManager.nextLevel();
				showLevelMessage();
				System.out.println("Level up!");
			}

		});
		onCollisionBegin(EntityType.PROJECTILE, EntityType.ASTEROID, (projectile, asteroid) -> {
			spawn("explosion", asteroid.getX() - 25, asteroid.getY() - 30);
			// note that points are not rewarded for destroying an asteroid
			if (!settings.isMuted()) {
				FXGL.play(GameConstants.ENEMY_EXPLODE_SOUND);
			}
			projectile.removeFromWorld();
			asteroid.removeFromWorld();
			// logic for spawning asteroid projectiles
			int numberOfShards = 5;
			double angleSpread = 60;
			double baseAngle = 90;
			double startAngle = baseAngle - angleSpread / 2.0;
			double angleIncrement = (numberOfShards > 1) ? angleSpread / (numberOfShards - 1) : 0;
			for (int i = 0; i < numberOfShards; i++) {
				double currentAngle = startAngle + i * angleIncrement;
				Vec2 directionVec = Vec2.fromAngle(currentAngle); 
				SpawnData shardData = new SpawnData(asteroid.getPosition());
				shardData.put("direction", directionVec.toPoint2D());
				spawn("asteroidShard", shardData);
			}
		});

		onCollisionBegin(EntityType.ASTEROIDSHARD, EntityType.PLAYER, (shard, player) -> {
			if(!settings.isMuted()) {
				FXGL.play(GameConstants.PLAYER_HIT_SOUND);
			}
			shard.removeFromWorld();
			System.out.println("You got hit !\n");
			damagePlayer();
		});

		onCollisionBegin(EntityType.ENEMYPROJECTILE, EntityType.PLAYER, (projectile, player) -> {
			if(!settings.isMuted()) {
				FXGL.play(GameConstants.PLAYER_HIT_SOUND);
			}
			projectile.removeFromWorld();
			System.out.println("You got hit !\n");
			damagePlayer();
		});

		onCollisionBegin(EntityType.PLAYER, EntityType.GREENDINO, (player, greendino) -> {
			if(!settings.isMuted()) {
				FXGL.play(GameConstants.PLAYER_HIT_SOUND);
			}
			greendino.removeFromWorld();
			System.out.println("You touched a dino !");
			damagePlayer();
		});
		onCollisionBegin(EntityType.PLAYER, EntityType.COIN, (player, coin) -> {
			if(!settings.isMuted()){
				FXGL.play(GameConstants.COIN_GAIN);
			}
			coin.removeFromWorld();
			System.out.println("You touched a coin!");
			coinComponent.incrementCoin();

			// Check for bomb regeneration when coin is collected
			if (bomb.hasComponent(BombComponent.class)) {
				bomb.getComponent(BombComponent.class).trackCoinForBombRegeneration();
			}
		});
	}

	/**
	 * Summary :
	 *      To detect whether the player lives are empty or not
	 */
	public void gameOver() {
		Button btnYes = getUIFactoryService().newButton(languageManager.getTranslation("yes"));
		btnYes.setPrefWidth(200);
		btnYes.defaultButtonProperty();
		// action event for the yes Button
		EventHandler<ActionEvent> startNewGameEvent = e -> getGameController().startNewGame();

		// when button is pressed
		btnYes.setOnAction(startNewGameEvent);

		Button btnNo = getUIFactoryService().newButton(languageManager.getTranslation("no"));
		btnNo.setPrefWidth(200);

		// action event for the no Button
		EventHandler<ActionEvent> backToMenuEvent = e -> getGameController().gotoMainMenu();

		// when button is pressed
		btnNo.setOnAction(backToMenuEvent);

		getDialogService().showBox(languageManager.getTranslation("new_game"), new VBox(), btnYes, btnNo);
	}
}