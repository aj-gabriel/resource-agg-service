package org.aggservice.service;

import org.aggservice.model.dto.UserResponseDTO;

import java.util.List;

public interface DataAggregatorService {
  List<UserResponseDTO> getConsolidatedData();
}
