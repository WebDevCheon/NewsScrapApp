package com.myapp.backend.entity;

import com.myapp.backend.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Userkeyword extends BaseEntity implements Persistable<UserKeywordId> {
    @EmbeddedId
    private UserKeywordId userKeywordId;

    @MapsId("userId")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("keywordId")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    public Userkeyword(UserKeywordId userKeywordId) {
        this.userKeywordId = userKeywordId;
    }

    @Override
    public UserKeywordId getId() {
        return this.userKeywordId;
    }

    @Override
    public boolean isNew() {
        return getCreatedDate() == null;
    }
}
