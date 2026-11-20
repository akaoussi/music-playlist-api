package com.musicstream.playlist.domain.model;

import java.time.Duration;

public record Song(String id, String title, String artist, String album, Genre genre, Duration duration) {
}
