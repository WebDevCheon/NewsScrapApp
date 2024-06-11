package com.myapp.backend.page;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Slice;

@Getter
@NoArgsConstructor
public class PageableCustom {

    private boolean first;
    private boolean last;
    private boolean hasNext;
    private int totalPages;
    private long totalElements;
    private int page;
    private int size;

    public PageableCustom(PageImpl page) {
        this.first = page.isFirst();
        this.last = page.isLast();
        this.hasNext = page.hasNext();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.page = page.getNumber() + 1;
        this.size = page.getSize();
    }

    public PageableCustom(Slice slice) {
        this.first = slice.isFirst();
        this.last = slice.isLast();
        this.hasNext = slice.hasNext();
        this.page = slice.getNumber() + 1;
        this.size = slice.getSize();
    }
}