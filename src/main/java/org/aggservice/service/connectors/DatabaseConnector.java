package org.aggservice.service.connectors;

import org.aggservice.config.DataSourcesConfig;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Interface for connecting to a custom database and fetching data.
 */
public interface DatabaseConnector {

  boolean supports(DatabaseStrategy strategy);

  DatabaseConnector withConfig(DataSourcesConfig.DataSourceProperties config);

  List<Map<String, Object>> fetchData();

  default String buildColumnsMapping(Map<String, String> fieldMapping) {
    return fieldMapping.entrySet().stream()
            .map(entry -> entry.getValue() + " AS " + entry.getKey())
            .collect(Collectors.joining(", "));
  }

}
