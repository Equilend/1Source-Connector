package com.intellecteu.onesource.integration.api.services.nackinstructions;

import static com.intellecteu.onesource.integration.api.services.nackinstructions.NackInstructionFilterFields.RETURN_ID;

import com.intellecteu.onesource.integration.mapper.NackInstructionMapper;
import com.intellecteu.onesource.integration.model.integrationtoolkit.NackInstruction;
import com.intellecteu.onesource.integration.repository.entity.toolkit.NackInstructionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class NackInstructionApiService {

    private final NackInstructionApiRepository nackInstructionRepository;
    private final NackInstructionMapper mapper;


    public Page<NackInstruction> getNackInstructions(Pageable pageable, MultiValueMap<String, String> parameters) {
        Specification<NackInstructionEntity> dynamicFieldsFilter = createSpecificationForNackInstruction(parameters);
        Page<NackInstructionEntity> all = nackInstructionRepository.findAll(dynamicFieldsFilter, pageable);
        return all.map(mapper::toModel);
    }

    public NackInstruction save(NackInstruction nackInstruction) {
        NackInstructionEntity nackInstructionEntity = mapper.toEntity(nackInstruction);
        log.debug("Saving correctionInstructionEntity with id: {}", nackInstructionEntity.getNackInstructionId());
        nackInstructionEntity = nackInstructionRepository.save(nackInstructionEntity);
        return mapper.toModel(nackInstructionEntity);
    }

    private Specification<NackInstructionEntity> createSpecificationForNackInstruction(
        MultiValueMap<String, String> parameters) {
        return Specification.where(
            StringUtils.isBlank(parameters.getFirst(RETURN_ID)) ? null
                : (root, query, builder) -> builder.equal(root.get(RETURN_ID), parameters.getFirst(RETURN_ID)));
    }
}
