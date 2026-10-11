/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.constants

/** This holds every constant in the project */
object GameConstants {
    const val VERSION = "2.1.0"
    const val GAME_NAME = "Dinosaur Exploder"

    /*
     * CONSTANTS FOR IMAGES
     */
    const val BACKGROUND_IMAGE_PATH = "assets/textures/background.png"
    const val BASE_PROJECTILE_IMAGE_PATH = "assets/textures/basicProjectile.png"
    const val ENEMY_PROJECTILE_IMAGE_FILE = "enemyProjectile.png"
    const val GREEN_DINO_IMAGE_PATH = "assets/textures/greenDino.png"
    const val GREEN_DINO_IMAGE_FILE = "greenDino.png"
    const val RED_DINO_IMAGE_FILE = "redDino.png"
    const val ORANGE_DINO_IMAGE_FILE = "orangeDino.png"
    const val COIN_IMAGE_PATH = "assets/textures/coin.png"
    const val COIN_IMAGE_FILE = "coin.png"
    const val HEART_IMAGE_PATH = "assets/textures/life.png"
    const val HEART_IMAGE_FILE = "life.png"
    const val HEART_LOST_IMAGE_PATH = "assets/textures/lifeLost.png"
    const val BOMB_IMAGE_PATH = "assets/textures/bomb.png"
    const val GAME_ICON_DINOSAUR = "icon.png"
    const val SILENT_IMAGE_PATH = "assets/textures/silent.png"
    const val PLAYING_IMAGE_PATH = "assets/textures/playing.png"
    const val GAME_LOGO_DINOSAUR = "assets/textures/dinomenu.png"
    const val ASTEROIDS_IMAGE_FILE = "asteroids.png"
    const val ALLY_IMAGE_FILE = "blue_ship.png"
    const val ALLY_DROP_IMAGE_FILE = "blue_ship_drop.png"

    /*
     * CONSTANTS FOR FONTS
     */
    const val GAME_FONT_NAME = "publicpixel.ttf"

    /*
     * SOUNDS
     */
    const val SHOOT_SOUND = "shoot.mp3"
    const val MAIN_MENU_SOUND = "/assets/sounds/mainMenu.mp3"
    const val BACKGROUND_SOUND = "gameBackground.mp3"
    const val ENEMY_EXPLODE_SOUND = "enemyExplode.mp3"
    const val PLAYER_HIT_SOUND = "playerHit.mp3"
    const val COIN_GAIN = "coinHit.mp3"
    const val HEART_HIT_SOUND = "heartHit.mp3"

    /*
     * CONSTANTS FOR UI
     */
    const val MAIN_TITLES = 25.0
    const val TEXT_SIZE_GAME_DETAILS = 20.0
    const val TEXT_SUB_DETAILS = 15.0
    const val TEXT_SIZE_GAME_INFO = 12.0
    const val STYLESHEET_PATH = "/styles/styles.css"

    /*
     * CONSTANTS FOR SAVED FILES
     */
    const val HIGH_SCORE_FILE = "highScore.ser"
    const val TOTAL_COINS_FILE = "totalCoins.ser"
    const val ACHIEVEMENTS_FILE = "achievements.ser"

    /*
     * CONSTANTS FOR TEXT
     */
    const val BOMBS_LEFT = "bombs_left"
    const val LIVES = "lives"
    const val OUT_OF_BOUNDS = "Out of bounds"
    const val SHIELD = "shield"
}
