module com.dinosaur.dinosaurexploder {
  requires javafx.controls;
  requires com.almasb.fxgl.all;
  requires jpro.webapi;

  exports com.dinosaur.dinosaurexploder;

  requires javafx.media;
  requires com.almasb.fxgl.scene;
  requires javafx.graphics;
  requires com.fasterxml.jackson.databind;
  requires annotations;
  requires javafx.base;
  requires com.almasb.fxgl.entity;
  requires java.logging;

  opens assets.textures;
  opens assets.sounds;
  opens assets.ui.fonts;
  opens com.dinosaur.dinosaurexploder.model to
      com.almasb.fxgl.core;
  opens com.dinosaur.dinosaurexploder.components to
      com.almasb.fxgl.core;
  opens com.dinosaur.dinosaurexploder.utils to
      com.almasb.fxgl.core;
  opens com.dinosaur.dinosaurexploder.constants to
      com.almasb.fxgl.core;
  opens com.dinosaur.dinosaurexploder.interfaces to
      com.almasb.fxgl.core;
  opens com.dinosaur.dinosaurexploder.controller.core to
      com.almasb.fxgl.core;
}
