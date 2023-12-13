package com.intellecteu.onesource.integration;

import static com.intellecteu.onesource.integration.model.AgreementStatus.CONFIRMED;
import static com.intellecteu.onesource.integration.model.CollateralDescription.DEBT;
import static com.intellecteu.onesource.integration.model.CollateralType.CASH;
import static com.intellecteu.onesource.integration.model.CurrencyCd.USD;
import static com.intellecteu.onesource.integration.model.PartyRole.BORROWER;
import static com.intellecteu.onesource.integration.model.PartyRole.LENDER;
import static com.intellecteu.onesource.integration.model.PriceUnit.LOT;
import static com.intellecteu.onesource.integration.model.RoundingMode.ALWAYSUP;
import static com.intellecteu.onesource.integration.model.SettlementType.DVP;
import static com.intellecteu.onesource.integration.model.TermType.OPEN;
import static com.intellecteu.onesource.integration.model.VenueType.ONPLATFORM;

import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.CollateralDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.FeeRateDto;
import com.intellecteu.onesource.integration.dto.InstrumentDto;
import com.intellecteu.onesource.integration.dto.InternalReferenceDto;
import com.intellecteu.onesource.integration.dto.LocalMarketFieldDto;
import com.intellecteu.onesource.integration.dto.LocalVenueFieldsDto;
import com.intellecteu.onesource.integration.dto.PartyDto;
import com.intellecteu.onesource.integration.dto.PriceDto;
import com.intellecteu.onesource.integration.dto.RateDto;
import com.intellecteu.onesource.integration.dto.SettlementDto;
import com.intellecteu.onesource.integration.dto.SettlementInstructionDto;
import com.intellecteu.onesource.integration.dto.TradeAgreementDto;
import com.intellecteu.onesource.integration.dto.TradeEventDto;
import com.intellecteu.onesource.integration.dto.TransactingPartyDto;
import com.intellecteu.onesource.integration.dto.VenueDto;
import com.intellecteu.onesource.integration.dto.VenuePartyDto;
import com.intellecteu.onesource.integration.dto.record.CloudEvent;
import com.intellecteu.onesource.integration.dto.spire.AccountDto;
import com.intellecteu.onesource.integration.dto.spire.CurrencyDto;
import com.intellecteu.onesource.integration.dto.spire.LoanBorrowDto;
import com.intellecteu.onesource.integration.dto.spire.PositionCollateralTypeDto;
import com.intellecteu.onesource.integration.dto.spire.PositionDto;
import com.intellecteu.onesource.integration.dto.spire.PositionExposureDto;
import com.intellecteu.onesource.integration.dto.spire.PositionTypeDto;
import com.intellecteu.onesource.integration.dto.spire.SecurityDetailDto;
import com.intellecteu.onesource.integration.model.EventType;
import com.intellecteu.onesource.integration.model.LocalMarketField;
import com.intellecteu.onesource.integration.model.LocalVenueField;
import com.intellecteu.onesource.integration.model.PartyRole;
import com.intellecteu.onesource.integration.model.SettlementInstruction;
import com.intellecteu.onesource.integration.model.SettlementType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import lombok.experimental.UtilityClass;

/**
 * Utility class for building dto objects for tests.
 */
@UtilityClass
public class DtoTestFactory {

    public static AgreementDto buildAgreementDto() {
        return AgreementDto.builder()
            .agreementId("testId")
            .status(CONFIRMED)
            .lastUpdateDatetime(LocalDateTime.now())
            .trade(buildTradeAgreementDto())
            .build();
    }

    public static ContractDto buildContractDto() {
        return ContractDto.builder()
            .contractId("testId")
            .lastEvent(buildTradeEventDto())
            .lastUpdatePartyId("test")
            .eventType(EventType.CONTRACT_PROPOSED)
            .lastUpdateDatetime(LocalDateTime.now())
            .settlement(List.of(buildSettlementDto()))
            .trade(buildTradeAgreementDto())
            .build();
    }

    public static TradeEventDto buildTradeEventDto() {
        return TradeEventDto.builder()
            .eventId(1L)
            .build();
    }

    public static TradeAgreementDto buildTradeAgreementDto() {
        return TradeAgreementDto.builder()
            .id(1L)
            .executionVenue(buildVenueDto())
            .instrument(buildInstrumentDto())
            .rate(buildRateDto())
            .quantity(2)
            .billingCurrency(USD)
            .dividendRatePct(2)
            .tradeDate(LocalDate.now())
            .termType(OPEN)
            .termDate(LocalDate.now())
            .settlementDate(LocalDate.now())
            .settlementType(DVP)
            .collateral(buildCollateralDto())
            .transactingParties(createTransactionParties())
            .resourceUri("test/ledger/agreements/32b71278-9ad2-445a-bfb0-b5ada72f7199")
            .build();
    }

    public static PositionDto buildPositionDto() {
        return buildPositionDtoFromTradeAgreement(buildTradeAgreementDto());
    }

    public static CloudEvent buildTestCloudEvent() {
        return CloudEvent.builder()
            .id(UUID.randomUUID().toString())
            .specVersion("1.0.2")
            .type("testType")
            .source("testSource")
            .subject("testSubject")
            .time(LocalDateTime.now())
            .relatedProcess("testRelatedProcess")
            .relatedSubProcess("testRelatedSubProcess")
            .dataContentType("application/json")
            .eventData("{ \"test\":\"data\" }")
            .build();
    }

    public static PositionDto buildPositionDtoFromTradeAgreement(TradeAgreementDto tradeAgreement) {
        return PositionDto.builder()
            .id(tradeAgreement.getId())
            .positionId("testSpirePositionId")
            .customValue2(tradeAgreement.getExecutionVenue().getVenueRefKey())
            .securityDetailDto(buildSecurityDetailDto(tradeAgreement))
            .rate(tradeAgreement.getRate().getFee().getBaseRate())
            .quantity(tradeAgreement.getQuantity().doubleValue())
            .currency(buildCurrencyDto(tradeAgreement))
            .loanBorrowDto(buildLoanBorrowDto(tradeAgreement))
            .tradeDate(LocalDateTime.of(tradeAgreement.getTradeDate(), LocalTime.now()))
            .settleDate(LocalDateTime.of(tradeAgreement.getSettlementDate(), LocalTime.now()))
            .deliverFree(translateDeliverFree(tradeAgreement.getSettlementType()))
            .amount(tradeAgreement.getCollateral().getCollateralValue())
            .price(tradeAgreement.getCollateral().getContractPrice())
            .contractValue(tradeAgreement.getCollateral().getContractValue())
            .collateralTypeDto(buildCollateralTypeDto(tradeAgreement))
            .exposureDto(buildPositionExposureDto(tradeAgreement))
            .positionTypeDto(buildPositionTypeDto(tradeAgreement))
            .accountDto(buildAccountDto(tradeAgreement.getTransactingParties().get(0)))
            .cpDto(buildAccountDto(tradeAgreement.getTransactingParties().get(1)))
            .build();
    }

    public static AccountDto buildAccountDto(TransactingPartyDto party) {
        return AccountDto.builder()
            .lei(retrieveLei(party))
            .build();
    }

    public static PositionTypeDto buildPositionTypeDto(TradeAgreementDto tradeAgreement) {
        return PositionTypeDto.builder()
            .positionType(retrievePositionType(tradeAgreement.getTransactingParties()))
            .build();
    }

    public static PositionExposureDto buildPositionExposureDto(TradeAgreementDto tradeAgreement) {
        return PositionExposureDto.builder()
            .cpHaircut(tradeAgreement.getCollateral().getMargin() / 100.0)
            .cpMarkRoundTo(tradeAgreement.getCollateral().getRoundingRule())
            .build();
    }

    public static PositionCollateralTypeDto buildCollateralTypeDto(TradeAgreementDto tradeAgreement) {
        return PositionCollateralTypeDto.builder()
            .collateralType(tradeAgreement.getCollateral().getType().name())
            .build();
    }

    public static LoanBorrowDto buildLoanBorrowDto(TradeAgreementDto tradeAgreement) {
        return LoanBorrowDto.builder()
            .taxWithholdingRate(tradeAgreement.getDividendRatePct().doubleValue())
            .build();
    }

    public static CurrencyDto buildCurrencyDto(TradeAgreementDto tradeAgreement) {
        return CurrencyDto.builder()
            .currencyKy(tradeAgreement.getBillingCurrency().name())
            .build();
    }

    public static SecurityDetailDto buildSecurityDetailDto(TradeAgreementDto tradeAgreement) {
        return SecurityDetailDto.builder()
            .ticker(tradeAgreement.getInstrument().getTicker())
            .cusip(tradeAgreement.getInstrument().getCusip())
            .isin(tradeAgreement.getInstrument().getIsin())
            .sedol(tradeAgreement.getInstrument().getSedol())
            .quickCode(tradeAgreement.getInstrument().getQuick())
            .bloombergId(tradeAgreement.getInstrument().getFigi())
            .build();
    }

    private static String retrieveLei(TransactingPartyDto party) {
        return party.getParty().getGleifLei();
    }

    private static String retrievePositionType(List<TransactingPartyDto> parties) {
        boolean isCashLoan = parties.stream().anyMatch(p -> p.getPartyRole() == LENDER);
        if (isCashLoan) {
            return "CASH LOAN";
        }
        boolean isCashBorrow = parties.stream().anyMatch(p -> p.getPartyRole() == BORROWER);
        if (isCashBorrow) {
            return "CASH BORROW";
        }
        return null;
    }

    private static Boolean translateDeliverFree(SettlementType settlementType) {
        return settlementType != DVP;
    }

    public static TransactingPartyDto createTransactionParty(PartyRole role, String gleifLei) {
        return TransactingPartyDto.builder()
            .partyRole(role)
            .party(createPartyDto(gleifLei))
            .build();
    }

    public static List<TransactingPartyDto> createTransactionParties() {
        return List.of(
            createTransactionParty(LENDER, "lender-lei"),
            createTransactionParty(BORROWER, "borrower-lei"));
    }

    public static PartyDto createPartyDto(String gleifLei) {
        return PartyDto.builder()
            .partyId("testPartyId")
            .partyName("testPartyName")
            .gleifLei(gleifLei)
            .internalPartyId("testInternalPartyId")
            .build();
    }

    public static CollateralDto buildCollateralDto() {
        return CollateralDto.builder()
            .contractPrice(100.00)
            .contractValue(4.52)
            .collateralValue(400.32)
            .currency("USD")
            .type(CASH)
            .descriptionCd(DEBT)
            .margin(202.0)
            .roundingRule(2)
            .roundingMode(ALWAYSUP)
            .build();
    }

    public static RateDto buildRateDto() {
        return RateDto.builder()
            .fee(buildFeeRateDto())
            .rebate(null)
            .build();
    }

    public static FeeRateDto buildFeeRateDto() {
        return FeeRateDto.builder()
            .baseRate(10.2d)
            .effectiveRate(4.0d)
            .effectiveDate(null)
            .cutoffTime(null)
            .build();
    }

    public static InstrumentDto buildInstrumentDto() {
        return InstrumentDto.builder()
            .ticker("testTicker")
            .cusip("testCusip")
            .isin("testIsin")
            .sedol("testSedol")
            .quick("testQuick")
            .figi("testFigi")
            .description("testDescription")
            .price(buildPriceDto())
            .build();
    }

    public static PriceDto buildPriceDto() {
        return PriceDto.builder()
            .value(10.0d)
            .currency("USD")
            .unit(LOT)
            .build();
    }

    public static VenueDto buildVenueDto() {
        return VenueDto.builder()
            .partyId("testPartyId")
            .type(ONPLATFORM)
            .venueName("testVenueName")
            .venueRefKey("testVenueRefId")
            .transactionDatetime(LocalDateTime.now())
            .venueParties(List.of(buildVenuePartyDto()))
            .localVenueFields(List.of(buildVenueFieldsDto()))
            .build();

    }

    public static VenuePartyDto buildVenuePartyDto() {
        return VenuePartyDto.builder()
            .partyRole(LENDER)
            .venuePartyRefKey("testVenuePartyRefKey")
            .build();
    }

    public static InternalReferenceDto buildInternalReferenceDto() {
        return InternalReferenceDto.builder()
            .brokerCd("testBrokerCd")
            .accountId("testAccountId")
            .internalRefId("testInternalRefId")
            .build();
    }

    public static SettlementDto buildSettlementDto() {
        return SettlementDto.builder()
            .partyRole(BORROWER)
            .instruction(buildInstructionDto())
            .build();
    }

    public static SettlementInstructionDto buildInstructionDto() {
        return SettlementInstructionDto.builder()
            .localAgentAcct("testacc")
            .localAgentBic("RHBBMYKL")
            .localAgentName("nestname")
            .settlementBic("RHBBMYKL")
            .localMarketFields(List.of(buildMarketDto()))
            .build();
    }

    public static SettlementInstruction buildInstruction() {
        return SettlementInstruction.builder()
            .localAgentAcct("testacc")
            .localAgentBic("RHBBMYKL")
            .localAgentName("nestname")
            .settlementBic("RHBBMYKL")
            .localMarketField(List.of(buildMarket()))
            .build();
    }

    public static LocalMarketFieldDto buildMarketDto() {
        return LocalMarketFieldDto.builder()
            .localFieldName("testName")
            .localFieldValue("testValue")
            .build();
    }

    public static LocalVenueFieldsDto buildVenueFieldsDto() {
        return LocalVenueFieldsDto.builder()
            .localFieldName("testName")
            .localFieldValue("testValue")
            .build();
    }

    public static LocalMarketField buildMarket() {
        return LocalMarketField.builder()
            .localFieldName("testName")
            .localFieldValue("testValue")
            .build();
    }

    public static LocalVenueField buildVenueField() {
        return LocalVenueField.builder()
            .localFieldName("testName")
            .localFieldValue("testValue")
            .build();
    }

    public static String getPositionAsJson() {
        return """
            {
            	"data": {
            		"beans": [
            			{
            				"positionId": 772311,
            				"positionTypeId": 51,
            				"termId": 1,
            				"counterpartyId": 2701,
            				"accountId": 2045,
            				"accountId2": 2612,
            				"securityId": 314937,
            				"currencyId": 51,
            				"calendarId": 1,
            				"depoId": 1,
            				"tradeDate": "2023-10-27T00:00:00",
            				"settleDate": "2023-10-27T00:00:00",
            				"poolPositionId": 772311,
            				"createUserId": 1,
            				"createTs": "2023-10-27T11:11:24",
            				"lastModUserId": 1,
            				"lastModTs": "2023-10-30T00:30:10",
            				"statusId": 1,
            				"quantity": 15000.0,
            				"price": 119.57,
            				"factor": 1.0,
            				"indexId": 12,
            				"rate": 0.05,
            				"basisId": 1,
            				"positionRef": "772311",
            				"tradingDeskId": 1,
            				"beginQuantity": 15000.0,
            				"beginPrice": 119.57,
            				"beginFactor": 1.0,
            				"markStatusId": 4,
            				"exposureId": 1111,
            				"settledQuantity": 0.0,
            				"customValue2": "895147539741",
            				"deliverFree": false,
            				"indemnified": false,
            				"amount": 17935.5,
            				"excludeFromAutoMark": false,
            				"isPooled": false,
            				"factoredPrice": 119.57,
            				"loanBorrowDTO": {
            					"positionId": 772311,
            					"taxWithholdingRate": 85.0,
            					"collateralIndexId": 12,
            					"collateralCalendarId": 1
            				},
            				"tradingdeskDTO": {
            					"tradingDeskId": 1,
            					"tradingDeskName": "US/NEW YORK",
            					"timezone": "America/New_York",
            					"calendarId": 1,
            					"__qualifiedName": "com.stonewain.ref.data.dto.TradingdeskDTO"
            				},
            				"positiontypeDTO": {
            					"positionTypeId": 51,
            					"positionType": "CASH BORROW",
            					"parentId": 50,
            					"collateralIndicator": true,
            					"exposureIndicator": true,
            					"isCash": true,
            					"ledgerAccountId": 86,
            					"drCrIndicator": true,
            					"accrualTypeId": 50,
            					"futureLimitDays": 0,
            					"flatAccrual": true,
            					"forTradeEntry": true
            				},
            				"counterPartyDTO": {
            					"accountId": 2701,
            					"accountTypeId": 4,
            					"accountNo": "test lender",
            					"shortName": "test lender",
            					"legalName": "test lender",
            					"statusId": 30,
            					"startDate": "2022-08-17T00:00:00",
            					"description": "test lender",
            					"tradingDeskId": 1,
            					"taxId": "OPOF",
            					"dtc": 123,
            					"isLegalEntity": true,
            					"isPooled": false,
            					"isAldDisclosed": false,
            					"isSubAccount": false,
            					"currencyId": 51,
            					"countryId": 51,
            					"createUserId": 1,
            					"createTs": "2022-08-12T17:32:39",
            					"lastModUserId": 38,
            					"lastModTs": "2022-10-21T16:58:40",
            					"isTrialBalance": false,
            					"isBorrowAccount": false,
            					"isExternalLender": true,
            					"accountSubTypeId": 6,
            					"indemnified": false,
            					"allowInvUpload": false,
            					"profileId": 66,
            					"restrictOnReturn": false,
            					"participating": false,
            					"reinvestment": false,
            					"subCustodian": false,
            					"armsReady": false,
            					"notInClientRelation": false,
            					"skipGl": false,
            					"accountNo2": "1000034121",
            					"billingCurrencyId": 51,
            					"extAccountRef": "1000895535",
            					"excludeFromAutoborrow": false,
            					"lei": "3S2K00ZMKMQ57SPKJ719",
            					"callinRequired": false,
            					"callbackType": "autowrap",
            					"autowrapCutofftime": "11;50",
            					"countryDTO": {
            						"countryId": 51,
            						"countryKy": "US",
            						"countryName": "UNITED STATES",
            						"calendarId": 1,
            						"aldRegion": "US",
            						"bojCountryCode": 304,
            						"region": "Americas",
            						"__qualifiedName": "com.stonewain.ref.data.dto.CountryDTO"
            					},
            					"__qualifiedName": "com.stonewain.ref.data.dto.AccountDTO"
            				},
            				"counterpartyGroupDTO": null,
            				"accountDTO": {
            					"accountId": 2045,
            					"accountTypeId": 1,
            					"accountNo": "test borrower",
            					"shortName": "test borrower",
            					"legalName": "test borrower",
            					"statusId": 30,
            					"startDate": "2021-05-17T00:00:00",
            					"description": "test borrower",
            					"tradingDeskId": 1,
            					"dtc": 789,
            					"isLegalEntity": false,
            					"isPooled": false,
            					"isAldDisclosed": false,
            					"isSubAccount": false,
            					"currencyId": 51,
            					"countryId": 51,
            					"createUserId": 1,
            					"createTs": "2021-06-17T04:44:03",
            					"lastModUserId": 38,
            					"lastModTs": "2022-04-29T07:10:58",
            					"isTrialBalance": false,
            					"isBorrowAccount": true,
            					"isExternalLender": false,
            					"indemnified": false,
            					"allowInvUpload": false,
            					"profileId": 66,
            					"restrictOnReturn": false,
            					"participating": false,
            					"reinvestment": false,
            					"subCustodian": false,
            					"armsReady": true,
            					"notInClientRelation": false,
            					"skipGl": false,
            					"billingCurrencyId": 51,
            					"extAccountRef": "573",
            					"excludeFromAutoborrow": false,
            					"lei": "4YCP00N1DDKL1NC2MW38",
            					"countryDTO": {
            						"countryId": 51,
            						"countryKy": "US",
            						"countryName": "UNITED STATES",
            						"calendarId": 1,
            						"aldRegion": "US",
            						"bojCountryCode": 304,
            						"region": "Americas",
            						"__qualifiedName": "com.stonewain.ref.data.dto.CountryDTO"
            					},
            					"__qualifiedName": "com.stonewain.ref.data.dto.AccountDTO"
            				},
            				"accountGroupDTO": null,
            				"account2DTO": {
            					"accountId": 2612,
            					"accountTypeId": 2,
            					"accountNo": "09860000",
            					"shortName": "09860000",
            					"legalName": "Deutsche Bank Securities Inc.",
            					"statusId": 30,
            					"startDate": "2022-05-19T00:00:00",
            					"description": "FIC Management",
            					"tradingDeskId": 1,
            					"isLegalEntity": false,
            					"isPooled": false,
            					"isAldDisclosed": false,
            					"isSubAccount": false,
            					"currencyId": 51,
            					"countryId": 51,
            					"createUserId": 1,
            					"createTs": "2022-08-12T05:52:42",
            					"lastModUserId": 43,
            					"lastModTs": "2022-11-24T07:55:10",
            					"isTrialBalance": false,
            					"isBorrowAccount": false,
            					"isExternalLender": false,
            					"accountSubTypeId": 22,
            					"indemnified": false,
            					"allowInvUpload": false,
            					"profileId": 66,
            					"restrictOnReturn": false,
            					"participating": false,
            					"reinvestment": false,
            					"subCustodian": false,
            					"armsReady": false,
            					"notInClientRelation": false,
            					"skipGl": false,
            					"billingCurrencyId": 51,
            					"extAccountRef": "2043495",
            					"excludeFromAutoborrow": false,
            					"countryDTO": {
            						"countryId": 51,
            						"countryKy": "US",
            						"countryName": "UNITED STATES",
            						"calendarId": 1,
            						"aldRegion": "US",
            						"bojCountryCode": 304,
            						"region": "Americas",
            						"__qualifiedName": "com.stonewain.ref.data.dto.CountryDTO"
            					},
            					"__qualifiedName": "com.stonewain.ref.data.dto.AccountDTO"
            				},
            				"securityDetailDTO": {
            					"securityId": 314937,
            					"categoryId": 144,
            					"statusId": 30,
            					"status": "ACTIVE",
            					"createUserId": 1,
            					"createTs": "2022-08-31T00:00:00",
            					"lastModUserId": 1,
            					"lastModTs": "2022-10-24T00:00:00",
            					"paymentFrequencyId": 7,
            					"isChilled": "Not Chilled",
            					"taxationCountryId": 51,
            					"shortName": "AMAZON.COM INC",
            					"cusip": "023135106",
            					"sedol": "2000019",
            					"isin": "US0231351067",
            					"clientSecId": "50224524",
            					"categoryName": "Treasury",
            					"primaryIdType": "ISIN",
            					"primaryId": "US0231351067",
            					"factor": 1.0,
            					"priceFactor": 100,
            					"countryDTO": {
            						"countryId": 51,
            						"countryKy": "US",
            						"countryName": "UNITED STATES",
            						"calendarId": 1,
            						"aldRegion": "US",
            						"bojCountryCode": 304,
            						"region": "Americas",
            						"__qualifiedName": "com.stonewain.ref.data.dto.CountryDTO"
            					},
            					"currencyDTO": {
            						"currencyId": 51,
            						"currencyKy": "USD",
            						"currencyName": "US Dollar",
            						"currencySymbol": "$",
            						"bojCurrencyCode": 102
            					},
            					"categoryDTO": {
            						"categoryId": 144,
            						"categoryName": "Treasury",
            						"categoryLevel": 2,
            						"priceFactor": 100,
            						"parentId": 14,
            						"category0Id": 2,
            						"category0Name": "F",
            						"category1Id": 14,
            						"category1Name": "Government",
            						"category2Id": 144,
            						"category2Name": "Treasury",
            						"defaultDrf": 3.0,
            						"sortValue": "200010010",
            						"sortTitle": "Convertible",
            						"monthEndSortValue": "1005500",
            						"monthEndSortTitle": "US Treasuries",
            						"clientCat1Name": "Gov",
            						"__qualifiedName": "com.stonewain.ref.data.dto.CategoryDTO"
            					},
            					"loanRateAvg": 0.0,
            					"taxationCountryName": "US",
            					"frequencyName": "N/A",
            					"withholdRate": 0.0,
            					"__qualifiedName": "com.stonewain.ref.data.dto.response.SecurityDetailDTO"
            				},
            				"currencyDTO": {
            					"currencyId": 51,
            					"currencyKy": "USD",
            					"currencyName": "US Dollar",
            					"currencySymbol": "$",
            					"bojCurrencyCode": 102
            				},
            				"depositoryDTO": {
            					"depoId": 1,
            					"depoKy": "DTC",
            					"tzname": "US/Eastern",
            					"swiftBicId": 200,
            					"swiftCode": "DTCYID",
            					"securityRefId": 1,
            					"maxQuantity": 50000000,
            					"depoGroup": "DTC",
            					"forPrepayDate": false,
            					"bulkable": true,
            					"offset": 0,
            					"__qualifiedName": "com.stonewain.ref.data.dto.DepositoryDTO"
            				},
            				"userDTO": {
            					"userId": 1,
            					"profileId": 1,
            					"homePageId": 1,
            					"companyId": 1,
            					"userName": "stonewain.support",
            					"firstName": "Stonewain",
            					"lastName": "Support",
            					"email": "support@stonewain.com",
            					"supervisorId": 0,
            					"isExecuteTrader": 1,
            					"isRecommendTrader": 0,
            					"tradingDeskId": 1,
            					"accountId": 1977,
            					"userType": "SPIRE_SYSTEM_USER",
            					"userCategory": "INTERNAL",
            					"inactive": false,
            					"__qualifiedName": "com.stonewain.ref.data.dto.UserDTO"
            				},
            				"statusDTO": {
            					"statusId": 1,
            					"status": "OPEN",
            					"forPosition": 1,
            					"forTrade": 1,
            					"forOrder": 1,
            					"__qualifiedName": "com.stonewain.ref.data.dto.StatusDTO"
            				},
            				"indexDTO": {
            					"indexId": 12,
            					"indexName": "Fixed Rate",
            					"indexCategory": "GENERIC",
            					"isActive": true,
            					"calendarId": 1,
            					"calendarDTO": {
            						"calendarId": 1,
            						"calendarName": "USA",
            						"timezone": "America/New_York",
            						"__qualifiedName": "com.stonewain.ref.data.dto.CalendarDTO"
            					},
            					"__qualifiedName": "com.stonewain.ref.data.dto.IndexDTO"
            				},
            				"basisDTO": {
            					"basisId": 1,
            					"basisName": "ACTUAL/360",
            					"__qualifiedName": "com.stonewain.ref.data.dto.BasisDTO"
            				},
            				"exposureDTO": {
            					"exposureId": 1111,
            					"expModuleId": 1,
            					"accountId": 2045,
            					"counterpartyId": 2701,
            					"positionTypeId": 50,
            					"xpHaircut": 0.001,
            					"xpMarkRoundTo": 0,
            					"xpRoundUpMin": 0.0,
            					"xpMinPrice": 0,
            					"cpHaircut": 1.02,
            					"cpMarkRoundTo": 0,
            					"cpRoundUpMin": 0.0,
            					"cpMinPrice": 0,
            					"minMarkAmount": 0,
            					"description": "0573_0005_BORROW",
            					"intInHaircut": false,
            					"__qualifiedName": "com.stonewain.ref.data.dto.ExposureDTO"
            				}
            			}
            		],
            		"totalRows": 1
            	},
            	"status": "SUCCESS",
            	"code": "SUCCESS",
            	"success": true,
            	"error": false
            }
            """;
    }

}
