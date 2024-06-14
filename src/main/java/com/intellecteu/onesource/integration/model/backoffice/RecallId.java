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

    private Long recallId;
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
        return Objects.equals(recallId, recallId1.getRecallId()) && Objects.equals(relatedPositionId,
            recallId1.getRelatedPositionId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(recallId, relatedPositionId);
    }
}
