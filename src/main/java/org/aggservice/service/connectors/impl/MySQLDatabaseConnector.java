package org.aggservice.service.connectors.impl;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aggservice.service.connectors.DatabaseStrategy;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.aggservice.config.DataSourcesConfig.DataSourceProperties;

/**
 * MySQLDatabaseConnector extends AbstractDatabaseConnector to provide
 * functionality specific to connecting and fetching data from MySQL databases.
 */
@Slf4j
@NoArgsConstructor
public class MySQLDatabaseConnector extends AbstractDatabaseConnector {

  /**
   * Check if the specified DatabaseStrategy is supported by this connector.
   *
   * @param strategy The DatabaseStrategy to check for support.
   * @return true if the DatabaseStrategy is MYSQL, false otherwise.
   */
  @Override
  public boolean supports(DatabaseStrategy strategy) {
    return DatabaseStrategy.MYSQL.equals(strategy);
  }

  /**
   * Fetches data from the database based on the provided configuration.
   *
   * @return A list of maps where each map represents a row of data with column names as keys and values as values.
   * Returns an empty list if an error occurs.
   */
  @Override
  public List<Map<String, Object>> fetchData() {
    DataSourceProperties config = this.getConfig();
    String query = "SELECT " + buildColumnsMapping(config.getMapping()) + " FROM " + config.getTable();
    try {
      if (validateConnection(config) && validateSchemaConsistency(config)) {
        DataSource dataSource = getDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

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
