package com.musicstream.playlist.domain.recommendation;

import com.musicstream.playlist.domain.exception.BusinessException;
import com.musicstream.playlist.domain.model.Playlist;
import com.musicstream.playlist.domain.model.Song;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class RecommendationEngine {

    public List<Recommendation> recommend(Playlist playlist, Collection<Song> catalog, int limit) {
        List<Song> playlistSongs = playlist.getSongs();
        if (playlistSongs.isEmpty()) {
            throw new BusinessException("Add at least one song to the playlist to get recommendations");
        }
        return catalog.stream()
                .filter(candidate -> !playlist.contains(candidate.id()))
                .map(candidate -> new Recommendation(candidate, genreScore(candidate, playlistSongs)))
                .sorted(Comparator.comparingDouble(Recommendation::score).reversed()
                        .thenComparing(recommendation -> recommendation.song().title()))
                .limit(limit)
                .toList();
    }

    private static double genreScore(Song candidate, List<Song> playlistSongs) {
        return playlistSongs.stream()
                .mapToDouble(song -> candidate.genre().compatibilityWith(song.genre()))
                .average()
                .orElse(0);
    }
}
