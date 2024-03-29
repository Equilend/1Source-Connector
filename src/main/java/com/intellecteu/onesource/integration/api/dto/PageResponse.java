package com.intellecteu.onesource.integration.api.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

    private Long totalItems;
    private Integer currentPage;
    private Integer totalPages;
    private List<T> items;

    public PageResponse(Page page) {
        this.totalItems = Long.valueOf(page.getTotalElements());
        this.currentPage = Integer.valueOf(page.getPageable().getPageNumber());
        this.totalPages = Integer.valueOf(page.getTotalPages());
        this.items = (List<T>) page.getContent();
    }
}
