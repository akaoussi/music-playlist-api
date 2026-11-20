package com.musicstream.playlist.fixtures;

import com.musicstream.playlist.domain.model.Genre;
import com.musicstream.playlist.domain.model.Song;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public final class TestSongs {
    private TestSongs() {
    }

    public static Song song(String id, String artist, Genre genre) {
        return new Song(id, "Title " + id, artist, "Album", genre, Duration.ofSeconds(200));
    }

    public static Song song(String id) {
        return song(id, "Artist " + id, Genre.POP);
    }

    public static List<Song> songs(String... ids) {
        return Arrays.stream(ids).map(TestSongs::song).toList();
    }
}
