package com.musicstream.playlist.domain.analysis;

import com.musicstream.playlist.domain.model.Genre;

import java.util.Map;

public sealed interface PlaylistInsight permits PlaylistInsight.Duration, PlaylistInsight.GenreDistribution {
    String name();

    record Duration(int songCount, long totalSeconds, long averageSeconds) implements PlaylistInsight {
        @Override
        public String name() {
            return "duration";
        }
    }

    record GenreDistribution(Map<Genre, Long> songCountByGenre, Genre dominant) implements PlaylistInsight {
        @Override
        public String name() {
            return "genreDistribution";
        }
    }
}
