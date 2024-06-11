package com.myapp.backend.repository.news;

import com.myapp.backend.dto.UserNewsAndNewsDto;
import com.myapp.backend.dto.SearchCondition;
import com.myapp.backend.entity.News;
import com.myapp.backend.entity.Userkeyword;
import com.myapp.backend.entity.Usernews;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface NewsCustomRepository {
    public Page<UserNewsAndNewsDto> findNewsWithSearchCondition(SearchCondition searchCondition, Pageable pageable);   // 클라이언트가 검색창에서 직접 찾은 뉴스
    public void saveUserKeyword(Userkeyword userkeyword);    // 클라이언트가 지정한 키워드(자기가 보고 싶은 기사의 키워들 저장하면, 정해진 시간에 기사 스케줄러빈이 기사 스크랩)
    public void saveUserNews(Usernews userNews);    // 클라이언트가 지정한 키워드의 뉴스
    public List<Tuple> findUserKeyword();   // 클라이언트가 지정한 키워드 조회 (주기적인 시간에 기사 스크랩)
    public News findNewsByCondition(String title, String originalLink, String link, String description, LocalDateTime pubDate); // 네이버 뉴스 API로부터 가져온 Body 응답값을 가지고 New 데이터 조회
    public long findUsernewsById(Long userId, Long newsId);
}