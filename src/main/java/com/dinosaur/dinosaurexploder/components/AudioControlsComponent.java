package com.dinosaur.dinosaurexploder.components;

import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import com.dinosaur.dinosaurexploder.utils.SettingsProvider;
import java.util.Objects;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

/**
 * Reusable component for creating audio volume controls (sliders + labels). Eliminates duplication
 * across DinosaurMenu, SoundMenu, and PauseMenu.
 */
public class AudioControlsComponent {
  public static final String LABEL_FORMAT = "%.0f%%";
  public static final double SLIDER_MIN = 0;
  public static final double SLIDER_MAX = 1;
  public static final double SLIDER_INCREMENT = 0.01;

  /** Volume types supported by the audio system. */
  public enum VolumeType {
    MUSIC, // background music
    SFX // sound effects
  }

  /**
   * Creates a complete volume control (label + slider) for the specified volume type.
   *
   * @param type The type of volume control (MUSIC or SFX)
   * @param settings Current game settings
   * @param maxWidth Maximum width for the slider (optional, can be null)
   * @return VBox containing the label and slider
   */
  public static VBox createVolumeControl(VolumeType type, Settings settings, Double maxWidth) {
    Slider slider = createVolumeSlider(type, settings);
    Label label = createVolumeLabel(slider, type, settings);

    VBox volumeControl = new VBox(label, slider);
    volumeControl.setSpacing(10);

    return volumeControl;
  }

  /** Creates a complete volume control with default width. */
  public static VBox createVolumeControl(VolumeType type, Settings settings) {
    return createVolumeControl(type, settings, null);
  }

  /**
   * Creates a volume slider for the specified type.
   *
   * @param type The volume type (MUSIC or SFX)
   * @param settings Current game settings
   * @param maxWidth Maximum width for the slider (optional)
   * @return Configured slider with event listeners
   */
  public static Slider createVolumeSlider(VolumeType type, Settings settings, Double maxWidth) {
    Slider slider = new Slider(SLIDER_MIN, SLIDER_MAX, SLIDER_MAX);

    double initialValue =
        (type == VolumeType.MUSIC) ? settings.getVolume() : settings.getSfxVolume();
    slider.adjustValue(initialValue);
    slider.setBlockIncrement(SLIDER_INCREMENT);

    applySliderStyling(slider);

    if (maxWidth != null) {
      slider.setMaxWidth(maxWidth);
    }
    return slider;
  }

  /** Creates a volume slider with default width. */
  public static Slider createVolumeSlider(VolumeType type, Settings settings) {
    return createVolumeSlider(type, settings, null);
  }

  /**
   * Creates a volume label that displays percentage and updates with slider changes.
   *
   * @param slider The slider to link with this label
   * @param type The volume type
   * @param settings Current game settings
   * @return Label that shows volume percentage
   */
  public static Label createVolumeLabel(Slider slider, VolumeType type, Settings settings) {
    double initialVolume =
        (type == VolumeType.MUSIC) ? settings.getVolume() : settings.getSfxVolume();

    Label label = new Label(String.format(LABEL_FORMAT, initialVolume * 100));
    label.setStyle("-fx-text-fill: #61C181;");

    attachVolumeListener(slider, label, type, settings);

    return label;
  }

  /**
   * Applies consistent styling to volume sliders.
   *
   * @param slider The slider to style
   */
  public static void applySliderStyling(Slider slider) {
    try {
      slider
          .getStylesheets()
          .add(
              Objects.requireNonNull(AudioControlsComponent.class.getResource("/styles/styles.css"))
                  .toExternalForm());
    } catch (NullPointerException e) {
      System.err.println("Warning: Could not load slider styles.");
    }
  }

  /**
   * Attaches volume change listener to slider. Updates both the AudioManager and saves settings
   * when volume changes.
   *
   * @param slider The slider to attach listener to
   * @param label The label to update with percentage
   * @param type The volume type
   * @param settings The settings object to update
   */
  private static void attachVolumeListener(
      Slider slider, Label label, VolumeType type, Settings settings) {
    slider
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              double volume = newValue.doubleValue();

              if (type == VolumeType.MUSIC) {
                AudioManager.getInstance().setVolume(volume);
                settings.setVolume(volume);
              } else {
                AudioManager.getInstance().setSfxVolume(volume);
                settings.setSfxVolume(volume);
              }

              SettingsProvider.saveSettings(settings);

              label.setText(String.format(LABEL_FORMAT, volume * 100));
            });
  }
}
