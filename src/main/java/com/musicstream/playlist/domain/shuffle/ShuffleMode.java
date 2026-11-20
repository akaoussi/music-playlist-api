package com.musicstream.playlist.domain.shuffle;

import com.musicstream.playlist.domain.exception.BusinessException;
import com.musicstream.playlist.domain.model.Song;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public enum ShuffleMode {
    RANDOM(new RandomShuffleStrategy()),
    SMART(new SmartShuffleStrategy()),
    GENRE_BALANCED(new GenreBalancedShuffleStrategy());

    private final ShuffleStrategy strategy;

    ShuffleMode(ShuffleStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Song> shuffle(List<Song> songs) {
        return strategy.shuffle(songs, ThreadLocalRandom.current());
    }

    public static ShuffleMode fromApiName(String apiName) {
        try {
            return valueOf(apiName.strip().replace('-', '_').toUpperCase());
        } catch (IllegalArgumentException | NullPointerException unknown) {
            throw new BusinessException("Unknown shuffle strategy '%s'. Available: random, smart, genre-balanced"
                    .formatted(apiName));
        }
    }

    public String apiName() {
        return name().toLowerCase().replace('_', '-');
    }
}
