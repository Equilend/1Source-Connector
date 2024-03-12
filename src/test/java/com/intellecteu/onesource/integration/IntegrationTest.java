//package com.intellecteu.onesource.integration;
//
//import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
//import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
//import com.intellecteu.onesource.integration.model.backoffice.Position;
//import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
//import com.intellecteu.onesource.integration.model.onesource.Rate;
//import com.intellecteu.onesource.integration.repository.RerateTradeRepository;
//import com.intellecteu.onesource.integration.repository.entity.backoffice.PositionAccountEntity;
//import com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity;
//import com.intellecteu.onesource.integration.repository.entity.backoffice.RerateTradeEntity;
//import com.intellecteu.onesource.integration.repository.entity.onesource.FeeRateEntity;
//import com.intellecteu.onesource.integration.repository.entity.onesource.RateEntity;
//import java.time.LocalDate;
//import java.util.List;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//@SpringBootTest
//@ActiveProfiles("local")
//public class IntegrationTest {
//
//    @Autowired
//    private RerateTradeRepository rerateTradeRepository;
//
//    @Test
//    void testMock() {
//
//        List<RerateTradeEntity> unmatchedRerateTrades = rerateTradeRepository.findUnmatchedRerateTrades("7777",
//            List.of(ProcessingStatus.CREATED, ProcessingStatus.UPDATED));
//
//        System.out.println(unmatchedRerateTrades);
//
//    }
//}
