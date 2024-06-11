package com.myapp.backend.controller;

import com.myapp.backend.dto.UserNewsAndNewsDto;
import com.myapp.backend.dto.SearchCondition;
import com.myapp.backend.entity.User;
import com.myapp.backend.page.PageCustom;
import com.myapp.backend.security.UserPrincipalDetails;
import com.myapp.backend.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @GetMapping("/search")
    public String findNews(SearchCondition searchCondition,
                           @PageableDefault(size = 10, page = 1) Pageable pageable,
                           @AuthenticationPrincipal UserPrincipalDetails userPrincipalDetails,
                           Model model) {
        searchCondition.setUserId(userPrincipalDetails.getUser().getId());
        Page<UserNewsAndNewsDto> newsDto = newsService.findNews(searchCondition, pageable);
        PageCustom<UserNewsAndNewsDto> userNewsAndNewsDtoPageCustom =
                new PageCustom<>(newsDto.getContent(), newsDto.getPageable(), newsDto.getTotalElements());
        model.addAttribute("content", userNewsAndNewsDtoPageCustom.getContent());
        model.addAttribute("pageableCustom", userNewsAndNewsDtoPageCustom.getPageableCustom());
        return "search/news";
    }
}