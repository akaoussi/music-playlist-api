package com.musicstream.playlist.application.port.out;

import com.musicstream.playlist.domain.model.Playlist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaylistRepository {
    Playlist save(Playlist playlist);

    Optional<Playlist> findById(UUID id);

    List<Playlist> findAll();

    void deleteById(UUID id);
}
