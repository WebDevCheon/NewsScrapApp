package com.myapp.backend.entity;

import com.myapp.backend.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News extends BaseEntity implements Persistable<Long> {     // API로 읽어왔던 과거 뉴스 데이터 저장
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String originalLink;

    private String link;

    private String description;

    private LocalDateTime pubDate;

    public News(String title, String originalLink, String link, String description, LocalDateTime pubDate) {
        this.title = title;
        this.originalLink = originalLink;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
    }

    @OneToMany(mappedBy = "news")
    private List<Usernews> userNews = new ArrayList<>();
    @Override
    public boolean isNew() {
        return getCreatedDate() == null;
    }
}