package org.aggservice.service.connectors.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aggservice.service.connectors.DatabaseConnector;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.aggservice.config.DataSourcesConfig.DataSourceProperties;

/**
 * This abstract class serves as a base for database connectors.
 * It provides methods to initialize a data source,
 * validate the connection to the target database, and handle connection-related errors.
 */
@Slf4j
@Getter
public abstract class AbstractDatabaseConnector implements DatabaseConnector {

  private DataSource dataSource;
  private DataSourceProperties config;

  @Override
  public DatabaseConnector withConfig(DataSourceProperties config) {
    return initDataSource(config);
  }

  protected DatabaseConnector initDataSource(DataSourceProperties config) {
    try {
      this.config = config;
      HikariConfig hikariConfig = new HikariConfig();

      hikariConfig.setJdbcUrl(config.getUrl());
      hikariConfig.setUsername(config.getUser());
      hikariConfig.setPassword(config.getPassword());

      dataSource = new HikariDataSource(hikariConfig);

      return validateConnection(config) ? this : null;
    } catch (Exception e) {//silent error handling
      log.error("Unable to connect to database with name: {}", config.getName(), e);
      return null;
    }
  }

  //validate if target DB available
  protected boolean validateConnection(DataSourceProperties config) {
    try (Connection connection = dataSource.getConnection()) {
      if (!connection.isValid(5)) {
        log.error("Failed to validate connection to DB with name {}", config.getName());
        return false;
      }
      return true;
    } catch (SQLException e) {
      log.error("Unable to connect to database with name: {}", config.getName(), e);
      return false;
    }
  }

  //method should validate if requested fields are present in target DB table
  //implementation omitted for simplification
  protected boolean validateSchemaConsistency(DataSourceProperties config) {
    return true;
  }

}
