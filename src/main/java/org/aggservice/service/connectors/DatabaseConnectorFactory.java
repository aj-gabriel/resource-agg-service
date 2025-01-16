package org.aggservice.service.connectors;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aggservice.config.DataSourcesConfig;
import org.aggservice.service.connectors.impl.MySQLDatabaseConnector;
import org.aggservice.service.connectors.impl.PostgresDatabaseConnector;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.aggservice.config.DataSourcesConfig.*;

/**
 * DatabaseConnectorFactory is a component responsible for initializing database connectors based on configured data sources.
 * It creates instances of DatabaseConnector for each data source and populates the connector mapping for specific database strategies.
 * The factory utilizes reflection to dynamically create connectors based on the specified strategy.
 */
@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class DatabaseConnectorFactory {

  private final DataSourcesConfig dataSourceProperties;

  private final Map<String, DatabaseConnector> connectors = new ConcurrentHashMap<>();
  private Map<DatabaseStrategy, Class<? extends DatabaseConnector>> connectorMapping;

  /**
   * Initializes the database connectors based on the configured data sources.
   * Creates DatabaseConnector instances for each data source and populates the connector mapping.
   * Logs the total number of data sources and initialized connectors.
   */
  @PostConstruct
  public void initConnectors() {
    this.connectorMapping = Map.of(
            DatabaseStrategy.POSTGRES, PostgresDatabaseConnector.class,
            DatabaseStrategy.MYSQL, MySQLDatabaseConnector.class
    );

    for (DataSourceProperties config : dataSourceProperties.getDataSources()) {
      DatabaseConnector connector = createConnector(config);
      if (connector != null) {
        connectors.put(config.getName(), connector);
      }
    }

    log.info("Total configs: {}. Initialized database connectors {}",
            dataSourceProperties.getDataSources().size(),
            connectors.size());
  }

  /**
   * Creates a DatabaseConnector based on the provided data source configuration.
   *
   * @param config The data source configuration containing properties such as strategy, url, table, user, password, and mapping.
   * @return The created DatabaseConnector with the specified configuration.
   * @throws IllegalArgumentException if no connector class is found for the given strategy.
   * @throws IllegalStateException if an error occurs while creating the connector with the specified strategy.
   */
  private DatabaseConnector createConnector(DataSourceProperties config) {
    DatabaseStrategy databaseStrategy = DatabaseStrategy.fromString(config.getStrategy());

    Class<? extends DatabaseConnector> connectorClass = connectorMapping.get(databaseStrategy);
    if (connectorClass == null) {
      //if there is no proper Database connector, then we shouldn't start application at all
      log.error("No connector found for strategy: {}", databaseStrategy);
      throw new IllegalArgumentException("No connector found for strategy: " + databaseStrategy);
    }

    try {
      // instantiation through reflection API is expensive, but it happens only once on bean init
      // so maybe ignored as non-critical. If, for whatever reason, this approach is not suitable
      // then little refactoring should be done, e.g. to support dynamic resource add/remove
      DatabaseConnector connector = connectorClass.getDeclaredConstructor().newInstance();
      return connector.withConfig(config);
    } catch (Exception e) {
      log.error("Failed to create connector for strategy: {}", databaseStrategy, e);
      throw new IllegalStateException("Failed to create connector for strategy: " + databaseStrategy, e);
    }
  }
}
