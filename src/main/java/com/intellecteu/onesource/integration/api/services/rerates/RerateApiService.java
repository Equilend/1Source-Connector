package com.intellecteu.onesource.integration.api.services.rerates;

import static com.intellecteu.onesource.integration.api.services.rerates.RerateFilterFields.CONTRACT_ID;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateFilterFields.CREATE_DATETIME_FROM;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateFilterFields.CREATE_DATETIME_TILL;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateFilterFields.EFFECTIVE_DATE_FROM;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateFilterFields.EFFECTIVE_DATE_TILL;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateFilterFields.LAST_UPDATE_DATETIME_FROM;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateFilterFields.LAST_UPDATE_DATETIME_TILL;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateFilterFields.MATCHING_SPIRE_TRADE_ID;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateFilterFields.PROCESSING_STATUS;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateFilterFields.RELATED_SPIRE_POSITION_ID;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateFilterFields.RERATE_ID;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateFilterFields.RERATE_STATUS;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateSpecification.contractIdEquals;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateSpecification.createDatetimeFrom;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateSpecification.createDatetimeTill;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateSpecification.effectiveDateFrom;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateSpecification.effectiveDateTill;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateSpecification.lastUpdateDatetimeFrom;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateSpecification.lastUpdateDatetimeTill;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateSpecification.matchingSpireTradeIdEquals;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateSpecification.processingStatusEquals;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateSpecification.relatedSpirePositionIdEquals;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateSpecification.rerateIdEquals;
import static com.intellecteu.onesource.integration.api.services.rerates.RerateSpecification.rerateStatusEquals;

import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import com.intellecteu.onesource.integration.model.onesource.RerateStatus;
import com.intellecteu.onesource.integration.repository.entity.onesource.RerateEntity;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RerateApiService {

    private final RerateApiRepository rerateApiRepository;
    private final OneSourceMapper oneSourceMapper;

    public Page<Rerate> getAll(Pageable pageable, MultiValueMap<String, String> parameters) {
        Specification<RerateEntity> dynamicFieldsFilter = createSpecificationForRerate(parameters);
        Page<RerateEntity> allRerates = rerateApiRepository.findAll(dynamicFieldsFilter, pageable);
        return allRerates.map(oneSourceMapper::toModel);
    }

    private Specification<RerateEntity> createSpecificationForRerate(MultiValueMap<String, String> parameters) {
        return Specification.where(
                StringUtils.isBlank(parameters.getFirst(RERATE_ID)) ? null : rerateIdEquals(parameters.getFirst(RERATE_ID)))
            .and(StringUtils.isBlank(parameters.getFirst(CONTRACT_ID)) ? null
                : contractIdEquals(parameters.getFirst(CONTRACT_ID)))
            .and(StringUtils.isBlank(parameters.getFirst(RERATE_STATUS)) ? null
                : rerateStatusEquals(RerateStatus.valueOf(parameters.getFirst(RERATE_STATUS))))
            .and(StringUtils.isBlank(parameters.getFirst(PROCESSING_STATUS)) ? null : processingStatusEquals(
                ProcessingStatus.valueOf(parameters.getFirst(PROCESSING_STATUS))))
            .and(StringUtils.isBlank(parameters.getFirst(MATCHING_SPIRE_TRADE_ID)) ? null : matchingSpireTradeIdEquals(
                Long.valueOf(parameters.getFirst(MATCHING_SPIRE_TRADE_ID))))
            .and(StringUtils.isBlank(parameters.getFirst(RELATED_SPIRE_POSITION_ID)) ? null
                : relatedSpirePositionIdEquals(
                    Long.valueOf(parameters.getFirst(RELATED_SPIRE_POSITION_ID))))
            .and(StringUtils.isBlank(parameters.getFirst(CREATE_DATETIME_FROM)) ? null : createDatetimeFrom(
                LocalDateTime.parse(parameters.getFirst(CREATE_DATETIME_FROM))))
            .and(StringUtils.isBlank(parameters.getFirst(CREATE_DATETIME_TILL)) ? null : createDatetimeTill(
                LocalDateTime.parse(parameters.getFirst(CREATE_DATETIME_TILL))))
            .and(StringUtils.isBlank(parameters.getFirst(LAST_UPDATE_DATETIME_FROM)) ? null : lastUpdateDatetimeFrom(
                LocalDateTime.parse(parameters.getFirst(LAST_UPDATE_DATETIME_FROM))))
            .and(StringUtils.isBlank(parameters.getFirst(LAST_UPDATE_DATETIME_TILL)) ? null : lastUpdateDatetimeTill(
                LocalDateTime.parse(parameters.getFirst(LAST_UPDATE_DATETIME_TILL))))
            .and(StringUtils.isBlank(parameters.getFirst(EFFECTIVE_DATE_FROM)) ? null : effectiveDateFrom(
                LocalDateTime.parse(parameters.getFirst(EFFECTIVE_DATE_FROM)).toLocalDate()))
            .and(StringUtils.isBlank(parameters.getFirst(EFFECTIVE_DATE_TILL)) ? null : effectiveDateTill(
                LocalDateTime.parse(parameters.getFirst(EFFECTIVE_DATE_TILL)).toLocalDate()));
    }

}
