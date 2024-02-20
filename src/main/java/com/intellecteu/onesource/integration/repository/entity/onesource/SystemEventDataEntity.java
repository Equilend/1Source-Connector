package com.intellecteu.onesource.integration.repository.entity.onesource;


import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.FieldImpacted;
import com.intellecteu.onesource.integration.model.integrationtoolkit.systemevent.RelatedObject;
import com.intellecteu.onesource.integration.repository.entity.toolkit.CloudSystemEventEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "event_data")
public class SystemEventDataEntity {

    @Id
    @Column(name = "event_data_id")
    private String eventDataId;

    private String message;

    @ElementCollection
    @CollectionTable(name = "event_data_fields_impacted",
        joinColumns = @JoinColumn(name = "event_data_id"))
    private List<FieldImpacted> fieldsImpacted;

    @ElementCollection
    @CollectionTable(name = "event_data_related_objects",
        joinColumns = @JoinColumn(name = "event_data_id"))
    private List<RelatedObject> relatedObjects;

    @OneToOne
    @MapsId
    @JoinColumn(name = "event_data_id", referencedColumnName = "id")
    @ToString.Exclude
    private CloudSystemEventEntity cloudEvent;

    public void setCloudEvent(CloudSystemEventEntity cloudEvent) {
        this.cloudEvent = cloudEvent;
        this.eventDataId = cloudEvent.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SystemEventDataEntity that = (SystemEventDataEntity) o;
        return eventDataId != null && eventDataId.equals(that.getEventDataId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass());
    }
}
