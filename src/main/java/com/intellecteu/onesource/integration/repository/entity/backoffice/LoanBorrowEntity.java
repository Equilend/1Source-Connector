package com.intellecteu.onesource.integration.repository.entity.backoffice;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoanBorrowEntity {

    private Double taxWithholdingRate;

}
