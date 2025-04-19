
    requires com.almasb.fxgl.all;
    requires jpro.webapi;

    exports com.dinosaur.dinosaurexploder;

    requires javafx.media;
    requires com.almasb.fxgl.scene;
    requires javafx.graphics;

    requires com.fasterxml.jackson.databind;

    opens assets.textures;
    opens assets.sounds;
    opens assets.ui.fonts;

    opens com.dinosaur.dinosaurexploder.model to com.almasb.fxgl.core;
}