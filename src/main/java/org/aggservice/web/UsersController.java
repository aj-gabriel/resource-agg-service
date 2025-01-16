package org.aggservice.web;

import lombok.RequiredArgsConstructor;
import org.aggservice.model.dto.UserResponseDTO;
import org.aggservice.service.DataAggregatorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UsersController {

  private final DataAggregatorService dataAggregatorService;

  @GetMapping("")
  public ResponseEntity<List<UserResponseDTO>> getConsolidatedInfo() {
    List<UserResponseDTO> data = dataAggregatorService.getConsolidatedData();
    if (data.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(data);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneralException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("An unexpected error occurred: " + ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Invalid request: " + ex.getMessage());
  }

}
