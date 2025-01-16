package org.aggservice.service.connectors;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DatabaseStrategy {
  POSTGRES("postgres"),
  ORACLE("oracle"),
  MYSQL("mysql");


  private final String strategyName;

  DatabaseStrategy(String strategyName) {
    this.strategyName = strategyName;
  }

  @JsonCreator
  public static DatabaseStrategy fromString(String value) {
    for (DatabaseStrategy strategy : values()) {
      if (strategy.strategyName.equalsIgnoreCase(value)) {
        return strategy;
      }
    }
    throw new IllegalArgumentException("Unknown strategy: " + value);
  }

}
