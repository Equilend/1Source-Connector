package com.intellecteu.onesource.integration.mapper;

import static com.intellecteu.onesource.integration.DtoTestFactory.buildInstrumentDto;
import static com.intellecteu.onesource.integration.ModelTestFactory.buildInstrument;
import static com.intellecteu.onesource.integration.ModelTestFactory.buildVenueParty;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellecteu.onesource.integration.DtoTestFactory;
import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.TestConfig;
import com.intellecteu.onesource.integration.dto.AgreementDto;
import com.intellecteu.onesource.integration.dto.ContractDto;
import com.intellecteu.onesource.integration.dto.InstrumentDto;
import com.intellecteu.onesource.integration.model.Agreement;
import com.intellecteu.onesource.integration.model.Contract;
import com.intellecteu.onesource.integration.model.Instrument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventMapperTest {

    private EventMapper eventMapper;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = TestConfig.createTestObjectMapper();
        eventMapper = new EventMapper(objectMapper);
    }

    @Test
    @DisplayName("Entity mapping shall ignore Price when missed")
    void priceModelMapping_shouldIgnore_whenPriceIsMissed() {
        Instrument instrument = buildInstrument();
        instrument.setPrice(null);

        var instrumentDto = eventMapper.toInstrumentDto(instrument);

        assertNotNull(instrumentDto);
        assertNull(instrumentDto.getPrice());
    }

    @Test
    @DisplayName("Dto mapping shall ignore Price when missed")
    void priceDtoMapping_shouldIgnore_whenPriceIsMissed() {
        InstrumentDto instrumentDto = buildInstrumentDto();
        instrumentDto.setPrice(null);

        var instrument = eventMapper.toInstrumentEntity(instrumentDto);

        assertNotNull(instrument);
        assertNull(instrument.getPrice());
    }

    @Test
    @Disabled
    @DisplayName("Entity mapping shall ignore Internal Ref when missed")
    void internalRefModelMapping_shouldIgnore_whenInternalRefIsMissed() {
        var venueParty = buildVenueParty();

        var venuePartyDto = eventMapper.toVenuePartyDto(venueParty);

        assertNotNull(venuePartyDto);
    }

    @Test
    @Disabled
    @DisplayName("Dto mapping shall ignore Internal Ref when missed")
    void internalRefDtoMapping_shouldIgnore_whenInternalRefIsMissed() {
        var venuePartyDto = DtoTestFactory.buildVenuePartyDto();

        var venueParty = eventMapper.toVenueParty(venuePartyDto);

        assertNotNull(venueParty);
    }

    @Test
    @DisplayName("Dto mapping shall ignore Rounding Rule when missed")
    void roundingRuleDtoMapping_shouldIgnore_whenRoundingRuleIsMissed() {
        var collateralDto = DtoTestFactory.buildCollateralDto();
        collateralDto.setRoundingRule(null);

        var collateral = eventMapper.toCollateralEntity(collateralDto);

        assertNotNull(collateral);
        assertNull(collateral.getRoundingRule());
    }

    @Test
    @DisplayName("Entity mapping shall ignore Rounding Rule when missed")
    void roundingRuleModelMapping_shouldIgnore_whenRoundingRuleIsMissed() {
        var collateral = ModelTestFactory.buildCollateral();
        collateral.setRoundingRule(null);

        var collateralDto = eventMapper.toCollateralDto(collateral);

        assertNotNull(collateralDto);
        assertNull(collateralDto.getRoundingRule());
    }

    @Test
    @DisplayName("Agreement mapping shall read lastUpdateDatetime on missed camel case Style")
    void testAgreementDtoMapping_shouldMapLastUpdateDatetime_whenMissedCamelCaseStyle() throws Exception {
        var agreementJson = getRawAgreement();
        final Agreement agreementEntity = objectMapper.readValue(agreementJson, Agreement.class);
        final AgreementDto agreementDto = eventMapper.toAgreementDto(agreementEntity);

        assertNotNull(agreementDto.getLastUpdateDatetime());
    }

    @Test
    @DisplayName("Agreement mapping shall read lastUpdateDateTime")
    void testAgreementDtoMapping_shouldMapLastUpdateDatetime_whenInCamelCaseStyle() throws Exception {
        var agreementJson = getRawContract().replace("lastUpdateDatetime", "lastUpdateDateTime");
        final Agreement agreementEntity = objectMapper.readValue(agreementJson, Agreement.class);
        final AgreementDto agreementDto = eventMapper.toAgreementDto(agreementEntity);

        assertNotNull(agreementDto.getLastUpdateDatetime());
    }

    @Test
    @DisplayName("Contract mapping shall read lastUpdateDatetime on missed camel case Style")
    void testContractDtoMapping_shouldMapLastUpdateDatetime_whenMissedCamelCaseStyle() throws Exception {
        var contractJson = getRawContract();
        final Contract contractEntity = objectMapper.readValue(contractJson, Contract.class);
        final ContractDto contractDto = eventMapper.toContractDto(contractEntity);

        assertNotNull(contractDto.getLastUpdateDatetime());
    }

    @Test
    @DisplayName("Contract mapping shall read lastUpdateDateTime")
    void testContractDtoMapping_shouldMapLastUpdateDatetime_whenInCamelCaseStyle() throws Exception {
        var contractJson = getRawContract().replace("lastUpdateDatetime", "lastUpdateDateTime");
        final Contract contractEntity = objectMapper.readValue(contractJson, Contract.class);
        final ContractDto contractDto = eventMapper.toContractDto(contractEntity);

        assertNotNull(contractDto.getLastUpdateDatetime());
    }

    private String getRawAgreement() {
        return """
            {
              "agreementId": "32b71278-9ad2-445a-bfb0-b5ada72f7193",
              "status": "PENDING",
              "lastUpdateDatetime": "2023-08-11T05:01:12.192Z",
              "trade": {
                "executionVenue": {
                "partyId": "String",
                "type": "ONPLATFORM",
                "venueName": "string",
                "venueRefId": "f7193",
                "transactionDatetime": "2023-08-11T05:01:12.192Z",
                "venueParties": [
                {
                    "partyRole": "LENDER",
                    "venuePartyRefKey": "string"
                }
            ],
            "localVenueFields": [
                {
                    "localFieldName": "string",
                    "localFieldValue": "string"
                }
            ]
        },
                "instrument": {
                  "ticker": "string",
                  "cusip": "023135106",
                  "isin": "US0231351067",
                  "sedol": "2000019",
                  "quick": "string",
                  "figi": "string",
                  "description": "string",
                  "price": {
                    "value": 0,
                    "currency": "USD",
                    "unit": "SHARE"
                  }
                },
                "rate": {
                  "rebate": {
                    "fixed": {
                      "baseRate": 0.05,
                      "effectiveRate": 0,
                      "effectiveDate": "2023-09-18",
                      "cutoffTime": "23:54"
                    }
                  }
                },
                "quantity": 15000.0,
                "billingCurrency": "USD",
                "dividendRatePct": 85.0,
                "tradeDate": "2023-10-25",
                "termType": "OPEN",
                "termDate": "2023-08-11",
                "settlementDate": "2023-10-25",
                "settlementType": "DVP",
                "collateral": {
                  "contractPrice": 119.57,
                  "contractValue": 17935.5,
                  "collateralValue": 17935.5,
                  "currency": "USD",
                  "type": "CASH",
                  "descriptionCd": "NONUSAGENCIES",
                  "margin": 1.02,
                  "roundingRule": 0,
                  "roundingMode": "ALWAYSUP"
                },
                "transactingParties": [
                  {
                    "partyRole": "LENDER",
                    "party": {
                      "partyId": "001",
                      "partyName": "lender_party",
                      "gleifLei": "f7193",
                      "internalPartyId": "No data"
                    }
                  },
                  {
                    "partyRole": "BORROWER",
                    "party": {
                      "partyId": "002",
                      "partyName": "borrower_party",
                      "gleifLei": "f7195",
                      "internalPartyId": "No data"
                    }
                  }
                ]
              }
            }
            """;
    }

    private String getRawContract() {
        return """
            {
              "contractId": "{{urlParam 'contractId'}}",
              "lastEvent":  {
                  "eventId": 1,
                  "eventType": "TRADE_AGREED",
                  "eventDateTime": "2023-07-27T15:51:40.011Z",
                  "resourceUri": "/ledger/agreements/32b71278-9ad2-445a-bfb0-b5ada72f7193"
                },
              "contractStatus": "APPROVED",
              "settlementStatus": "NONE",
              "lastUpdatePartyId": "string",
              "lastUpdateDatetime": "2023-07-27T17:22:46.245",
              "trade": {
                "executionVenue": {
                "partyId": "String",
                "type": "ONPLATFORM",
                "venueName": "string",
                "venueRefId": "f7195",
                "transactionDatetime": "2023-08-11T09:02:37.011",
                "venueParties": [
                    {
                    "partyRole": "BORROWER",
                    "venuePartyRefKey": "string"
                    }
                ],
                "localVenueFields": [
                    {
                    "localFieldName": "string",
                    "localFieldValue": "string"
                    }
                ]
                },
                "instrument": {
                  "ticker": "string",
                  "cusip": "string",
                  "isin": "string",
                  "sedol": "string",
                  "quick": "string",
                  "figi": "string",
                  "description": "string",
                  "price": {
                    "value": 0,
                    "currency": "USD",
                    "unit": "SHARE"
                  }
                },
                "rate": {
                  "rebate": {
                    "fixed": {
                      "baseRate": 0,
                      "effectiveRate": 0,
                      "effectiveDate": "2023-09-18",
                      "cutoffTime": "23:54"
                    }
                  }
                },
                "quantity": 0,
                "billingCurrency": "USD",
                "dividendRatePct": 0,
                "tradeDate": "2023-08-11",
                "termType": "OPEN",
                "termDate": "2023-08-11",
                "settlementDate": "2023-08-11",
                "settlementType": "DVP",
                "collateral": {
                  "contractPrice": 0,
                  "contractValue": 0,
                  "collateralValue": 0,
                  "currency": "USD",
                  "type": "CASH",
                  "descriptionCd": "NONUSAGENCIES",
                  "margin": 0,
                  "roundingRule": 0,
                  "roundingMode": "ALWAYSUP"
                },
                "transactingParties": [
                  {
                    "partyRole": "BORROWER",
                    "party": {
                      "partyId": "002",
                      "partyName": "party_borrower",
                      "gleifLei": "f7195",
                      "internalPartyId": "no_data"
                    }
                  },
                  {
                    "partyRole": "LENDER",
                    "party": {
                      "partyId": "001",
                      "partyName": "party_lender",
                      "gleifLei": "f7193",
                      "internalPartyId": "no_data"
                    }
                  }
                ]
              },
              "settlement": [
                {
                  "partyRole": "BORROWER",
                  "instruction": {
                    "settlementBic": "string",
                    "localAgentBic": "string",
                    "localAgentName": "string",
                    "localAgentAcct": "string",
                    "localMarketFields": [
                      {
                        "localFieldName": "string",
                        "localFieldValue": "0"
                      }
                    ]
                  }
                }
              ]
            }
            """;
    }

}