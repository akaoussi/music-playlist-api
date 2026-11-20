package com.musicstream.playlist.infrastructure.web;

import com.musicstream.playlist.domain.model.Playlist;
import com.musicstream.playlist.domain.model.Song;
import com.musicstream.playlist.domain.player.PlaybackSession;
import com.musicstream.playlist.domain.recommendation.Recommendation;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

final class Responses {
    private Responses() {
    }

    record SongView(String id, String title, String artist, String album, String genre, long durationSeconds) {
        static SongView from(Song song) {
            return new SongView(song.id(), song.title(), song.artist(), song.album(), song.genre().name(),
                    song.duration().toSeconds());
        }
    }

    record PlaylistView(UUID id, String name, String description, int songCount, long totalDurationSeconds,
                        List<SongView> songs, Instant updatedAt) {
        static PlaylistView from(Playlist playlist) {
            return new PlaylistView(playlist.getId(), playlist.getName(), playlist.getDescription(),
                    playlist.getSongs().size(), playlist.totalDuration().toSeconds(),
                    playlist.getSongs().stream().map(SongView::from).toList(), playlist.getUpdatedAt());
        }
    }

    record RecommendationView(SongView song, double score) {
        static RecommendationView from(Recommendation recommendation) {
            return new RecommendationView(SongView.from(recommendation.song()), recommendation.score());
        }
    }

    record PlayerView(String status, UUID playlistId, SongView currentSong, int queueSize, String shuffle) {
        static PlayerView from(PlaybackSession session) {
            return new PlayerView(session.status().name(), session.playlistId(),
                    session.currentSong().map(SongView::from).orElse(null),
                    session.queue().size(), session.shuffle());
        }
    }
}
