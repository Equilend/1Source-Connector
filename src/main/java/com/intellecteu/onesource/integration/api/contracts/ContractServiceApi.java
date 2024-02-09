package com.intellecteu.onesource.integration.api.contracts;

import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.ACCOUNT_ID;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.BEFORE_LAST_UPDATE_DATE;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.BEFORE_SETTLEMENT_DATE;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.BEFORE_TRADE_DATE;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.COLLATERAL_TYPE;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.CONTRACT_ID;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.CONTRACT_STATUS;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.CUSIP;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.C_CURRENCY;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.INTERNAL_PARTY_ID;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.INTERNAL_REF_ID;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.ISIN;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.LAST_UPDATE_PARTY_ID;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.MATCHING_SPIRE_POSITION_ID;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.PROCESSING_STATUS;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.SEDOL;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.SINCE_LAST_UPDATE_DATE;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.SINCE_SETTLEMENT_DATE;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.SINCE_TRADE_DATE;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.TICKER;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractFields.VENUE_REF_KEY;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.beforeLastUpdateDate;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.beforeSettlementDate;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.beforeTradeDate;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.collateralCurrencyEquals;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.collateralTypeEquals;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.contractIdEquals;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.contractLastUpdatePartyIdEquals;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.contractMatchingSpirePositionIdEquals;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.contractProcessingStatusEquals;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.contractStatusEquals;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.instrumentCusipEquals;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.instrumentIsinEquals;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.instrumentSedolEquals;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.instrumentTickerEquals;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.internalPartyIdEquals;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.internalRefAccountIdEquals;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.internalRefIdEquals;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.sinceLastUpdateDate;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.sinceSettlementDate;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.sinceTradeDate;
import static com.intellecteu.onesource.integration.api.contracts.OneSourceLoanContractSpecs.venueRefKeyEquals;

import com.intellecteu.onesource.integration.api.dto.PageResponse;
import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.onesource.Contract;
import com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class ContractServiceApi {

    private final ContractRepositoryApi contractRepository;
    private final OneSourceMapper oneSourceMapper;

    public PageResponse<Contract> getAllContracts(Pageable pageable, MultiValueMap<String, String> parameters) {
        Specification<ContractEntity> dynamicFieldsFilter = createSpecificationForContract(parameters);
        Page<ContractEntity> contracts = contractRepository.findAll(dynamicFieldsFilter, pageable);
        if (!parameters.isEmpty() && contracts.isEmpty()) {
            throw new EntityNotFoundException();
        }
        log.debug("Found {} contracts", contracts.getTotalElements());
        final Page<Contract> contractPage = contracts.map(oneSourceMapper::toModel);
        return PageResponse.<Contract>builder()
            .totalItems(contractPage.getTotalElements())
            .currentPage(contractPage.getPageable().getPageNumber())
            .totalPages(contractPage.getTotalPages())
            .items(contractPage.getContent())
            .build();
    }

    public Contract getContractById(String id) {
        Optional<ContractEntity> contract = contractRepository.getByContractId(id);
        return contract.map(oneSourceMapper::toModel).orElseThrow(EntityNotFoundException::new);
    }

    private Specification<ContractEntity> createSpecificationForContract(MultiValueMap<String, String> parameters) {
        String contractId = parameters.getFirst(CONTRACT_ID);
        String processingStatus = parameters.getFirst(PROCESSING_STATUS);
        String matchingSpirePositionId = parameters.getFirst(MATCHING_SPIRE_POSITION_ID);
        String contractStatus = parameters.getFirst(CONTRACT_STATUS);
        String lastUpdatePartyId = parameters.getFirst(LAST_UPDATE_PARTY_ID);
        String venueRefKey = parameters.getFirst(VENUE_REF_KEY);
        String cusip = parameters.getFirst(CUSIP);
        String isin = parameters.getFirst(ISIN);
        String sedol = parameters.getFirst(SEDOL);
        String ticker = parameters.getFirst(TICKER);
        String collateralType = parameters.getFirst(COLLATERAL_TYPE);
        String collateralCurrency = parameters.getFirst(C_CURRENCY);
        String internalPartyId = parameters.getFirst(INTERNAL_PARTY_ID);
        String internalRefAccountId = parameters.getFirst(ACCOUNT_ID);
        String internalRefId = parameters.getFirst(INTERNAL_REF_ID);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String sinceLastUpdate = parameters.getFirst(SINCE_LAST_UPDATE_DATE);
        LocalDate sinceLastUpdateDate = null;
        if (sinceLastUpdate != null) {
            sinceLastUpdateDate = LocalDate.parse(sinceLastUpdate, dateFormatter);
        }
        String beforeLastUpdate = parameters.getFirst(BEFORE_LAST_UPDATE_DATE);
        LocalDate beforeLastUpdateDate = null;
        if (beforeLastUpdate != null) {
            beforeLastUpdateDate = LocalDate.parse(beforeLastUpdate, dateFormatter);
        }

        String sinceTradeDate = parameters.getFirst(SINCE_TRADE_DATE);
        LocalDate sinceTradeDateTime = null;
        if (sinceTradeDate != null) {
            sinceTradeDateTime = LocalDate.parse(sinceTradeDate, dateFormatter);
        }
        String beforeTradeDate = parameters.getFirst(BEFORE_TRADE_DATE);
        LocalDate beforeTradeDateTime = null;
        if (beforeTradeDate != null) {
            beforeTradeDateTime = LocalDate.parse(beforeTradeDate, dateFormatter);
        }

        String sinceSettlementDate = parameters.getFirst(SINCE_SETTLEMENT_DATE);
        LocalDate sinceSettlementDateTime = null;
        if (sinceSettlementDate != null) {
            sinceSettlementDateTime = LocalDate.parse(sinceSettlementDate, dateFormatter);
        }
        String beforeSettlementDate = parameters.getFirst(BEFORE_SETTLEMENT_DATE);
        LocalDate beforeSettlementDateTime = null;
        if (beforeSettlementDate != null) {
            beforeSettlementDateTime = LocalDate.parse(beforeSettlementDate, dateFormatter);
        }

        Specification<ContractEntity> contractIdSpec = contractIdEquals(contractId);
        Specification<ContractEntity> processingStatusSpec = contractProcessingStatusEquals(processingStatus);
        Specification<ContractEntity> matchingSpirePositionIdSpec = contractMatchingSpirePositionIdEquals(
            matchingSpirePositionId);
        Specification<ContractEntity> contractStatusSpec = contractStatusEquals(contractStatus);
        Specification<ContractEntity> lastUpdatePartyIdSpec = contractLastUpdatePartyIdEquals(lastUpdatePartyId);
        Specification<ContractEntity> venueRefKeySpec = venueRefKeyEquals(venueRefKey);
        Specification<ContractEntity> cusipSpec = instrumentCusipEquals(cusip);
        Specification<ContractEntity> isinSpec = instrumentIsinEquals(isin);
        Specification<ContractEntity> sedolSpec = instrumentSedolEquals(sedol);
        Specification<ContractEntity> tickerSpec = instrumentTickerEquals(ticker);
        Specification<ContractEntity> collateralTypeSpec = collateralTypeEquals(collateralType);
        Specification<ContractEntity> collateralCurrencySpec = collateralCurrencyEquals(collateralCurrency);
        Specification<ContractEntity> internalPartyIdSpec = internalPartyIdEquals(internalPartyId);
        Specification<ContractEntity> internalRefAccountIdSpec = internalRefAccountIdEquals(internalRefAccountId);
        Specification<ContractEntity> internalRefIdSpec = internalRefIdEquals(internalRefId);

        Specification<ContractEntity> sinceLastUpdateDateSpec = sinceLastUpdateDate(sinceLastUpdateDate);
        Specification<ContractEntity> beforeLastUpdateDateSpec = beforeLastUpdateDate(beforeLastUpdateDate);
        Specification<ContractEntity> sinceTradeDateSpec = sinceTradeDate(sinceTradeDateTime);
        Specification<ContractEntity> beforeTradeDateSpec = beforeTradeDate(beforeTradeDateTime);
        Specification<ContractEntity> sinceSettlementDateSpec = sinceSettlementDate(sinceSettlementDateTime);
        Specification<ContractEntity> beforeSettlementDateSpec = beforeSettlementDate(beforeSettlementDateTime);

        return Specification
            .where(contractIdSpec)
            .and(processingStatusSpec)
            .and(matchingSpirePositionIdSpec)
            .and(contractStatusSpec)
            .and(lastUpdatePartyIdSpec)
            .and(venueRefKeySpec)
            .and(cusipSpec)
            .and(isinSpec)
            .and(sedolSpec)
            .and(tickerSpec)
            .and(collateralTypeSpec)
            .and(collateralCurrencySpec)
            .and(internalPartyIdSpec)
            .and(internalRefAccountIdSpec)
            .and(internalRefIdSpec)
            .and(beforeLastUpdateDateSpec)
            .and(sinceLastUpdateDateSpec)
            .and(sinceTradeDateSpec)
            .and(beforeTradeDateSpec)
            .and(sinceSettlementDateSpec)
            .and(beforeSettlementDateSpec);
    }
}
