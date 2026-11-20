package com.musicstream.playlist.infrastructure.persistence;

import com.musicstream.playlist.domain.model.Genre;
import com.musicstream.playlist.domain.model.Song;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Duration;

@Entity
@Table(name = "song")
class SongEntity {
    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    private String album;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Genre genre;

    @Column(name = "duration_seconds", nullable = false)
    private int durationSeconds;

    protected SongEntity() {
    }

    Song toDomain() {
        return new Song(id, title, artist, album, genre, Duration.ofSeconds(durationSeconds));
    }
}
