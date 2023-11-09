package com.intellecteu.onesource.integration.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellecteu.onesource.integration.DtoTestFactory;
import com.intellecteu.onesource.integration.ModelTestFactory;
import com.intellecteu.onesource.integration.dto.InstrumentDto;
import com.intellecteu.onesource.integration.model.Instrument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.intellecteu.onesource.integration.DtoTestFactory.buildInstrumentDto;
import static com.intellecteu.onesource.integration.ModelTestFactory.buildInstrument;
import static com.intellecteu.onesource.integration.ModelTestFactory.buildVenueParty;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class EventMapperTest {

  private EventMapper eventMapper;

  @BeforeEach
  void setUp() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    eventMapper = new EventMapper(objectMapper);
  }

  @Test
  void priceModelMapping_shouldIgnore_whenPriceIsMissed() {
    Instrument instrument = buildInstrument();
    instrument.setPrice(null);

    var instrumentDto = eventMapper.toInstrumentDto(instrument);

    assertNotNull(instrumentDto);
    assertNull(instrumentDto.getPrice());
  }

  @Test
  void priceDtoMapping_shouldIgnore_whenPriceIsMissed() {
    InstrumentDto instrumentDto = buildInstrumentDto();
    instrumentDto.setPrice(null);

    var instrument = eventMapper.toInstrumentEntity(instrumentDto);

    assertNotNull(instrument);
    assertNull(instrument.getPrice());
  }

  @Test
  void internalRefModelMapping_shouldIgnore_whenInternalRefIsMissed() {
    var venueParty = buildVenueParty();
    venueParty.setInternalRef(null);

    var venuePartyDto = eventMapper.toVenuePartyDto(venueParty);

    assertNotNull(venuePartyDto);
    assertNull(venuePartyDto.getInternalRef());
  }

  @Test
  void internalRefDtoMapping_shouldIgnore_whenInternalRefIsMissed() {
    var venuePartyDto = DtoTestFactory.buildVenuePartyDto();
    venuePartyDto.setInternalRef(null);

    var venueParty = eventMapper.toVenueParty(venuePartyDto);

    assertNotNull(venueParty);
    assertNull(venueParty.getInternalRef());
  }

  @Test
  void roundingRuleDtoMapping_shouldIgnore_whenRoundingRuleIsMissed() {
    var collateralDto = DtoTestFactory.buildCollateralDto();
    collateralDto.setRoundingRule(null);

    var collateral = eventMapper.toCollateralEntity(collateralDto);

    assertNotNull(collateral);
    assertNull(collateral.getRoundingRule());
  }

  @Test
  void roundingRuleModelMapping_shouldIgnore_whenRoundingRuleIsMissed() {
    var collateral = ModelTestFactory.buildCollateral();
    collateral.setRoundingRule(null);

    var collateralDto = eventMapper.toCollateralDto(collateral);

    assertNotNull(collateralDto);
    assertNull(collateralDto.getRoundingRule());
  }

}