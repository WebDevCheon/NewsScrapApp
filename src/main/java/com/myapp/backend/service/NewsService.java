package com.myapp.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.myapp.backend.dto.UserNewsAndNewsDto;
import com.myapp.backend.dto.SearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface NewsService {
    public Page<UserNewsAndNewsDto> findNews(SearchCondition searchCondition, Pageable pageable);
    public void saveKeyword(Long loginId, List<String> keyword);
    public void scrapUserNews() throws JsonProcessingException;
}
