package com.myapp.backend.repository.keyword;

import com.myapp.backend.entity.Keyword;
import com.myapp.backend.entity.Usernews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Keyword findByContent(String content);
}
