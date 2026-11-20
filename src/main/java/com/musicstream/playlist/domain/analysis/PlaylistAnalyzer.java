package com.musicstream.playlist.domain.analysis;

import com.musicstream.playlist.domain.model.Genre;
import com.musicstream.playlist.domain.model.Playlist;
import com.musicstream.playlist.domain.model.Song;

import java.util.Map;
import java.util.stream.Collectors;

@FunctionalInterface
public interface PlaylistAnalyzer {
    PlaylistInsight analyze(Playlist playlist);

    PlaylistAnalyzer DURATION = playlist -> {
        int songCount = playlist.getSongs().size();
        long totalSeconds = playlist.totalDuration().toSeconds();
        long averageSeconds = songCount == 0 ? 0 : totalSeconds / songCount;
        return new PlaylistInsight.Duration(songCount, totalSeconds, averageSeconds);
    };

    PlaylistAnalyzer GENRE_DISTRIBUTION = playlist -> {
        Map<Genre, Long> songCountByGenre = playlist.getSongs().stream()
                .collect(Collectors.groupingBy(Song::genre, Collectors.counting()));
        Genre dominant = songCountByGenre.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        return new PlaylistInsight.GenreDistribution(songCountByGenre, dominant);
    };
}
