//package com.intellecteu.onesource.integration;
//
//import com.intellecteu.onesource.integration.mapper.BackOfficeMapper;
//import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
//import com.intellecteu.onesource.integration.model.backoffice.Position;
//import com.intellecteu.onesource.integration.model.onesource.Rate;
//import com.intellecteu.onesource.integration.repository.entity.backoffice.PositionAccountEntity;
//import com.intellecteu.onesource.integration.repository.entity.backoffice.PositionEntity;
//import com.intellecteu.onesource.integration.repository.entity.onesource.FeeRateEntity;
//import com.intellecteu.onesource.integration.repository.entity.onesource.RateEntity;
//import java.time.LocalDate;
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
//    private OneSourceMapper oneSourceMapper;
//    @Autowired
//    private BackOfficeMapper backOfficeMapper;
//
//    @Test
//    void testMock() {
//
//        PositionEntity positionEntity = new PositionEntity();
//        positionEntity.setPositionAccount(new PositionAccountEntity(1l, "!", "1", "2"));
//
//        Position model = backOfficeMapper.toModel(positionEntity);
//
//        System.out.println(model);
//    }
//}
