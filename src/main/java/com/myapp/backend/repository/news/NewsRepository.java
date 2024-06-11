package com.myapp.backend.repository.news;

import com.myapp.backend.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface NewsRepository extends JpaRepository<News, Long>, NewsCustomRepository {

}