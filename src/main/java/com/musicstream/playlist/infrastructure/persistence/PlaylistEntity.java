package com.musicstream.playlist.infrastructure.persistence;

import com.musicstream.playlist.domain.model.Playlist;
import com.musicstream.playlist.domain.model.Song;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "playlist")
class PlaylistEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = Playlist.MAX_NAME_LENGTH)
    private String name;

    @Column(length = 500)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "playlist_song", joinColumns = @JoinColumn(name = "playlist_id"))
    @OrderColumn(name = "position")
    @Column(name = "song_id", nullable = false)
    private List<String> songIds = new ArrayList<>();

    @Column(nullable = false)
    private Instant updatedAt;

    protected PlaylistEntity() {
    }

    static PlaylistEntity from(Playlist playlist) {
        PlaylistEntity entity = new PlaylistEntity();
        entity.id = playlist.getId();
        entity.name = playlist.getName();
        entity.description = playlist.getDescription();
        entity.songIds = playlist.getSongs().stream().map(Song::id).collect(Collectors.toCollection(ArrayList::new));
        entity.updatedAt = playlist.getUpdatedAt();
        return entity;
    }

    Playlist toDomain(List<Song> songs) {
        return Playlist.restore(id, name, description, songs, updatedAt);
    }

    List<String> songIds() {
        return songIds;
    }
}
