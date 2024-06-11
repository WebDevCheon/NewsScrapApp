package com.myapp.backend.repository.news;

import com.myapp.backend.dto.QUserNewsAndNewsDto;
import com.myapp.backend.dto.UserNewsAndNewsDto;
import com.myapp.backend.dto.SearchCondition;
import com.myapp.backend.entity.News;
import com.myapp.backend.entity.Userkeyword;
import com.myapp.backend.entity.Usernews;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import java.time.LocalDateTime;
import java.util.List;

import static com.myapp.backend.entity.QKeyword.keyword;
import static com.myapp.backend.entity.QNews.news;
import static com.myapp.backend.entity.QUserkeyword.userkeyword;
import static com.myapp.backend.entity.QUsernews.usernews;

public class NewsCustomRepositoryImpl implements NewsCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    public NewsCustomRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public Page<UserNewsAndNewsDto> findNewsWithSearchCondition(SearchCondition searchCondition, Pageable pageable) {
        List<UserNewsAndNewsDto> result = queryFactory
                .select(new QUserNewsAndNewsDto(
                     usernews.userNewsId,
                     news.title,
                     news.originalLink,
                     news.link,
                     news.description,
                     news.pubDate
                ))
                .from(usernews)
                .leftJoin(news)
                .on((usernews.userNewsId.newsId).eq(news.id))
                .where(
                       news.title.like(searchCondition.getQuery()),
                       usernews.userNewsId.userId.eq(searchCondition.getUserId())
                      )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(news.count())
                .from(news)
                .leftJoin(news)
                .on((usernews.userNewsId.newsId).eq(news.id))
                .where(
                        news.title.like(searchCondition.getQuery()),
                        usernews.userNewsId.userId.eq(searchCondition.getUserId())
                );

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    @Override
    public void saveUserKeyword(Userkeyword userkeyword) {
        entityManager.persist(userkeyword);
    }

    @Override
    public List<Tuple> findUserKeyword() {
        return queryFactory
                .select(
                        userkeyword,
                        keyword.content         // 유저가 지정한 키워드의 String 값
                )
                .from(userkeyword)
                .innerJoin(keyword)
                .on(userkeyword.userKeywordId.keywordId.eq(keyword.id))
                .fetch();
    }

    @Override
    public News findNewsByCondition(String title, String originalLink, String link,
                                     String description, LocalDateTime pubDate) {
        return queryFactory
                .select(
                      news
                )
                .from(news)
                .where(
                    news.title.eq(title),
                    news.originalLink.eq(originalLink),
                    news.link.eq(link),
                    news.description.eq(description),
                    news.pubDate.eq(pubDate)
                )
                .fetchOne();
    }

    @Override
    public long findUsernewsById(Long userId, Long newsId) {
        return queryFactory
                .select(usernews.count())
                .from(usernews)
                .where(
                      usernews.userNewsId.userId.eq(userId),
                      usernews.userNewsId.newsId.eq(newsId)
                )
                .fetchOne();
    }

    @Override
    public void saveUserNews(Usernews userNews) {
        entityManager.persist(userNews);
    }
}
