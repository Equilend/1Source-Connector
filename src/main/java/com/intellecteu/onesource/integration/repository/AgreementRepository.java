package com.intellecteu.onesource.integration.repository;

import com.intellecteu.onesource.integration.model.Agreement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgreementRepository extends JpaRepository<Agreement, Long> {

    @Query("select a from Agreement a left join fetch a.trade t left join fetch t.venue v left join fetch t.collateral col left join fetch t.transactingParties prt left join fetch prt.party left join fetch t.rate rt left join fetch rt.fee f left join fetch rt.rebate rb left join fetch rb.fixed left join fetch rb.floating left join fetch t.instrument i left join fetch i.price left join fetch v.platform p")
    List<Agreement> findAll();

    @Query("select distinct a from Agreement a left join fetch a.trade t left join fetch t.venue v left join fetch t.collateral col left join fetch t.transactingParties prt left join fetch prt.party left join fetch t.rate rt left join fetch rt.fee f left join fetch rt.rebate rb left join fetch rb.fixed left join fetch rb.floating left join fetch t.instrument i left join fetch i.price left join fetch v.platform p where a.flowStatus <> 'PROCESSED'")
    List<Agreement> findAllNotProcessed();

    @Query("select a from Agreement a left join fetch a.trade t left join fetch t.venue v left join fetch t.collateral col left join fetch t.transactingParties prt left join fetch prt.party left join fetch t.rate rt left join fetch rt.fee f left join fetch rt.rebate rb left join fetch rb.fixed left join fetch rb.floating left join fetch t.instrument i left join fetch i.price left join fetch v.platform p where p.venueRefId = :venueRefId")
    List<Agreement> findByVenueRefId(@Param("venueRefId") String venueRefId);

    @Query("select a from Agreement a left join fetch a.trade t left join fetch t.venue v left join fetch t.collateral col left join fetch t.transactingParties prt left join fetch prt.party left join fetch t.rate rt left join fetch rt.fee f left join fetch rt.rebate rb left join fetch rb.fixed left join fetch rb.floating left join fetch t.instrument i left join fetch i.price left join fetch v.platform p where a.agreementId = :agreementId")
    List<Agreement> findByAgreementId(@Param("agreementId") String agreementId);
}
