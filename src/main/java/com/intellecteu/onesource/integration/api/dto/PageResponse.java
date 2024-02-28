package com.intellecteu.onesource.integration.api.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PageResponse<T> {

    private Long totalItems;
    private Integer currentPage;
    private Integer totalPages;
    private List<T> items;

}
