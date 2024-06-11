package com.myapp.backend.entity;

import com.myapp.backend.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
public class Usernews extends BaseEntity implements Persistable<UserNewsId> {
    @EmbeddedId
    private UserNewsId userNewsId;

    @MapsId("userId")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("newsId")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "news_id")
    private News news;

    public Usernews(UserNewsId userNewsId) {
        this.userNewsId = userNewsId;
    }

    @Override
    public UserNewsId getId() {
        return this.userNewsId;
    }

    @Override
    public boolean isNew() {
        return getCreatedDate() == null;
    }
}
