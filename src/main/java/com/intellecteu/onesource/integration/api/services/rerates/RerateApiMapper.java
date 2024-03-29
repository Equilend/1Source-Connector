package com.intellecteu.onesource.integration.api.services.rerates;

import com.intellecteu.onesource.integration.api.dto.RerateDTO;
import com.intellecteu.onesource.integration.model.onesource.Rerate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RerateApiMapper {

    @Mapping(target = "createDatetime", source = "createUpdateDatetime")
    @Mapping(target = "effectiveDate", ignore = true)
    RerateDTO toDTO(Rerate rerate);

    @AfterMapping
    default void setEffectiveDate(@MappingTarget RerateDTO rerateDTO, Rerate rerate) {
        getRerateEffectiveDate(rerate).ifPresent(red -> rerateDTO.setEffectiveDate(red.atStartOfDay()));
    }

    private Optional<LocalDate> getRerateEffectiveDate(Rerate rerate) {
        if (rerate.getRerate() != null && rerate.getRerate().getRebate() != null
            && rerate.getRerate().getRebate().getFixed() != null) {
            return Optional.of(rerate.getRerate().getRebate().getFixed().getEffectiveDate());
        } else if (rerate.getRerate() != null && rerate.getRerate().getRebate() != null
            && rerate.getRerate().getRebate().getFloating() != null) {
            return Optional.of(rerate.getRerate().getRebate().getFloating().getEffectiveDate());
        }
        return Optional.empty();
    }

}
