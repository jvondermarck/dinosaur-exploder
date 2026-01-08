package com.dinosaur.dinosaurexploder.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LanguageManager {
  private final StringProperty selectedLanguage = new SimpleStringProperty("English");
  private static final String TRANSLATION_PATH = "/assets/translation/";
  private static final String TRANSLATION_PATH_NO_SLASH = "assets/translation/";
  private static final String JSON_FILE_EXTENSION = ".json";
  private Map<String, String> translations = new HashMap<>();
  private static final Logger LOGGER = Logger.getLogger(LanguageManager.class.getName());
  private static LanguageManager languageManager;

  // Private constructor to prevent instantiation
  private LanguageManager() {}

  // Get the singleton instance
  public static synchronized LanguageManager getInstance() {
    if (languageManager == null) {
      languageManager = new LanguageManager();
      languageManager.setSelectedLanguage("English");
    }
    return languageManager;
  }

  // Setter for selected language, loads the respective translations
  public void setSelectedLanguage(String language) {
    translations = loadTranslations(language);
    selectedLanguage.set(language);
  }

  public StringProperty selectedLanguageProperty() {
    return selectedLanguage;
  }

  // Main method to fetch all available languages
  public List<String> getAvailableLanguages() {
    List<String> languages;

    // Try loading languages from resources or JAR file based on environment
    if (isRunningInsideJar()) {
      languages = loadLanguagesFromJar();
    } else {
      languages = loadLanguagesFromResources();
    }

    LOGGER.log(Level.INFO, "Available languages: {0}", languages);
    return languages;
  }

  // Check if the application is running inside a JAR
  private boolean isRunningInsideJar() {
    try (InputStream is =
        getClass().getClassLoader().getResourceAsStream(TRANSLATION_PATH_NO_SLASH)) {
      return is == null;
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Error checking JAR environment: ", e);
      return false;
    }
  }

  // Load language files from the JAR (for packaged applications)
  private List<String> loadLanguagesFromJar() {
    List<String> languages = new ArrayList<>();
    try {
      String jarPath =
          LanguageManager.class
              .getProtectionDomain()
              .getCodeSource()
              .getLocation()
              .toURI()
              .getPath();
      try (JarFile jarFile = new JarFile(jarPath)) {
        jarFile.stream()
            .map(JarEntry::getName)
            .filter(
                name ->
                    name.startsWith(TRANSLATION_PATH_NO_SLASH)
                        && name.endsWith(JSON_FILE_EXTENSION))
            .forEach(name -> languages.add(extractLanguageName(name)));
      }
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Error reading languages from JAR: ", e);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
    return languages;
  }

  // Extract language name from file path (e.g. "assets/translation/english.json" => "English")
  private String extractLanguageName(String path) {
    String lang =
        path.substring(
            TRANSLATION_PATH_NO_SLASH.length() - 1,
            path.length() - 5); // Remove "assets/translation/" and ".json"
    return capitalizeFirstLetter(lang);
  }

  // Capitalize the first letter of a string
  private String capitalizeFirstLetter(String text) {
    return text.substring(0, 1).toUpperCase() + text.substring(1);
  }

  // Load language files from the resources folder (for non-JAR environment)
  private List<String> loadLanguagesFromResources() {
    List<String> languages = new ArrayList<>();
    String[] availableLanguages = {
      "english",
      "french",
      "german",
      "spanish",
      "japanese",
      "russian",
      "portuguese",
      "greek",
      "bulgarian"
    };
    for (String lang : availableLanguages) {
      String filePath = TRANSLATION_PATH + lang + JSON_FILE_EXTENSION;
      try (InputStream is = getClass().getResourceAsStream(filePath)) {
        if (is != null) {
          languages.add(capitalizeFirstLetter(lang));
        }
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, "Error checking language file: {0}", filePath);
      }
    }
    return languages;
  }

  // Load the translations for the selected language
  public Map<String, String> loadTranslations(String language) {
    String filePath = TRANSLATION_PATH + language.toLowerCase() + JSON_FILE_EXTENSION;
    try (InputStream inputStream = getClass().getResourceAsStream(filePath)) {
      if (inputStream == null) {
        throw new RuntimeException("Translation file not found:  " + filePath);
      }

      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(inputStream, new TypeReference<>() {});
    } catch (IOException e) {
      LOGGER.log(
          Level.INFO,
          "Error loading translation {0} :{1}",
          new Object[] {language, e.getMessage()});
      return Collections.emptyMap();
    }
  }

  // Get a translated string by key
  public String getTranslation(String key) {
    return translations.getOrDefault(key, key); // Default to key if translation not found
  }
}
