package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.Timestamp;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimestampRepository extends JpaRepository<Timestamp, String> {

}
