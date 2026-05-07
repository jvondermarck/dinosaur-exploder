/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;

/**
 * Central image cache to avoid reloading the same images multiple times. Prevents OutOfMemoryError
 * in web (JPro) deployments where images are large PNG files.
 */
public class ImageCache {
  private ImageCache() {}

  private static final Map<String, Image> cache = new HashMap<>();

  /**
   * Returns a cached Image loaded from the classpath. Loads it only once.
   *
   * @param resourcePath classpath path e.g. "assets/textures/coin.png"
   * @return the cached Image
   */
  public static Image get(String resourcePath) {
    return cache.computeIfAbsent(resourcePath, ImageCache::load);
  }

  /**
   * Returns a cached Image with specific dimensions. The key includes dimensions to allow different
   * sizes of the same image.
   */
  public static Image get(String resourcePath, double width, double height) {
    String key = resourcePath + "@" + (int) width + "x" + (int) height;
    return cache.computeIfAbsent(key, k -> load(resourcePath, width, height));
  }

  /** Clears the cache (call between game sessions if needed). */
  public static void clear() {
    cache.clear();
  }

  private static Image load(String resourcePath) {
    InputStream stream = ImageCache.class.getClassLoader().getResourceAsStream(resourcePath);
    if (stream == null) {
      // fallback: try with leading slash
      stream = ImageCache.class.getResourceAsStream("/" + resourcePath);
    }
    if (stream == null) {
      throw new IllegalArgumentException("Image resource not found: " + resourcePath);
    }
    return new Image(stream);
  }

  private static Image load(String resourcePath, double width, double height) {
    InputStream stream = ImageCache.class.getClassLoader().getResourceAsStream(resourcePath);
    if (stream == null) {
      stream = ImageCache.class.getResourceAsStream("/" + resourcePath);
    }
    if (stream == null) {
      throw new IllegalArgumentException("Image resource not found: " + resourcePath);
    }
    return new Image(stream, width, height, false, false);
  }
}
