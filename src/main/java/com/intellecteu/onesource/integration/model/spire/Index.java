package com.intellecteu.onesource.integration.model.spire;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Embeddable;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Index {

    private String indexName;
    private Double spread;

}
