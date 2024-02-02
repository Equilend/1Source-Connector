package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.onesource.ParticipantHolder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantHolderRepository extends JpaRepository<ParticipantHolder, Long> {

    List<ParticipantHolder> findAll();
}
