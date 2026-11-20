package com.musicstream.playlist.infrastructure.config;

import com.musicstream.playlist.domain.analysis.PlaylistAnalyzer;
import com.musicstream.playlist.domain.recommendation.RecommendationEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class BeanConfig {
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    RecommendationEngine recommendationEngine() {
        return new RecommendationEngine();
    }

    @Bean
    PlaylistAnalyzer durationAnalyzer() {
        return PlaylistAnalyzer.DURATION;
    }

    @Bean
    PlaylistAnalyzer genreDistributionAnalyzer() {
        return PlaylistAnalyzer.GENRE_DISTRIBUTION;
    }
}
