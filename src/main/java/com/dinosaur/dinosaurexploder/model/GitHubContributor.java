/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

/**
 * Model class representing a GitHub contributor. Immutable data object following clean architecture
 * principles.
 */
public class GitHubContributor {
  private final String login;
  private final String avatarUrl;
  private final int contributions;
  private final String htmlUrl;

  public GitHubContributor(String login, String avatarUrl, int contributions, String htmlUrl) {
    this.login = login;
    this.avatarUrl = avatarUrl;
    this.contributions = contributions;
    this.htmlUrl = htmlUrl;
  }

  public String getLogin() {
    return login;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public int getContributions() {
    return contributions;
  }

  public String getHtmlUrl() {
    return htmlUrl;
  }

  @Override
  public String toString() {
    return "GitHubContributor{"
        + "login='"
        + login
        + '\''
        + ", contributions="
        + contributions
        + '}';
  }
}
