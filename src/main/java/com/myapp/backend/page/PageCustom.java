package com.myapp.backend.page;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
public class PageCustom<T> implements Serializable {

    private List<T> content;

    private PageableCustom pageableCustom;

    public PageCustom(List<T> content, Pageable pageable, long total) {
        this.content = content;
        this.pageableCustom = new PageableCustom(new PageImpl(content, pageable, total));
    }

    public PageCustom(List<T> content, Pageable pageable, boolean hasNext) {
        this.content = content;
        this.pageableCustom = new PageableCustom(new SliceImpl(content, pageable, hasNext));
    }

}