package com.intellecteu.onesource.integration;

import static com.intellecteu.onesource.integration.TestConfig.createTestObjectMapper;
import static com.intellecteu.onesource.integration.model.enums.FieldExceptionType.DISCREPANCY;
import static com.intellecteu.onesource.integration.model.enums.FieldExceptionType.UNMATCHED;
import static com.intellecteu.onesource.integration.model.enums.FieldSource.ONE_SOURCE_LOAN_CONTRACT;
import static com.intellecteu.onesource.integration.model.enums.FieldSource.ONE_SOURCE_RERATE;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.OPEN;
import static com.intellecteu.onesource.integration.model.onesource.CollateralDescription.DEBT;
import static com.intellecteu.onesource.integration.model.onesource.CollateralType.CASH;
import static com.intellecteu.onesource.integration.model.onesource.CurrencyCd.USD;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.onesource.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.onesource.PriceUnit.LOT;
import static com.intellecteu.onesource.integration.model.onesource.RoundingMode.ALWAYSUP;
import static com.intellecteu.onesource.integration.model.onesource.SettlementType.DVP;
import static com.intellecteu.onesource.integration.model.onesource.VenueType.ONPLATFORM;

import com.intellecteu.onesource.integration.model.backoffice.Currency;
import com.intellecteu.onesource.integration.model.backoffice.LoanBorrow;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.PositionAccount;
import com.intellecteu.onesource.integration.model.backoffice.PositionCollateralType;
import com.intellecteu.onesource.integration.model.backoffice.PositionExposure;
import com.intellecteu.onesource.integration.model.backoffice.PositionSecurityDetail;
import com.intellecteu.onesource.integration.model.backoffice.PositionStatus;
import com.intellecteu.onesource.integration.model.backoffice.PositionType;
import com.intellecteu.onesource.integration.model.enums.FieldExceptionType;
import com.intellecteu.onesource.integration.model.enums.FieldSource;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RelatedObject;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.EventType;
import com.intellecteu.onesource.integration.model.onesource.PartyRole;
import com.intellecteu.onesource.integration.model.onesource.TermType;
import com.intellecteu.onesource.integration.repository.entity.onesource.CollateralEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.ContractEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.FeeRateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.FixedRateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.InstrumentEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.InternalReferenceEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.LocalVenueFieldEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.PartyEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.PriceEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.RateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.RebateRateEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.SettlementEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.SettlementInstructionEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.SystemEventDataEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.TradeAgreementEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.TradeEventEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.TransactingPartyEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.VenueEntity;
import com.intellecteu.onesource.integration.repository.entity.onesource.VenuePartyEntity;
import com.intellecteu.onesource.integration.repository.entity.toolkit.CloudSystemEventEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.experimental.UtilityClass;

/**
 * Utility class for building entity objects for tests.
 */
@UtilityClass
public class EntityTestFactory {

    public static InstrumentEntity buildInstrumentEntity() {
        return InstrumentEntity.builder()
            .id(9999)
            .ticker("testTicker")
            .cusip("testCusip")
            .isin("testIsin")
            .sedol("testSedol")
            .quick("testQuick")
            .figi("testFigi")
            .description("testDescription")
            .price(buildPriceEntity())
            .build();
    }

    public static ContractEntity buildContract() {
        return ContractEntity.builder()
            .contractId("32b71278-9ad2-445a-bfb0-b5ada72f7194")
            .lastEvent(buildTradeEvent())
            .lastUpdatePartyId("test")
            .eventType(EventType.CONTRACT_PROPOSED)
            .lastUpdateDatetime(LocalDateTime.now())
            .settlement(List.of(buildSettlementEntity()))
            .trade(buildTradeAgreement())
            .build();
    }

    public static TradeAgreementEntity buildTradeAgreement() {
        return TradeAgreementEntity.builder()
            .id(1L)
            .venue(buildVenueEntity())
            .instrument(buildInstrumentEntity())
            .rate(buildRateEntity())
            .quantity(2)
            .billingCurrency(USD)
            .dividendRatePct(2)
            .tradeDate(LocalDate.now())
            .termType(TermType.OPEN)
            .termDate(LocalDate.now())
            .settlementDate(LocalDate.now())
            .settlementType(DVP)
            .collateral(buildCollateralEntity())
            .transactingParties(createTransactionParties())
            .resourceUri("test/ledger/agreements/32b71278-9ad2-445a-bfb0-b5ada72f7199")
            .build();
    }

    public static List<TransactingPartyEntity> createTransactionParties() {
        return List.of(
            createTransactionPartyEntity(LENDER, "lender-lei"),
            createTransactionPartyEntity(BORROWER, "borrower-lei"));
    }

    public static TransactingPartyEntity createTransactionPartyEntity(PartyRole role, String gleifLei) {
        return TransactingPartyEntity.builder()
            .partyRole(role)
            .party(createPartyEntity(gleifLei))
            .build();
    }

    public static PartyEntity createPartyEntity(String gleifLei) {
        return PartyEntity.builder()
            .partyId("testPartyId")
            .partyName("testPartyName")
            .gleifLei(gleifLei)
            .internalPartyId("testInternalPartyId")
            .build();
    }

    public static RateEntity buildRateEntity() {
        return RateEntity.builder()
            .fee(buildFeeRateEntity())
            .rebate(buildRebateRateEntity())
            .build();
    }

    public static RebateRateEntity buildRebateRateEntity() {
        return RebateRateEntity.builder()
            .fixed(new FixedRateEntity(10.2d))
            .build();
    }

    public static FeeRateEntity buildFeeRateEntity() {
        return FeeRateEntity.builder()
            .baseRate(10.2d)
            .effectiveRate(4.0d)
            .effectiveDate(null)
            .cutoffTime(null)
            .build();
    }

    public static VenueEntity buildVenueEntity() {
        return VenueEntity.builder()
            .id(99999L)
            .partyId("testPartyId")
            .type(ONPLATFORM)
            .venueName("testVenueName")
            .venueRefKey("testVenueRefId")
            .transactionDatetime(LocalDateTime.now())
            .venueParties(Set.of(buildVenuePartyEntity()))
            .localVenueFields(Set.of(buildVenueFieldsEntity()))
            .build();

    }

    public static LocalVenueFieldEntity buildVenueFieldsEntity() {
        return LocalVenueFieldEntity.builder()
            .localFieldName("testName")
            .localFieldValue("testValue")
            .build();
    }

    public static SettlementEntity buildSettlementEntity() {
        return SettlementEntity.builder()
            .partyRole(BORROWER)
            .instruction(buildSettlementInstructionEntity())
            .build();
    }

    public static SettlementInstructionEntity buildSettlementInstructionEntity() {
        return SettlementInstructionEntity.builder()
            .localAgentAcct("testacc")
            .localAgentBic("RHBBMYKL")
            .localAgentName("nestname")
            .settlementBic("RHBBMYKL")
            .dtcParticipantNumber("123")
            .build();
    }

    public static TradeEventEntity buildTradeEvent() {
        return TradeEventEntity.builder()
            .eventId(1L)
            .build();
    }

    public static Agreement buildAgreement() {
        var agreementDto = DtoTestFactory.buildAgreementDto();
        var om = createTestObjectMapper();
        return om.convertValue(agreementDto, Agreement.class);
    }

    public static Position buildPosition() {
        return buildPosition(null);
    }

    public static Position buildPosition(PositionStatus positionStatus) {
        if (positionStatus == null) {
            positionStatus = new PositionStatus(11, OPEN.getValue());
        }
        return Position.builder()
            .venueRefId("testVenueRefId")
            .positionId(100L)
            .customValue2("customValue2")
            .termId(1)
            .positionSecurityDetail(buildSecurityDetail())
            .rate(0.1d)
            .quantity(15000.0d)
            .currency(new Currency("USD"))
            .loanBorrow(new LoanBorrow(0.5d))
            .tradeDate(LocalDateTime.now())
            .settleDate(LocalDateTime.now())
            .deliverFree(true)
            .amount(1.0d)
            .price(100.0d)
            .contractValue(123.0d)
            .positionStatus(positionStatus)
            .positionCollateralType(new PositionCollateralType("CASH"))
            .exposure(new PositionExposure(11, 0.05d, 10, 12))
            .positionType(new PositionType(22, "CASH BORROW"))
            .positionAccount(new PositionAccount(1L, 11L, "testLei", "testLeiName", 123L, 123L))
            .positionCpAccount(new PositionAccount(2L, 22L, "testCpLei", "testCpLeiName", 345L, 345L))
            .endDate(LocalDateTime.now())
            .lastUpdateDateTime(LocalDateTime.now().minusDays(1))
            .build();
    }

    private static PositionSecurityDetail buildSecurityDetail() {
        PositionSecurityDetail securityDetail = new PositionSecurityDetail();
        securityDetail.setTicker("testTicker");
        securityDetail.setPriceFactor(1);
        return securityDetail;
    }

    public static CloudSystemEventEntity buildCloudEventEntity() {
        return CloudSystemEventEntity.builder()
            .id(UUID.randomUUID().toString())
            .specVersion("testSpecVersion")
            .type("testType")
            .source("testSource")
            .subject("testSubject")
            .time(LocalDateTime.now())
            .relatedProcess("testRelatedProcess")
            .relatedSubProcess("testRelatedSubProcess")
            .dataContentType("testDataContentType")
            .data(null)
            .build();
    }

    public static PriceEntity buildPriceEntity() {
        return PriceEntity.builder()
            .value(10.0d)
            .currency("USD")
            .unit(LOT)
            .build();
    }

    public static VenuePartyEntity buildVenuePartyEntity() {
        return VenuePartyEntity.builder()
            .partyRole(LENDER)
            .venueId("testVenuePartyRefKey")
            .build();
    }

    private static InternalReferenceEntity buildInternalReferenceEntity() {
        return InternalReferenceEntity.builder()
            .brokerCd("testBrokerCd")
            .accountId("testAccountId")
            .internalRefId("testInternalRefId")
            .build();
    }

    public static CollateralEntity buildCollateralEntity() {
        return CollateralEntity.builder()
            .contractPrice(100.0)
            .contractValue(4.25)
            .collateralValue(400.33)
            .currency("USD")
            .type(CASH)
            .description(DEBT)
            .margin(2.05)
            .roundingRule(2)
            .roundingMode(ALWAYSUP)
            .build();
    }

    public static CloudSystemEventEntity createSystemEventEntity() {
        var entity = CloudSystemEventEntity.builder()
            .id(UUID.randomUUID().toString())
            .specVersion("1.0")
            .type("testType")
            .source("testSource")
            .subject("testSubject")
            .time(LocalDateTime.now())
            .relatedProcess("testRelatedProcess")
            .relatedSubProcess("testRelatedSubProcess")
            .dataContentType("application/json")
            .build();
        entity.setData(createTestEventData(entity.getId()));
        return entity;
    }

    public static SystemEventDataEntity createTestEventData(String id) {
        FieldImpacted firstFieldImpacted = createFieldImpacted(ONE_SOURCE_RERATE, DISCREPANCY);
        FieldImpacted secondFieldImpacted = createFieldImpacted(ONE_SOURCE_LOAN_CONTRACT, UNMATCHED);

        SystemEventDataEntity dataEntity = new SystemEventDataEntity();
        dataEntity.setEventDataId(id);
        dataEntity.setMessage("Test message");
        dataEntity.setFieldsImpacted(List.of(firstFieldImpacted, secondFieldImpacted));
        dataEntity.setRelatedObjects(List.of(RelatedObject.notApplicable(), new RelatedObject("testId", "testType")));
        return dataEntity;
    }

    public static FieldImpacted createFieldImpacted(FieldSource fieldSource, FieldExceptionType fieldExceptionType) {
        return FieldImpacted.builder()
            .fieldSource(fieldSource)
            .fieldName("test-" + fieldExceptionType.getValue())
            .fieldValue("test-" + fieldSource.getValue())
            .fieldExceptionType(fieldExceptionType)
            .build();
    }
}
