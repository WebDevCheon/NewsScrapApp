package com.myapp.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.backend.dto.UserNewsAndNewsDto;
import com.myapp.backend.dto.SearchCondition;
import com.myapp.backend.entity.*;
import com.myapp.backend.repository.keyword.KeywordRepository;
import com.myapp.backend.repository.news.NewsRepository;
import com.myapp.backend.repository.user.UserRepository;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import static com.myapp.backend.entity.QKeyword.keyword;
import static com.myapp.backend.entity.QNews.news;
import static com.myapp.backend.entity.QUserkeyword.userkeyword;
import static com.myapp.backend.entity.QUsernews.usernews;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final UserRepository userRepository;
    private final NewsRepository newsRepository;
    private final KeywordRepository keywordRepository;
    private final RestTemplate restTemplate;

    @Value("${naver.apiUrl}")
    private String apiUrl;

    @Value("${naver.cliendId}")
    private String naverApiClientId;

    @Value("${naver.clientSecret}")
    private String naverApiClientSecret;

    @Value("${naver.displayLen}")
    private Integer displayLen;

    @Override
    public Page<UserNewsAndNewsDto> findNews(SearchCondition searchCondition, Pageable pageable) {
        return newsRepository.findNewsWithSearchCondition(searchCondition, pageable);
    }

    @Transactional
    public void saveKeyword(Long loginId, List<String> contentList) {   // 키워드 저장후 유저키워드에 저장 -> 키워드 저장 이전에 키워드 존재하는지 확인
        User user = userRepository.findById(loginId).get();

        for (String content : contentList) {
            Keyword keyword = keywordRepository.findByContent(content);
            Long keywordId;
            if (keyword == null) {
                keywordId = keywordRepository.save(keyword).getId();
            } else {
                keywordId = keyword.getId();
            }
            UserKeywordId userKeywordId = new UserKeywordId(user.getId(), keywordId);
            Userkeyword userkeyword = new Userkeyword(userKeywordId);
            newsRepository.saveUserKeyword(userkeyword);   // 키워드 저장
        }
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public void scrapUserNews() throws JsonProcessingException {

        List<Tuple> userKeywordList = newsRepository.findUserKeyword(); // 사용자가 입력한 키워드를 조회

        for(Tuple userKeywordTuple : userKeywordList) {
                URI uri = UriComponentsBuilder
                    .fromHttpUrl(apiUrl)
                    .path("/v1/search/news.json")
                    .queryParam("query", userKeywordTuple.get(keyword.content))  // 보고싶은 네이버 뉴스의 키워드 값
                    .queryParam("display", displayLen)      // 가져올 네이버 뉴스 기사
                    .queryParam("start", 1)
                    .queryParam("sort", "date")     // 날짜 내림차순
                    .encode()
                    .build()
                    .toUri();

                RequestEntity<Void> req = RequestEntity
                        .get(uri)
                        .header("X-Naver-Client-Id", naverApiClientId)
                        .header("X-Naver-Client-Secret", naverApiClientSecret)
                        .build();

                ResponseEntity<Map> response = restTemplate.exchange(req, Map.class);

                ObjectMapper mapper = new ObjectMapper();
                String jsonInString = mapper.writeValueAsString(response.getBody());
                Map resultMap = mapper.readValue(jsonInString, HashMap.class);

                List<HashMap<String, Object>> newsItems = (ArrayList<HashMap<String, Object>>) resultMap.get("items");

                for(HashMap<String, Object> newsItem : newsItems) {     // API 호출로 받아온 네이버 뉴스 데이터
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);   // 네이버 뉴스의 기사가 올라온 날짜
                        String title = newsItem.get("title").toString();
                        String originalLink = newsItem.get("originallink").toString();
                        String link = newsItem.get("link").toString();
                        String description = newsItem.get("link").toString();
                        LocalDateTime pubDate = LocalDateTime.parse(newsItem.get("pubDate").toString(), formatter);

                        Long userId = userKeywordTuple.get(userkeyword).getUserKeywordId().getUserId();
                        User user = userRepository.findById(userId).get();
                        News existNews = newsRepository.findNewsByCondition(
                                title, originalLink, link, description, pubDate
                        );  // 같은 네이버 뉴스의 존재 유무
                        long usernewsCount = existNews != null ? newsRepository.findUsernewsById(userId, existNews.getId()) : 0;

                        if(existNews == null) { // 같은 네이버 뉴스 데이터가 없을 때, -> 뉴스 데이터 / 유저 뉴스 데이터 생성
                            News savedNews = newsRepository.save(new News(title, originalLink, link, description, pubDate));
                            UserNewsId userNewsId = new UserNewsId(userId, savedNews.getId());
                            Usernews userNews = new Usernews(userNewsId);   // UserNews 추가
                            userNews.setNews(savedNews);
                            userNews.getNews().setId(savedNews.getId());
                            userNews.setUser(user);
                            userNews.getUser().setId(userId);
                            user.getUserNewsList().add(userNews);
                            newsRepository.saveUserNews(userNews);
                        } else {               // 이미 존재하는 네이버 뉴스를 매핑하는 Usernews가 엔티티가 User 엔티티와 매핑하고 있는 것이 아니어야 함
                            if(usernewsCount == 0) {
                                UserNewsId usernewsId = new UserNewsId(userId, existNews.getId());
                                Usernews userNews = new Usernews(usernewsId);
                                userNews.setNews(existNews);
                                userNews.setUser(user);
                                userNews.getUser().getUserNewsList().add(userNews);
                                userNews.getNews().getUserNews().add(userNews);
                                newsRepository.saveUserNews(userNews);
                            }
                        }
                }
        }
    }
}