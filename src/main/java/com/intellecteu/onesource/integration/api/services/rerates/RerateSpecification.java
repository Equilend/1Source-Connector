package com.intellecteu.onesource.integration.api.services.rerates;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.RerateStatus;
import com.intellecteu.onesource.integration.repository.entity.onesource.RerateEntity;
import jakarta.persistence.criteria.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class RerateSpecification {

    public static Specification<RerateEntity> rerateIdEquals(String rerateId) {
        return (root, query, builder) -> builder.equal(root.get("rerateId"), rerateId);
    }

    public static Specification<RerateEntity> contractIdEquals(String contractId) {
        return (root, query, builder) -> builder.equal(root.get("contractId"), contractId);
    }

    public static Specification<RerateEntity> rerateStatusEquals(RerateStatus rerateStatus) {
        return (root, query, builder) -> builder.equal(root.get("rerateStatus"), rerateStatus);
    }

    public static Specification<RerateEntity> processingStatusEquals(ProcessingStatus processingStatus) {
        return (root, query, builder) -> builder.equal(root.get("processingStatus"), processingStatus);
    }

    public static Specification<RerateEntity> matchingSpireTradeIdEquals(Long matchingSpireTradeId) {
        return (root, query, builder) -> builder.equal(root.get("matchingSpireTradeId"), matchingSpireTradeId);
    }

    public static Specification<RerateEntity> relatedSpirePositionIdEquals(Long relatedSpirePositionId) {
        return (root, query, builder) -> builder.equal(root.get("relatedSpirePositionId"), relatedSpirePositionId);
    }

    public static Specification<RerateEntity> createDatetimeFrom(LocalDateTime createDatetimeFrom) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("createUpdateDatetime"),
            createDatetimeFrom);
    }

    public static Specification<RerateEntity> createDatetimeTill(LocalDateTime createDatetimeTill) {
        return (root, query, builder) -> builder.lessThan(root.get("createUpdateDatetime"), createDatetimeTill);
    }

    public static Specification<RerateEntity> lastUpdateDatetimeFrom(LocalDateTime lastUpdateDatetimeFrom) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("lastUpdateDatetime"),
            lastUpdateDatetimeFrom);
    }

    public static Specification<RerateEntity> lastUpdateDatetimeTill(LocalDateTime lastUpdateDatetimeTill) {
        return (root, query, builder) -> builder.lessThan(root.get("lastUpdateDatetime"), lastUpdateDatetimeTill);
    }

    public static Specification<RerateEntity> effectiveDateFrom(LocalDate effectiveDateFrom) {
        return (fixedEffectiveDateIsNotNull().and(fixedEffectiveDateFrom(effectiveDateFrom))).or(fixedEffectiveDateIsNotNull().and(fixedEffectiveDateFrom(effectiveDateFrom)));
    }

    public static Specification<RerateEntity> effectiveDateTill(LocalDate effectiveDateTill) {
        return (fixedEffectiveDateIsNotNull().and(fixedEffectiveDateTill(effectiveDateTill))).or(fixedEffectiveDateIsNotNull().and(fixedEffectiveDateTill(effectiveDateTill)));
    }

    public static Specification<RerateEntity> fixedEffectiveDateIsNotNull() {
        return (root, query, builder) -> {
            Path<LocalDate> fixedEffectiveRate = root.join("rate").join("rebate").join("fixed")
                .get("effectiveDate");
            return builder.isNotNull(fixedEffectiveRate);

        };
    }

    public static Specification<RerateEntity> fixedEffectiveDateFrom(LocalDate effectiveDateFrom) {
        return (root, query, builder) -> {
            Path<LocalDate> fixedEffectiveRate = root.join("rate").join("rebate").join("fixed")
                .get("effectiveDate");
            return builder.greaterThanOrEqualTo(fixedEffectiveRate, effectiveDateFrom);
        };
    }

    public static Specification<RerateEntity> fixedEffectiveDateTill(LocalDate effectiveDateTill) {
        return (root, query, builder) -> {
            Path<LocalDate> fixedEffectiveRate = root.join("rate").join("rebate").join("fixed")
                .get("effectiveDate");
            return builder.lessThan(fixedEffectiveRate, effectiveDateTill);
        };
    }

    public static Specification<RerateEntity> floatingEffectiveDateIsNotNull() {
        return (root, query, builder) -> {
            Path<LocalDate> floatingEffectiveRate = root.join("rate").join("rebate").join("floating")
                .get("effectiveDate");
            return builder.isNotNull(floatingEffectiveRate);
        };
    }

    public static Specification<RerateEntity> floatingEffectiveDateFrom(LocalDate effectiveDateFrom) {
        return (root, query, builder) -> {
            Path<LocalDate> floatingEffectiveRate = root.join("rate").join("rebate").join("floating")
                .get("effectiveDate");
            return builder.greaterThanOrEqualTo(floatingEffectiveRate, effectiveDateFrom);
        };
    }

    public static Specification<RerateEntity> floatingEffectiveDateTill(LocalDate effectiveDateTill) {
        return (root, query, builder) -> {
            Path<LocalDate> floatingEffectiveRate = root.join("rate").join("rebate").join("floating")
                .get("effectiveDate");
            return builder.lessThan(floatingEffectiveRate, effectiveDateTill);
        };
    }

}
