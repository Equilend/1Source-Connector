package com.intellecteu.onesource.integration.services;

import static com.intellecteu.onesource.integration.TestConfig.createTestObjectMapper;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.CANCELLED;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.FAILED;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.FUTURE;
import static com.intellecteu.onesource.integration.model.enums.PositionStatusEnum.OPEN;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.CANCELED;
import static com.intellecteu.onesource.integration.model.enums.ProcessingStatus.UPDATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.mapper.SpireMapper;
import com.intellecteu.onesource.integration.model.backoffice.Position;
import com.intellecteu.onesource.integration.model.backoffice.PositionStatus;
import com.intellecteu.onesource.integration.routes.common.processor.PositionPendingConfirmationProcessor;
import com.intellecteu.onesource.integration.services.client.onesource.OneSourceApiClient;
import com.intellecteu.onesource.integration.services.systemevent.CloudEventRecordService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PositionPendingConfirmationProcessorTest {

    @Mock
    private AgreementService agreementService;
    @Mock
    private ContractService contractService;
    @Mock
    private PositionService positionService;
    @Mock
    private SettlementService settlementService;
    @Mock
    private CloudEventRecordService cloudEventRecordService;
    @Mock
    private OneSourceApiClient oneSourceApiClient;
    @Mock
    private BackOfficeService lenderBackOfficeService;
    @Mock
    private BackOfficeService borrowerBackOfficeService;
    private PositionPendingConfirmationProcessor service;

    @Test
    @DisplayName("Position should be saved with processing status UPDATED when position status is FUTURE")
    void testUpdatePosition_shouldSetUpdatedStatus_whenPositionStatusIsFuture() {
        var testPosition = ModelTestFactory.buildPosition(new PositionStatus(11, FUTURE.getValue()));

        var argumentCaptor = ArgumentCaptor.forClass(Position.class);

        when(positionService.findAllNotCanceledAndSettled()).thenReturn(List.of(testPosition));
        when(lenderBackOfficeService.getNewSpirePositionsObsolete(any(), any())).thenReturn(List.of(testPosition));
        when(lenderBackOfficeService.retrieveSettlementInstruction(any(), any(), any())).thenReturn(Optional.empty());

        service.processUpdatedPositions();

        verify(positionService).savePosition(argumentCaptor.capture());

        Position savedPosition = argumentCaptor.getValue();

        assertEquals(UPDATED, savedPosition.getProcessingStatus());
    }

    @Test
    @DisplayName("Position should be saved with processing status CANCELED when position status is CANCEL")
    void testUpdatePosition_shouldSetCanceledStatus_whenPositionStatusIsCancel() {
        var testPosition = ModelTestFactory.buildPosition(new PositionStatus(11, CANCELLED.getValue()));

        var argumentCaptor = ArgumentCaptor.forClass(Position.class);

        when(positionService.findAllNotCanceledAndSettled()).thenReturn(List.of(testPosition));
        when(lenderBackOfficeService.getNewSpirePositionsObsolete(any(), any())).thenReturn(List.of(testPosition));

        service.processUpdatedPositions();

        verify(positionService).savePosition(argumentCaptor.capture());

        Position savedPosition = argumentCaptor.getValue();

        assertEquals(CANCELED, savedPosition.getProcessingStatus());
    }

    @Test
    @DisplayName("Position should be saved with processing status CANCELED when position status is FAILED")
    void testUpdatePosition_shouldSetCanceledStatus_whenPositionStatusIsFailed() {
        var testPosition = ModelTestFactory.buildPosition(new PositionStatus(11, FAILED.getValue()));

        var argumentCaptor = ArgumentCaptor.forClass(Position.class);

        when(positionService.findAllNotCanceledAndSettled()).thenReturn(List.of(testPosition));
        when(lenderBackOfficeService.getNewSpirePositionsObsolete(any(), any())).thenReturn(List.of(testPosition));

        service.processUpdatedPositions();

        verify(positionService).savePosition(argumentCaptor.capture());

        Position savedPosition = argumentCaptor.getValue();

        assertEquals(CANCELED, savedPosition.getProcessingStatus());
    }

    @Test
    @DisplayName("Position should be saved with processing status SETTLED when position status is OPEN")
    @Disabled
    void testUpdatePosition_shouldSetSettledStatus_whenPositionStatusIsOpen() throws Exception {
        var testPosition = ModelTestFactory.buildPosition(new PositionStatus(11, OPEN.getValue()));
        var argumentCaptor = ArgumentCaptor.forClass(Position.class);

        when(positionService.findAllNotCanceledAndSettled()).thenReturn(List.of(testPosition));
        when(lenderBackOfficeService.getNewSpirePositionsObsolete(any(), any())).thenReturn(List.of(testPosition));

        service.processUpdatedPositions();

        verify(positionService).savePosition(argumentCaptor.capture());

        Position savedPosition = argumentCaptor.getValue();

//        assertEquals(SETTLED, savedPosition.getProcessingStatus());
    }

    @BeforeEach
    void setUp() {
        SpireMapper spireMapper = new SpireMapper(createTestObjectMapper());
        service = new PositionPendingConfirmationProcessor(agreementService, contractService, spireMapper,
            positionService, settlementService, cloudEventRecordService, oneSourceApiClient,
            lenderBackOfficeService, borrowerBackOfficeService);
    }

    private String createTestPositionAsJsonWithStatus(String positionStatus) {
        return String.format("""
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
            					"status": "%s",
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
            """, positionStatus);
    }
}