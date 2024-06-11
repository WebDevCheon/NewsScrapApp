package com.myapp.backend.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserKeywordId implements Serializable {
    private Long userId;
    private Long keywordId;

    public UserKeywordId(Long userId, Long keywordId) {
        this.userId = userId;
        this.keywordId = keywordId;
    }
}