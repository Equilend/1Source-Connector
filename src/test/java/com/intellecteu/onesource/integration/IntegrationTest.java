//package com.intellecteu.onesource.integration;
//
//import com.intellecteu.onesource.integration.model.spire.TradeOut;
//import com.intellecteu.onesource.integration.services.BackOfficeService;
//import java.util.List;
//import java.util.Optional;
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
//    private BackOfficeService borrowerBackOfficeService;
//
//    @Test
//    void testMock() {
//        List<TradeOut> tradeOutList = borrowerBackOfficeService.getNewSpireTradeEvents(Optional.empty(),
//            List.of("positionId1"));
//
//        System.out.println(tradeOutList);
//
//    }
//}
