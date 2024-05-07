package com.intellecteu.onesource.integration.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.model.onesource.Agreement;
import com.intellecteu.onesource.integration.model.onesource.ContractProposalApproval;
import com.intellecteu.onesource.integration.model.onesource.Instrument;
import com.intellecteu.onesource.integration.model.onesource.LocalVenueField;
import com.intellecteu.onesource.integration.model.onesource.TradeAgreement;
import com.intellecteu.onesource.integration.model.onesource.Venue;
import com.intellecteu.onesource.integration.model.onesource.VenueParty;
import com.intellecteu.onesource.integration.repository.entity.onesource.TradeAgreementEntity;
import com.intellecteu.onesource.integration.services.client.onesource.dto.AgreementDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.ContractProposalApprovalDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.InstrumentDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.LocalVenueFieldDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.VenueDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.VenuePartyDTO;
import com.intellecteu.onesource.integration.services.client.onesource.dto.VenueTradeAgreementDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class OneSourceMapperTest {

    private OneSourceMapper oneSourceMapper;

    @BeforeEach
    void setUp() {
        oneSourceMapper = Mappers.getMapper(OneSourceMapper.class);
    }

    @Test
    void testContractProposalMapping_shouldNotFail_whenRoundingRuleIsNull() {
        final ContractProposalApproval approval = ModelTestFactory.buildContractProposalApproval();
        approval.setRoundingRule(null);
        final ContractProposalApprovalDTO result = oneSourceMapper.toRequestDto(approval);

        assertNull(result.getRoundingRule());
        assertEquals(approval.getInternalRefId(), result.getInternalRefId());
    }

    @Test
    void testVenueMappingToEntity_shouldAssignTradeId() {
        final TradeAgreement model = ModelTestFactory.buildTradeAgreement();
        TradeAgreementEntity entity = oneSourceMapper.toEntity(model);

        assertNotNull(entity.getVenues().get(0));
    }

    @Test
    void testAgreementDTO_shouldMapToAgreement() {
        AgreementDTO agreementDTO = ModelTestFactory.buildAgreementDTO();
        Agreement agreement = oneSourceMapper.toModel(agreementDTO);

        assertNotNull(agreement);
        assertNotNull(agreement.getTrade());
        assertEquals(agreement.getAgreementId(), agreementDTO.getAgreementId());
        assertEquals(agreement.getLastUpdateDateTime(), agreementDTO.getLastUpdateDatetime());
    }

    @Test
    void testAgreementDTO_shouldMapVenueTradeToAgreementTrade() {
        AgreementDTO agreementDTO = ModelTestFactory.buildAgreementDTO();
        Agreement agreement = oneSourceMapper.toModel(agreementDTO);

        final VenueTradeAgreementDTO venueTrade = agreementDTO.getTrade();
        final TradeAgreement trade = agreement.getTrade();

        assertNotNull(agreement);
        assertNotNull(trade);
        assertNotNull(trade.getVenues());
        assertNotNull(trade.getInstrument());
        assertNotNull(trade.getRate());
        assertEquals(trade.getQuantity(), venueTrade.getQuantity());
        assertEquals(trade.getBillingCurrency().name(), venueTrade.getBillingCurrency().name());
        assertEquals(trade.getDividendRatePct(), venueTrade.getDividendRatePct());
        assertEquals(trade.getTradeDate(), venueTrade.getTradeDate());
        assertEquals(trade.getTermType().name(), venueTrade.getTermType().name());
        assertEquals(trade.getTermDate(), venueTrade.getTermDate());
        assertEquals(trade.getSettlementDate(), venueTrade.getSettlementDate());
        assertEquals(trade.getSettlementType().name(), venueTrade.getSettlementType().name());
        assertNotNull(trade.getCollateral());
        Assertions.assertTrue(CollectionUtils.isNotEmpty(trade.getTransactingParties()));
    }

    @Test
    void testAgreementDTO_shouldMapInstrument() {
        AgreementDTO agreementDTO = ModelTestFactory.buildAgreementDTO();
        Agreement agreement = oneSourceMapper.toModel(agreementDTO);

        final InstrumentDTO instrumentDTO = agreementDTO.getTrade().getInstrument();
        final Instrument instrument = agreement.getTrade().getInstrument();

        assertNotNull(instrument);
        assertEquals(instrument.getTicker(), instrumentDTO.getTicker());
        assertEquals(instrument.getCusip(), instrumentDTO.getCusip());
        assertEquals(instrument.getIsin(), instrumentDTO.getIsin());
        assertEquals(instrument.getSedol(), instrumentDTO.getSedol());
        assertEquals(instrument.getQuickCode(), instrumentDTO.getQuick());
        assertEquals(instrument.getFigi(), instrumentDTO.getFigi());
        assertEquals(instrument.getPrice().getValue(), instrumentDTO.getPrice().getValue());
        assertEquals(instrument.getPrice().getCurrency(), instrumentDTO.getPrice().getCurrency().name());
        assertEquals(instrument.getPrice().getUnit().name(), instrumentDTO.getPrice().getUnit().name());
    }

    @Test
    void testAgreementDTO_shouldMapExecutionVenueToVenues() {
        AgreementDTO agreementDTO = ModelTestFactory.buildAgreementDTO();
        Agreement agreement = oneSourceMapper.toModel(agreementDTO);

        final VenueDTO executionVenue = agreementDTO.getTrade().getExecutionVenue();
        final Venue venue = agreement.getTrade().getVenues().get(0);

        assertNotNull(venue);
        assertEquals(venue.getPartyId(), executionVenue.getPartyId());
        assertEquals(venue.getType().name(), executionVenue.getType().name());
        assertEquals(venue.getVenueName(), executionVenue.getVenueName());
        assertEquals(venue.getVenueRefKey(), executionVenue.getVenueRefKey());
        assertEquals(venue.getTransactionDateTime(), executionVenue.getTransactionDatetime());
        assertNotNull(venue.getVenueParties());
        assertNotNull(venue.getLocalVenueFields());
    }

    @Test
    void testAgreementDTO_shouldMapVenueParties() {
        AgreementDTO agreementDTO = ModelTestFactory.buildAgreementDTO();
        Agreement agreement = oneSourceMapper.toModel(agreementDTO);

        final VenuePartyDTO venuePartyDTO = agreementDTO.getTrade().getExecutionVenue().getVenueParties().get(0);
        final VenueParty venueParty = agreement.getTrade().getVenues().get(0).getVenueParties().stream().findFirst()
            .orElse(null);

        assertNotNull(venueParty);
        assertEquals(venueParty.getPartyRole().name(), venuePartyDTO.getPartyRole().name());
        assertEquals(venueParty.getVenueId(), venuePartyDTO.getVenuePartyRefKey());
    }

    @Test
    void testAgreementDTO_shouldMapLocalVenueField() {
        AgreementDTO agreementDTO = ModelTestFactory.buildAgreementDTO();
        Agreement agreement = oneSourceMapper.toModel(agreementDTO);

        final LocalVenueFieldDTO localVenueFieldDTO = agreementDTO.getTrade().getExecutionVenue().getLocalVenueFields()
            .get(0);
        final LocalVenueField localVenueField = agreement.getTrade().getVenues().get(0).getLocalVenueFields().stream()
            .findFirst().orElse(null);

        assertNotNull(localVenueField);
        assertEquals(localVenueField.getLocalFieldName(), localVenueFieldDTO.getLocalFieldName());
        assertEquals(localVenueField.getLocalFieldValue(), localVenueFieldDTO.getLocalFieldValue());
    }

}