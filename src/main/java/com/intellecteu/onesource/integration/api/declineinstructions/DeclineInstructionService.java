package com.intellecteu.onesource.integration.api.declineinstructions;

import com.intellecteu.onesource.integration.api.dto.DeclineInstruction;
import com.intellecteu.onesource.integration.api.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

public interface DeclineInstructionService {

    PageResponse<DeclineInstruction> getAllInstructions(Pageable pageable, MultiValueMap<String, String> parameters);

    DeclineInstruction createDeclineInstruction(DeclineInstruction declineInstruction);
}
