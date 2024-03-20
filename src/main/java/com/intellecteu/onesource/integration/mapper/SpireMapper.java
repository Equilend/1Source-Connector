package com.intellecteu.onesource.integration.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.services.client.spire.dto.PositionOutDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Deprecated(since = "0.0.5-SNAPSHOT", forRemoval = true)
public class SpireMapper {

    private final ObjectMapper objectMapper;

    public PositionDto toPositionDto(Position position) {
        if (position == null) {
            return null;
        }
        return objectMapper.convertValue(position, PositionDto.class);
    }

    public Position toPosition(PositionDto positionDto) {
        if (positionDto == null) {
            return null;
        }
        return objectMapper.convertValue(positionDto, Position.class);
    }

    public Position toPosition(PositionOutDTO positionOutDTO) {
        if (positionOutDTO == null) {
            return null;
        }
        return objectMapper.convertValue(positionOutDTO, Position.class);
    }

    public PositionDto jsonToPositionDto(JsonNode json) throws JsonProcessingException {
        if (json == null) {
            return null;
        }
        return objectMapper.readValue(json.toString(), PositionDto.class);
    }


}
