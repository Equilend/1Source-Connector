package com.intellecteu.onesource.integration.repository.entity.toolkit;

import com.intellecteu.onesource.integration.model.integrationtoolkit.FigiId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This entity works as cache for the figi code retrievement from external resources.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(FigiId.class)
@Table(name = "figi_cache")
public class FigiEntity {

    @Id
    private String ticker;
    @Id
    private String figi;

}
