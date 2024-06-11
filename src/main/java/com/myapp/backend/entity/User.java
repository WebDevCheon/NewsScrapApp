package com.myapp.backend.entity;

import com.myapp.backend.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"id", "loginId", "role", "isUsed", "isDel"})
@Table(name = "users")
public class User extends BaseEntity implements Persistable<Long>, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "USER_LOGIN_ID")
    private String loginId;

    @Column(name = "USER_ROLE")
    private String role;

    @Column(name = "USER_NAME")
    private String name;

    @Column(name = "USER_PASSWORD")
    private String password;

    @Column(name = "USER_EMAIL")
    private String email;

    @Column(name = "IS_USED")
    private String isUsed;

    @Column(name = "IS_DEL")
    private String isDel;

    @OneToMany(mappedBy = "user")
    private List<Userkeyword> keywordList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Userfavorites> favoritesList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Usernews> userNewsList = new ArrayList<>();

    public User(String loginId, String role) {
        this.loginId = loginId;
        this.role = role;
    }

    @Override
    public boolean isNew() {
        return getCreatedDate() == null;
    }
}