package com.musicstream.playlist.domain.model;

import com.musicstream.playlist.domain.exception.BusinessException;
import com.musicstream.playlist.domain.exception.NotFoundException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Playlist {
    public static final int MAX_SONGS = 500;
    public static final int MAX_NAME_LENGTH = 100;

    private final UUID id;
    private final List<Song> songs;
    private String name;
    private String description;
    private Instant updatedAt;

    private Playlist(UUID id, String name, String description, List<Song> songs, Instant updatedAt) {
        this.id = id;
        this.songs = new ArrayList<>(songs);
        this.updatedAt = updatedAt;
        rename(name, description);
    }

    public static Playlist create(String name, String description, Instant now) {
        return new Playlist(UUID.randomUUID(), name, description, List.of(), now);
    }

    public static Playlist restore(UUID id, String name, String description, List<Song> songs, Instant updatedAt) {
        return new Playlist(id, name, description, songs, updatedAt);
    }

    public void rename(String newName, String newDescription) {
        if (newName == null || newName.isBlank()) {
            throw new BusinessException("Playlist name is required");
        }
        if (newName.strip().length() > MAX_NAME_LENGTH) {
            throw new BusinessException("Playlist name must not exceed %d characters".formatted(MAX_NAME_LENGTH));
        }
        this.name = newName.strip();
        this.description = newDescription == null || newDescription.isBlank() ? null : newDescription.strip();
    }

    public void markUpdated(Instant now) {
        this.updatedAt = now;
    }

    public void addSong(Song song) {
        if (contains(song.id())) {
            throw new BusinessException("Song %s is already in playlist '%s'".formatted(song.id(), name));
        }
        if (songs.size() >= MAX_SONGS) {
            throw new BusinessException("A playlist cannot contain more than %d songs".formatted(MAX_SONGS));
        }
        songs.add(song);
    }

    public void removeSong(String songId) {
        if (!songs.removeIf(song -> song.id().equals(songId))) {
            throw new NotFoundException("Song %s is not in the playlist".formatted(songId));
        }
    }

    public void moveSong(String songId, int newPosition) {
        if (newPosition < 0 || newPosition >= songs.size()) {
            throw new BusinessException("Position %d is out of range [0, %d]".formatted(newPosition, songs.size() - 1));
        }
        Song song = songs.stream().filter(candidate -> candidate.id().equals(songId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Song %s is not in the playlist".formatted(songId)));
        songs.remove(song);
        songs.add(newPosition, song);
    }

    public boolean contains(String songId) {
        return songs.stream().anyMatch(song -> song.id().equals(songId));
    }

    public Duration totalDuration() {
        return songs.stream().map(Song::duration).reduce(Duration.ZERO, Duration::plus);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Song> getSongs() {
        return List.copyOf(songs);
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
