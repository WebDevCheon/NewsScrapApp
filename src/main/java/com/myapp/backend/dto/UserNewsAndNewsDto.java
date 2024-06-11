package com.myapp.backend.dto;

import com.myapp.backend.entity.UserNewsId;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserNewsAndNewsDto {

    private UserNewsId userNewsId;

    private String title;

    private String originalLink;

    private String link;

    private String description;

    private LocalDateTime pubDate;

    @QueryProjection
    public UserNewsAndNewsDto(UserNewsId userNewsId, String title, String originalLink, String link, String description, LocalDateTime pubDate) {
        this.userNewsId = userNewsId;
        this.title = title;
        this.originalLink = originalLink;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
    }
}
