package com.intellecteu.onesource.integration.api.contracts;

import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.ACCOUNT_ID;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.COLLATERAL_TABLE;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.CONTRACT_ID;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.CONTRACT_STATUS;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.CURRENCY;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.CUSIP;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.INSTRUMENT_TABLE;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.INTERNAL_REF_ID;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.INTERNAL_REF_TABLE;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.ISIN;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.LAST_UPDATE_DATE_TIME;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.MATCHING_SPIRE_POSITION_ID;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.PARTY_ID;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.PARTY_TABLE;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.PROCESSING_STATUS;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.SEDOL;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.SETTLEMENT_DATE;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.TICKER;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.TRADE_DATE;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.TRADE_TABLE;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.TRANSACTING_PARTY_TABLE;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.TYPE;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.VENUE_REF_KEY;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.VENUE_TABLE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.intellecteu.onesource.integration.api.ProcessingStatus;
import com.intellecteu.onesource.integration.model.onesource.CollateralType;
import com.intellecteu.onesource.integration.model.onesource.ContractStatus;
import com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.client.HttpClientErrorException;

@UtilityClass
class OneSourceLoanContractSpecs {

    static Specification<ContractEntity> contractIdEquals(String contractId) {
        return (root, query, builder) ->
            contractId == null ?
                builder.conjunction() :
                builder.equal(root.get(CONTRACT_ID), contractId);
    }

    static Specification<ContractEntity> contractProcessingStatusEquals(String processingStatus) {
        if (processingStatus == null) {
            return (root, query, builder) -> builder.conjunction();
        }
        ProcessingStatus statusEnum = ProcessingStatus.fromValue(processingStatus.toUpperCase());
        if (statusEnum == null) {
            throw new HttpClientErrorException(BAD_REQUEST,
                String.format("ProcessingStatus: %s is not existed", processingStatus));
        }
        return (root, query, builder) -> builder.equal(root.get(PROCESSING_STATUS), statusEnum);
    }

    static Specification<ContractEntity> contractMatchingSpirePositionIdEquals(String matchingSpirePositionId) {
        return (root, query, builder) ->
            matchingSpirePositionId == null ?
                builder.conjunction() :
                builder.equal(root.get(MATCHING_SPIRE_POSITION_ID), matchingSpirePositionId);
    }

    static Specification<ContractEntity> contractStatusEquals(String contractStatus) {
        if (contractStatus == null) {
            return (root, query, builder) -> builder.conjunction();
        }
        ContractStatus statusEnum = ContractStatus.fromValue(contractStatus.toUpperCase());
        if (statusEnum == null) {
            throw new HttpClientErrorException(BAD_REQUEST,
                String.format("ContractStatus: %s is not existed", contractStatus));
        }
        return (root, query, builder) -> builder.equal(root.get(CONTRACT_STATUS), statusEnum);
    }

    static Specification<ContractEntity> contractLastUpdatePartyIdEquals(String partyId) {
        return (root, query, builder) ->
            partyId == null ?
                builder.conjunction() :
                builder.equal(root.get(MATCHING_SPIRE_POSITION_ID), partyId);
    }

    static Specification<ContractEntity> sinceLastUpdateDate(LocalDate sinceLastUpdateDateTime) {
        return (root, query, builder) ->
            sinceLastUpdateDateTime == null ?
                builder.conjunction() :
                builder.greaterThanOrEqualTo(root.get(LAST_UPDATE_DATE_TIME), sinceLastUpdateDateTime);
    }

    static Specification<ContractEntity> beforeLastUpdateDate(LocalDate beforeLastUpdateDateTime) {
        return (root, query, builder) ->
            beforeLastUpdateDateTime == null ?
                builder.conjunction() :
                builder.lessThan(root.get(LAST_UPDATE_DATE_TIME), beforeLastUpdateDateTime);
    }

    static Specification<ContractEntity> venueRefKeyEquals(String venueRefKey) {
        return (root, query, builder) ->
            venueRefKey == null ?
                builder.conjunction() :
                builder.equal(root.join(TRADE_TABLE).join(VENUE_TABLE).get(VENUE_REF_KEY), venueRefKey);
    }

    static Specification<ContractEntity> instrumentCusipEquals(String cusip) {
        return (root, query, builder) ->
            cusip == null ?
                builder.conjunction() :
                builder.equal(root.join(TRADE_TABLE).join(INSTRUMENT_TABLE).get(CUSIP), cusip);
    }

    static Specification<ContractEntity> instrumentIsinEquals(String isin) {
        return (root, query, builder) ->
            isin == null ?
                builder.conjunction() :
                builder.equal(root.join(TRADE_TABLE).join(INSTRUMENT_TABLE).get(ISIN), isin);
    }

    static Specification<ContractEntity> instrumentSedolEquals(String sedol) {
        return (root, query, builder) ->
            sedol == null ?
                builder.conjunction() :
                builder.equal(root.join(TRADE_TABLE).join(INSTRUMENT_TABLE).get(SEDOL), sedol);
    }

    static Specification<ContractEntity> instrumentTickerEquals(String ticker) {
        return (root, query, builder) ->
            ticker == null ?
                builder.conjunction() :
                builder.equal(root.join(TRADE_TABLE).join(INSTRUMENT_TABLE).get(TICKER), ticker);
    }

    static Specification<ContractEntity> sinceTradeDate(LocalDate sinceTradeDate) {
        return (root, query, builder) ->
            sinceTradeDate == null ?
                builder.conjunction() :
                builder.greaterThanOrEqualTo(root.join(TRADE_TABLE).get(TRADE_DATE), sinceTradeDate);
    }

    static Specification<ContractEntity> beforeTradeDate(LocalDate beforeTradeDate) {
        return (root, query, builder) ->
            beforeTradeDate == null ?
                builder.conjunction() :
                builder.lessThan(root.join(TRADE_TABLE).get(TRADE_DATE), beforeTradeDate);
    }

    static Specification<ContractEntity> sinceSettlementDate(LocalDate sinceSettlementDate) {
        return (root, query, builder) ->
            sinceSettlementDate == null ?
                builder.conjunction() :
                builder.greaterThanOrEqualTo(root.join(TRADE_TABLE).get(SETTLEMENT_DATE), sinceSettlementDate);
    }

    static Specification<ContractEntity> beforeSettlementDate(LocalDate beforeSettlementDate) {
        return (root, query, builder) ->
            beforeSettlementDate == null ?
                builder.conjunction() :
                builder.lessThan(root.join(TRADE_TABLE).get(SETTLEMENT_DATE), beforeSettlementDate);
    }

    static Specification<ContractEntity> collateralTypeEquals(String collateralType) {
        if (collateralType == null) {
            return (root, query, builder) -> builder.conjunction();
        }
        CollateralType collateralTypeEnum = CollateralType.fromValue(collateralType);
        if (collateralTypeEnum == null) {
            throw new HttpClientErrorException(BAD_REQUEST,
                String.format("CollateralType: %s is not existed", collateralType));
        }
        return (root, query, builder) -> builder.equal(
            root.join(TRADE_TABLE).join(COLLATERAL_TABLE).get(TYPE), collateralTypeEnum);
    }

    static Specification<ContractEntity> collateralCurrencyEquals(String currency) {
        return (root, query, builder) ->
            currency == null ?
                builder.conjunction() :
                builder.equal(root.join(TRADE_TABLE).join(COLLATERAL_TABLE).get(CURRENCY), currency.toUpperCase());
    }

    static Specification<ContractEntity> internalPartyIdEquals(String partyId) {
        return (root, query, builder) ->
            partyId == null ?
                builder.conjunction() :
                builder.equal(
                    root.join(TRADE_TABLE).join(TRANSACTING_PARTY_TABLE).join(PARTY_TABLE).get(PARTY_ID),
                    partyId);
    }

    static Specification<ContractEntity> internalRefAccountIdEquals(String accountId) {
        return (root, query, builder) ->
            accountId == null ?
                builder.conjunction() :
                builder.equal(
                    root.join(TRADE_TABLE).join(TRANSACTING_PARTY_TABLE).join(INTERNAL_REF_TABLE).get(ACCOUNT_ID),
                    accountId);
    }

    static Specification<ContractEntity> internalRefIdEquals(String internalRefId) {
        return (root, query, builder) ->
            internalRefId == null ?
                builder.conjunction() :
                builder.equal(
                    root.join(TRADE_TABLE).join(TRANSACTING_PARTY_TABLE).join(INTERNAL_REF_TABLE).get(INTERNAL_REF_ID),
                    internalRefId);
    }
}
