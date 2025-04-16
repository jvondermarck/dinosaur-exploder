package com.dinosaur.dinosaurexploder.model;

import java.util.List;

import com.almasb.fxgl.localization.Language;

/**
 *
 * This holds every constant in the the PROJECT
 */
public class GameConstants {
    
    /*
    * CONSTANTS FOR IMAGES
    */
    public static final String BACKGROUND_IMAGEPATH = "/assets/textures/background.png";
    public static final String BASE_PROJECTILE_IMAGEPATH = "assets/textures/basicProjectile.png";
    public static final String BASE_PROJECTILE_IMAGEFILE = "basicProjectile.png";
    public static final String ENEMY_PROJECTILE_IMAGEFILE = "enemyProjectile.png";
    public static final String GREENDINO_IMAGEPATH = "assets/textures/greenDino.png";
    public static final String GREENDINO_IMAGEFILE = "greenDino.png";
    public static final String COIN_IMAGEPATH = "assets/textures/coin.png";
    public static final String COIN_IMAGEFILE = "coin.png";
    public static final String HEART_IMAGEPATH = "assets/textures/life.png";
    public static final String BOMB_IMAGEPATH = "assets/textures/bomb.png";
    /*
    *CONSTANTS FOR FONTS
    */
    public static final String ARCADECLASSIC_FONTNAME = "ArcadeClassic";
    /*
    * SOUNDS
    */
    public static final String ENEMYSHOOT_SOUND = "enemyShoot.wav";
    public static final String SHOOT_SOUND = "shoot.wav";
    public static final String MAINMENU_SOUND = "/assets/sounds/mainMenu.wav";
    public static final String BACKGROUND_SOUND ="gameBackground.wav";
    public static final String ENEMY_EXPLODE_SOUND = "enemyExplode.wav";
    public static final String PLAYER_HIT_SOUND = "playerHit.wav";
    public static final String COIN_GAIN = "coinHit.wav";
    
    public static final String GAME_NAME = "Dinosaur Exploder";
    
    public static final List<Language> AVAILABLE_LANGUAGES  = List.of(Language.ENGLISH, Language.GERMAN );

    
}
