package com.intellecteu.onesource.integration.model.backoffice;

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
public class RecallId implements Serializable {

    private String recallId;
    private Long relatedPositionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RecallId recallId1 = (RecallId) o;
        return Objects.equals(recallId, recallId1.recallId) && Objects.equals(relatedPositionId,
            recallId1.relatedPositionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recallId, relatedPositionId);
    }
}
