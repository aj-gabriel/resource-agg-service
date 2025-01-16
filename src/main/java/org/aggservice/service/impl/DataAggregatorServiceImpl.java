package org.aggservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aggservice.model.dto.UserResponseDTO;
import org.aggservice.service.DataAggregatorService;
import org.aggservice.service.connectors.DatabaseConnector;
import org.aggservice.service.connectors.DatabaseConnectorFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DataAggregatorServiceImpl is a service implementation that aggregates data from multiple database connections.
 * It utilizes parallel request processing to fetch data from different database connectors.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataAggregatorServiceImpl implements DataAggregatorService {

  private final ObjectMapper objectMapper;
  private final DatabaseConnectorFactory databaseConnectorFactory;

  //method uses parallel request processing
  //for sake of simplification current implementation is just on of possible solutions
  //and shouldn't be used in production due to multiple potential issues, like lack of threads, increased memory consumption.
  //for production purposes non-blocking reactive approach should be used instead,
  //like using r2dbc drivers where it's possible and/or Mono.fromCallable() from Project reactor if r2dbc drivers aren't available.
  @Override
  public List<UserResponseDTO> getConsolidatedData() {

    return databaseConnectorFactory
            .getConnectors()
            .values()
            .stream()
            .parallel()
            .map(DatabaseConnector::fetchData)
            .toList()
            .stream()
            .map(data -> objectMapper.convertValue(data, UserResponseDTO.class))
            .collect(Collectors.toList());

  }
}
