package com.musicstream.playlist.domain.player;

import com.musicstream.playlist.domain.exception.BusinessException;
import com.musicstream.playlist.domain.model.Song;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record PlaybackSession(PlayerStatus status, UUID playlistId, List<Song> queue, String shuffle) {
    public PlaybackSession {
        queue = List.copyOf(queue);
    }

    public static PlaybackSession stopped() {
        return new PlaybackSession(PlayerStatus.STOPPED, null, List.of(), null);
    }

    public PlaybackSession start(UUID playlistId, List<Song> songs, String shuffle) {
        if (songs.isEmpty()) {
            throw new BusinessException("Cannot play an empty playlist");
        }
        return new PlaybackSession(PlayerStatus.PLAYING, playlistId, songs, shuffle);
    }

    public Optional<Song> currentSong() {
        return queue.stream().findFirst();
    }
}
