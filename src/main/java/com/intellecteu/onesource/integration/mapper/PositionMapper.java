package com.intellecteu.onesource.integration.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.model.spire.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PositionMapper {

  private final ObjectMapper objectMapper;

  public PositionDto toPositionDto(Position position) {
    if (position == null) return null;
    return objectMapper.convertValue(position, PositionDto.class);
  }

  public Position toPosition(JsonNode json) throws JsonProcessingException {
    if (json == null) return null;
    return objectMapper.readValue(json.toString(), Position.class);
  }

}
