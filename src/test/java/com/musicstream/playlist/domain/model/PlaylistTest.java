package com.musicstream.playlist.domain.model;

import com.musicstream.playlist.domain.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static com.musicstream.playlist.fixtures.TestSongs.song;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlaylistTest {
    private Playlist playlistWith(String... songIds) {
        Playlist playlist = Playlist.create("test-playlist", null, Instant.EPOCH);
        for (String id : songIds) {
            playlist.addSong(song(id));
        }
        return playlist;
    }

    private static List<String> ids(Playlist playlist) {
        return playlist.getSongs().stream().map(Song::id).toList();
    }

    @Test
    void refuses_the_same_song_twice() {
        Playlist playlist = playlistWith("a");

        assertThatThrownBy(() -> playlist.addSong(song("a")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already");
    }

    @Test
    void moves_a_song_forward_and_backward() {
        Playlist playlist = playlistWith("a", "b", "c", "d");

        playlist.moveSong("a", 2);
        assertThat(ids(playlist)).containsExactly("b", "c", "a", "d");

        playlist.moveSong("d", 0);
        assertThat(ids(playlist)).containsExactly("d", "b", "c", "a");
    }
}
