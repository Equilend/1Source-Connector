package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.repository.entity.onesource.AgreementEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgreementRepository extends JpaRepository<AgreementEntity, Long> {

    @Query("select a from AgreementEntity a left join fetch a.trade t left join fetch t.venues v left join fetch t.collateral col left join fetch t.transactingParties prt left join fetch prt.party left join fetch t.rate rt left join fetch rt.fee f left join fetch rt.rebate rb left join fetch rb.fixed left join fetch rb.floating left join fetch t.instrument i left join fetch i.price")
    List<AgreementEntity> findAll();

    @Query(
        "select a from AgreementEntity a left join fetch a.trade t left join fetch t.venues v left join fetch t.collateral col "
            + "left join fetch t.transactingParties prt left join fetch prt.party left join fetch t.rate rt left join fetch rt.fee f "
            + "left join fetch rt.rebate rb left join fetch rb.fixed left join fetch rb.floating "
            + "left join fetch t.instrument i left join fetch i.price where v.venueRefKey = :venueRefId")
    List<AgreementEntity> findByVenueRefId(@Param("venueRefId") String venueRefId);

    Optional<AgreementEntity> findByAgreementId(@Param("agreementId") String agreementId);

    @Query("select a from AgreementEntity a join fetch a.trade t join fetch t.venues v where v.venueRefKey = :venueRefKey")
    Optional<AgreementEntity> findByVenueRefKey(String venueRefKey);
}
