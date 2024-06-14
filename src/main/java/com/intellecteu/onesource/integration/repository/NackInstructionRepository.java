package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.repository.entity.toolkit.NackInstructionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NackInstructionRepository extends JpaRepository<NackInstructionEntity, String> {

}
