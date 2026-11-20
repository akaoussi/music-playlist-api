package com.musicstream.playlist.infrastructure.persistence;

import com.musicstream.playlist.application.port.out.PlaylistRepository;
import com.musicstream.playlist.application.port.out.SongCatalog;
import com.musicstream.playlist.domain.model.Playlist;
import com.musicstream.playlist.domain.model.Song;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
class PersistenceAdapter implements PlaylistRepository, SongCatalog {
    private final PlaylistJpaRepository playlistJpa;
    private final SongJpaRepository songJpa;

    PersistenceAdapter(PlaylistJpaRepository playlistJpa, SongJpaRepository songJpa) {
        this.playlistJpa = playlistJpa;
        this.songJpa = songJpa;
    }

    @Override
    public Playlist save(Playlist playlist) {
        playlistJpa.save(PlaylistEntity.from(playlist));
        return playlist;
    }

    @Override
    public Optional<Playlist> findById(UUID id) {
        return playlistJpa.findById(id).map(entity -> toDomain(List.of(entity)).getFirst());
    }

    @Override
    public List<Playlist> findAll() {
        return toDomain(playlistJpa.findAllByOrderByUpdatedAtDesc());
    }

    @Override
    public void deleteById(UUID id) {
        playlistJpa.deleteById(id);
    }

    @Override
    public List<Song> listAll() {
        return songJpa.findAll().stream().map(SongEntity::toDomain).toList();
    }

    @Override
    public List<Song> findByIds(Collection<String> ids) {
        return songJpa.findAllById(ids).stream().map(SongEntity::toDomain).toList();
    }

    private List<Playlist> toDomain(List<PlaylistEntity> entities) {
        List<String> allIds = entities.stream().flatMap(entity -> entity.songIds().stream()).distinct().toList();
        Map<String, Song> songs = songJpa.findAllById(allIds).stream().map(SongEntity::toDomain)
                .collect(Collectors.toMap(Song::id, Function.identity()));
        return entities.stream()
                .map(entity -> entity.toDomain(entity.songIds().stream().map(songs::get).toList()))
                .toList();
    }
}
