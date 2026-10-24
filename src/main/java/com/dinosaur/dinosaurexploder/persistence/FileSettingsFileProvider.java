/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.persistence;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/** File-backed implementation for storing game settings. */
public class FileSettingsFileProvider implements SettingsFileProvider {

    private static final Logger LOGGER =
            Logger.getLogger(FileSettingsFileProvider.class.getName());

    private final Path settingsPath;

    public FileSettingsFileProvider(Path settingsPath) {
        this.settingsPath = Objects.requireNonNull(settingsPath);
    }

    @Override
    public Properties load() {
        Properties properties = new Properties();

        if (!Files.isRegularFile(settingsPath)) {
            return properties;
        }

        try (InputStream in = Files.newInputStream(settingsPath)) {
            properties.load(in);
            return properties;
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Error reading settings: {0}", e.getMessage());
            return new Properties();
        }
    }

    @Override
    public void save(Properties properties) {
        try (FileWriter writer = new FileWriter(settingsPath.toFile())) {
            properties.store(writer, "store properties");
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Error saving settings: {0}", e.getMessage());
        }
    }

    @Override
    public void delete() {
        try {
            Files.deleteIfExists(settingsPath);
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Could not delete settings file: {0}", e.getMessage());
        }
    }
}
