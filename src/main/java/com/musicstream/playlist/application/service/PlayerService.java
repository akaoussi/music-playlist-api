package com.musicstream.playlist.application.service;

import com.musicstream.playlist.application.port.in.PlayerUseCase;
import com.musicstream.playlist.application.port.out.PlaylistRepository;
import com.musicstream.playlist.domain.exception.NotFoundException;
import com.musicstream.playlist.domain.model.Playlist;
import com.musicstream.playlist.domain.model.Song;
import com.musicstream.playlist.domain.player.PlaybackSession;
import com.musicstream.playlist.domain.shuffle.ShuffleMode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PlayerService implements PlayerUseCase {
    private final AtomicReference<PlaybackSession> session = new AtomicReference<>(PlaybackSession.stopped());
    private final PlaylistRepository playlists;

    PlayerService(PlaylistRepository playlists) {
        this.playlists = playlists;
    }

    @Override
    public PlaybackSession state() {
        return session.get();
    }

    @Override
    public PlaybackSession play(UUID playlistId, ShuffleMode mode) {
        Playlist playlist = playlists.findById(playlistId)
                .orElseThrow(() -> new NotFoundException("Playlist %s not found".formatted(playlistId)));
        List<Song> queue = mode == null ? playlist.getSongs() : mode.shuffle(playlist.getSongs());
        String shuffle = mode == null ? null : mode.apiName();
        return session.updateAndGet(current -> current.start(playlistId, queue, shuffle));
    }

    @Override
    public PlaybackSession stop() {
        return session.updateAndGet(current -> PlaybackSession.stopped());
    }

    @Override
    public void stopIfPlaying(UUID playlistId) {
        session.updateAndGet(current -> playlistId.equals(current.playlistId()) ? PlaybackSession.stopped() : current);
    }
}
