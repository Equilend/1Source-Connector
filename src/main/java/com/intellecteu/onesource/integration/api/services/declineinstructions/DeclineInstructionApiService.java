package com.intellecteu.onesource.integration.api.services.declineinstructions;

import com.intellecteu.onesource.integration.api.dto.DeclineInstructionDto;
import com.intellecteu.onesource.integration.api.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

public interface DeclineInstructionApiService {

    PageResponse<DeclineInstructionDto> getAllInstructions(Pageable pageable, MultiValueMap<String, String> parameters);

    DeclineInstructionDto createDeclineInstruction(DeclineInstructionDto declineInstructionDto);
}
