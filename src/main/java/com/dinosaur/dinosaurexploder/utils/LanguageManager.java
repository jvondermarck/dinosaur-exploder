package com.dinosaur.dinosaurexploder.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class LanguageManager {
    private final StringProperty selectedLanguage = new SimpleStringProperty("English");
    private final String TRANSLATION_PATH = "/assets/translation/";
    private Map<String, String> translations = new HashMap<>();

    private static LanguageManager instance;

    // Private constructor to prevent instantiation
    private LanguageManager() {}

    // Get the singleton instance
    public static synchronized LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
            instance.setSelectedLanguage("English");
        }
        return instance;
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

        System.out.println("Available Languages: " + languages);
        return languages;
    }

    // Check if the application is running inside a JAR
    private boolean isRunningInsideJar() {
        return getClass().getClassLoader().getResourceAsStream("assets/translation/") == null;
    }

    // Load language files from the JAR (for packaged applications)
    private List<String> loadLanguagesFromJar() {
        List<String> languages = new ArrayList<>();
        try {
            String jarPath = LanguageManager.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            try (JarFile jarFile = new JarFile(jarPath)) {
                jarFile.stream()
                        .map(JarEntry::getName)
                        .filter(name -> name.startsWith("assets/translation/") && name.endsWith(".json"))
                        .forEach(name -> languages.add(extractLanguageName(name)));
            }
        } catch (IOException e) {
            System.err.println("Error reading languages from JAR: " + e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return languages;
    }

    // Extract language name from file path (e.g. "assets/translation/english.json" => "English")
    private String extractLanguageName(String path) {
        String lang = path.substring("assets/translation/".length(), path.length() - 5); // Remove "assets/translation/" and ".json"
        return capitalizeFirstLetter(lang);
    }

    // Capitalize the first letter of a string
    private String capitalizeFirstLetter(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    // Load language files from the resources folder (for non-JAR environment)
    private List<String> loadLanguagesFromResources() {
        List<String> languages = new ArrayList<>();
        String[] availableLanguages = {"english", "french", "german", "spanish", "japanese", "russian", "portuguese", "greek"};
        for (String lang : availableLanguages) {
            String filePath = TRANSLATION_PATH + lang + ".json";
            if (getClass().getResourceAsStream(filePath) != null) {
                languages.add(capitalizeFirstLetter(lang));
            }
        }
        return languages;
    }

    // Load the translations for the selected language
    public Map<String, String> loadTranslations(String language) {
        String filePath = TRANSLATION_PATH + language.toLowerCase() + ".json";
        try (InputStream inputStream = getClass().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new RuntimeException("Translation file not found: " + filePath);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(inputStream, Map.class);
        } catch (IOException e) {
            System.err.println("Error loading translation " + language + " :" + e.getMessage());
            return Collections.emptyMap();
        }
    }

    // Get a translated string by key
    public String getTranslation(String key) {
        return translations.getOrDefault(key, key); // Default to key if translation not found
    }
}
