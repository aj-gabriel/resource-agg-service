package org.aggservice.service.connectors.impl;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aggservice.service.connectors.DatabaseStrategy;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.aggservice.config.DataSourcesConfig.DataSourceProperties;

/**
 * PostgresDatabaseConnector class represents a connector specific to PostgreSQL database.
 * It extends AbstractDatabaseConnector and implements methods for supporting PostgreSQL database strategies,
 * fetching data from PostgreSQL database and building column mappings.
 */
@Slf4j
@NoArgsConstructor
public class PostgresDatabaseConnector extends AbstractDatabaseConnector {

  /**
   * Check if the given DatabaseStrategy is supported.
   *
   * @param strategy The DatabaseStrategy to check for support.
   * @return true if the given DatabaseStrategy is POSTGRES, false otherwise.
   */
  @Override
  public boolean supports(DatabaseStrategy strategy) {
    return DatabaseStrategy.POSTGRES.equals(strategy);
  }

  /**
   * Fetches data from the database based on the provided configuration.
   *
   * @return A list of maps where each map represents a row of data with column names as keys and values as values.
   * Returns an empty list if an error occurs.
   */
  @Override
  public List<Map<String, Object>> fetchData() {
    DataSourceProperties config = getConfig();

    Map<String, String> mapping = config.getMapping();
    String table = config.getTable();

    String query = "SELECT " + buildColumnsMapping(mapping) + " FROM " + table;
    try {
      if (validateConnection(config) && validateSchemaConsistency(config)) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());

        return jdbcTemplate.query(query, (rs, rowNum) ->
                Map.of("id", rs.getString("id"),
                        "username", rs.getString("username"),
                        "name", rs.getString("name"),
                        "surname", rs.getString("surname"))
        );
      }

      return Collections.emptyList();
    } catch (Exception e) {//silent error handling
      log.error("Unable to execute query {} to database with name: {}", query, config.getName(), e);
      return Collections.emptyList();
    }

  }

}
