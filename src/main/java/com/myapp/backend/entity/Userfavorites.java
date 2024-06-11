package com.myapp.backend.entity;

import com.myapp.backend.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Userfavorites extends BaseEntity implements Persistable<UserFavoritesId> {
    @EmbeddedId
    private UserFavoritesId favoritesId;

    @MapsId("userId")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("favoritesId")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "favorites_id")
    private Favorites favorites;

    @Override
    public boolean isNew() {
        return getCreatedDate() == null;
    }

    @Override
    public UserFavoritesId getId() {
        return this.favoritesId;
    }
}
