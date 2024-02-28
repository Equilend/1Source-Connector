package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.repository.entity.onesource.ParticipantHolderEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantHolderRepository extends JpaRepository<ParticipantHolderEntity, Long> {

    List<ParticipantHolderEntity> findAll();
}
