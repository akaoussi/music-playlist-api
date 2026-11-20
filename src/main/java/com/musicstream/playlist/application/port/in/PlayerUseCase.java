package com.musicstream.playlist.application.port.in;

import com.musicstream.playlist.domain.player.PlaybackSession;
import com.musicstream.playlist.domain.shuffle.ShuffleMode;

import java.util.UUID;

public interface PlayerUseCase {
    PlaybackSession state();

    PlaybackSession play(UUID playlistId, ShuffleMode mode);

    PlaybackSession stop();

    void stopIfPlaying(UUID playlistId);
}
