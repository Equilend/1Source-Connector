package com.intellecteu.onesource.integration.api.services.contracts;

import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.ACCOUNT_ID;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.BEFORE_LAST_UPDATE_DATE;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.BEFORE_SETTLEMENT_DATE;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.BEFORE_TRADE_DATE;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.COLLATERAL_TYPE;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.CONTRACT_ID;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.CONTRACT_STATUS;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.CUSIP;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.C_CURRENCY;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.INTERNAL_PARTY_ID;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.INTERNAL_REF_ID;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.ISIN;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.LAST_UPDATE_PARTY_ID;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.MATCHING_SPIRE_POSITION_ID;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.PROCESSING_STATUS;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.SEDOL;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.SINCE_LAST_UPDATE_DATE;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.SINCE_SETTLEMENT_DATE;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.SINCE_TRADE_DATE;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.TICKER;
import static com.intellecteu.onesource.integration.api.services.contracts.OneSourceLoanContractFields.VENUE_REF_KEY;

import com.intellecteu.onesource.integration.api.dto.ContractDto;
import com.intellecteu.onesource.integration.api.dto.PageResponse;
import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
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
public class ContractApiService {

    private final ContractApiRepository contractRepository;
    private final ContractApiMapper mapper;

    public PageResponse<ContractDto> getAllContracts(Pageable pageable, MultiValueMap<String, String> parameters) {
        Specification<ContractEntity> dynamicFieldsFilter = createSpecificationForContract(parameters);
        Page<ContractEntity> contracts = contractRepository.findAll(dynamicFieldsFilter, pageable);
        if (!parameters.isEmpty() && contracts.isEmpty()) {
            throw new EntityNotFoundException();
        }
        log.debug("Found {} contracts", contracts.getTotalElements());
        final Page<ContractDto> contractPage = contracts.map(mapper::toDto);
        return new PageResponse<>(contractPage);
    }

    public ContractDto getContractById(String id) {
        Optional<ContractEntity> contract = contractRepository.getByContractId(id);
        return contract.map(mapper::toDto).orElseThrow(EntityNotFoundException::new);
    }

    public Optional<ProcessingStatus> getProcessingStatusByContractId(String contractId) {
        return contractRepository.getProcessingStatusByContractId(contractId)
            .map(ProcessingStatusDbResponse::getProcessingStatus);
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

        Specification<ContractEntity> contractIdSpec = OneSourceLoanContractSpecs.contractIdEquals(contractId);
        Specification<ContractEntity> processingStatusSpec = OneSourceLoanContractSpecs.contractProcessingStatusEquals(
            processingStatus);
        Specification<ContractEntity> matchingSpirePositionIdSpec = OneSourceLoanContractSpecs.contractMatchingSpirePositionIdEquals(
            matchingSpirePositionId);
        Specification<ContractEntity> contractStatusSpec = OneSourceLoanContractSpecs.contractStatusEquals(
            contractStatus);
        Specification<ContractEntity> lastUpdatePartyIdSpec = OneSourceLoanContractSpecs.contractLastUpdatePartyIdEquals(
            lastUpdatePartyId);
        Specification<ContractEntity> venueRefKeySpec = OneSourceLoanContractSpecs.venueRefKeyEquals(venueRefKey);
        Specification<ContractEntity> cusipSpec = OneSourceLoanContractSpecs.instrumentCusipEquals(cusip);
        Specification<ContractEntity> isinSpec = OneSourceLoanContractSpecs.instrumentIsinEquals(isin);
        Specification<ContractEntity> sedolSpec = OneSourceLoanContractSpecs.instrumentSedolEquals(sedol);
        Specification<ContractEntity> tickerSpec = OneSourceLoanContractSpecs.instrumentTickerEquals(ticker);
        Specification<ContractEntity> collateralTypeSpec = OneSourceLoanContractSpecs.collateralTypeEquals(
            collateralType);
        Specification<ContractEntity> collateralCurrencySpec = OneSourceLoanContractSpecs.collateralCurrencyEquals(
            collateralCurrency);
        Specification<ContractEntity> internalPartyIdSpec = OneSourceLoanContractSpecs.internalPartyIdEquals(
            internalPartyId);
        Specification<ContractEntity> internalRefAccountIdSpec = OneSourceLoanContractSpecs.internalRefAccountIdEquals(
            internalRefAccountId);
        Specification<ContractEntity> internalRefIdSpec = OneSourceLoanContractSpecs.internalRefIdEquals(internalRefId);

        Specification<ContractEntity> sinceLastUpdateDateSpec = OneSourceLoanContractSpecs.sinceLastUpdateDate(
            sinceLastUpdateDate);
        Specification<ContractEntity> beforeLastUpdateDateSpec = OneSourceLoanContractSpecs.beforeLastUpdateDate(
            beforeLastUpdateDate);
        Specification<ContractEntity> sinceTradeDateSpec = OneSourceLoanContractSpecs.sinceTradeDate(
            sinceTradeDateTime);
        Specification<ContractEntity> beforeTradeDateSpec = OneSourceLoanContractSpecs.beforeTradeDate(
            beforeTradeDateTime);
        Specification<ContractEntity> sinceSettlementDateSpec = OneSourceLoanContractSpecs.sinceSettlementDate(
            sinceSettlementDateTime);
        Specification<ContractEntity> beforeSettlementDateSpec = OneSourceLoanContractSpecs.beforeSettlementDate(
            beforeSettlementDateTime);

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
