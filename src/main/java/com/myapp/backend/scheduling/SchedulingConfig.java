package com.myapp.backend.scheduling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.myapp.backend.service.NewsService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfig {

    private final NewsService newsService;

    @Scheduled(cron = "0 * * * * ?", zone = "Asia/Seoul")
    public void scheduleNewsScraping() throws JsonProcessingException {
        newsService.scrapUserNews();
    }
}
