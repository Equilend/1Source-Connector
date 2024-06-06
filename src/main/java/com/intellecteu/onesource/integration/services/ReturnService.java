package com.intellecteu.onesource.integration.services;

import com.intellecteu.onesource.integration.mapper.OneSourceMapper;
import com.intellecteu.onesource.integration.model.onesource.Return;
import com.intellecteu.onesource.integration.repository.ReturnRepository;
import com.intellecteu.onesource.integration.repository.entity.onesource.ReturnEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReturnService {

    private final ReturnRepository returnRepository;
    private final OneSourceMapper oneSourceMapper;

    @Autowired
    public ReturnService(ReturnRepository returnRepository, OneSourceMapper oneSourceMapper) {
        this.returnRepository = returnRepository;
        this.oneSourceMapper = oneSourceMapper;
    }

    public Return getByReturnId(String relatedReturnId) {
        ReturnEntity returnEntity = returnRepository.getReferenceById(relatedReturnId);
        return oneSourceMapper.toModel(returnEntity);
    }

    public Return saveReturn(Return oneSourceReturn) {
        ReturnEntity returnEntity = returnRepository.save(oneSourceMapper.toEntity(oneSourceReturn));
        return oneSourceMapper.toModel(returnEntity);
    }

    public Return merge(Return oneSourceReturn, Return oneSourceReturnUpdate) {
        oneSourceReturn.setReturnStatus(oneSourceReturnUpdate.getReturnStatus());
        oneSourceReturn.setExecutionVenue(oneSourceReturnUpdate.getExecutionVenue());
        oneSourceReturn.setQuantity(oneSourceReturnUpdate.getQuantity());
        oneSourceReturn.setCollateral(oneSourceReturnUpdate.getCollateral());
        oneSourceReturn.setSettlementType(oneSourceReturnUpdate.getSettlementType());
        oneSourceReturn.setReturnSettlementDate(oneSourceReturnUpdate.getReturnSettlementDate());
        oneSourceReturn.setReturnDate(oneSourceReturnUpdate.getReturnDate());
        oneSourceReturn.setAcknowledgementType(oneSourceReturnUpdate.getAcknowledgementType());
        oneSourceReturn.setDescription(oneSourceReturnUpdate.getDescription());
        oneSourceReturn.setSettlement(oneSourceReturnUpdate.getSettlement());
        return oneSourceReturn;
    }
}
