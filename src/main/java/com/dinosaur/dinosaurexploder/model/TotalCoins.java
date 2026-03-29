/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import java.io.Serializable;

public class TotalCoins implements Serializable {
  private Integer total;

  public TotalCoins() {
    this.total = 0;
  }

  public TotalCoins(Integer total) {
    this.total = total;
  }

  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }
}
