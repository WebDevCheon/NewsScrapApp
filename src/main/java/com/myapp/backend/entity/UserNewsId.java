package com.myapp.backend.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserNewsId {
    private Long userId;
    private Long newsId;

    public UserNewsId(Long userId, Long newsId) {
        this.userId = userId;
        this.newsId = newsId;
    }
}
