package com.intellecteu.onesource.integration.api.services.rerates;

import com.intellecteu.onesource.integration.model.enums.ProcessingStatus;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RerateApiService {
    private final RerateApiRepository rerateApiRepository;

    public Optional<ProcessingStatus> getProcessingStatusByRerateId(String rerateId){
        return rerateApiRepository.findById(rerateId)
            .map(rerateEntity -> rerateEntity.getProcessingStatus());
    }
}
