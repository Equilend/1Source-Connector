package com.intellecteu.onesource.integration.model.integrationtoolkit;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FigiId implements Serializable {

    private String ticker;
    private String figi;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FigiId figiId = (FigiId) o;
        return Objects.equals(ticker, figiId.ticker) && Objects.equals(figi, figiId.figi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, figi);
    }
}
