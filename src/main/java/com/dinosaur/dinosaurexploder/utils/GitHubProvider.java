/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.model.GitHubContributor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service provider for fetching GitHub repository contributors. Handles asynchronous API
 * communication with proper error handling.
 */
public class GitHubProvider {
  private static final Logger LOGGER = Logger.getLogger(GitHubProvider.class.getName());
  private static final String GITHUB_API_URL =
      "https://api.github.com/repos/jvondermarck/dinosaur-exploder/contributors";
  private static final int TIMEOUT_MS = 10000;

  private final ObjectMapper objectMapper;

  public GitHubProvider() {
    this.objectMapper = new ObjectMapper();
  }

  public CompletableFuture<List<GitHubContributor>> fetchContributorsAsync() {
    return CompletableFuture.supplyAsync(this::fetchContributors);
  }

  private List<GitHubContributor> fetchContributors() {
    List<GitHubContributor> contributors = new ArrayList<>();

    try {
      URL url = new URL(GITHUB_API_URL);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(TIMEOUT_MS);
      connection.setReadTimeout(TIMEOUT_MS);
      connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
      connection.setRequestProperty("User-Agent", "Dinosaur-Exploder-Game");

      int responseCode = connection.getResponseCode();

      if (responseCode == HttpURLConnection.HTTP_OK) {
        try (BufferedReader reader =
            new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
          StringBuilder response = new StringBuilder();
          String line;
          while ((line = reader.readLine()) != null) {
            response.append(line);
          }

          contributors = parseContributors(response.toString());
          LOGGER.log(Level.INFO, "Successfully fetched {0} contributors", contributors.size());
        }
      } else {
        LOGGER.log(Level.WARNING, "GitHub API returned response code: {0}", responseCode);
      }

      connection.disconnect();

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error fetching contributors from GitHub API", e);
    }

    return contributors;
  }

  private List<GitHubContributor> parseContributors(String jsonResponse) {
    List<GitHubContributor> contributors = new ArrayList<>();

    try {
      JsonNode rootNode = objectMapper.readTree(jsonResponse);

      for (JsonNode node : rootNode) {
        String login = node.get("login").asText();
        String avatarUrl = node.get("avatar_url").asText();
        int contributions = node.get("contributions").asInt();
        String htmlUrl = node.get("html_url").asText();

        contributors.add(new GitHubContributor(login, avatarUrl, contributions, htmlUrl));
      }

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Error parsing contributor JSON", e);
    }

    return contributors;
  }
}
