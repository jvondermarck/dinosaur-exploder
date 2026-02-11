/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.exception;

public class LockedShipException extends RuntimeException {
  public LockedShipException(String message) {
    super(message);
  }
}
