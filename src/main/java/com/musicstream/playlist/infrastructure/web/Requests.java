package com.musicstream.playlist.infrastructure.web;

import com.musicstream.playlist.domain.model.Playlist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

final class Requests {
    private Requests() {
    }

    record CreatePlaylist(@NotBlank @Size(max = Playlist.MAX_NAME_LENGTH) String name,
                          @Size(max = 500) String description,
                          @Size(max = Playlist.MAX_SONGS) List<@NotBlank String> songIds) {
    }

    record UpdatePlaylist(@NotBlank @Size(max = Playlist.MAX_NAME_LENGTH) String name,
                          @Size(max = 500) String description) {
    }

    record AddSong(@NotBlank String songId) {
    }

    record MoveSong(@NotNull @PositiveOrZero Integer position) {
    }

    record Play(@NotNull UUID playlistId, String shuffle) {
    }
}
