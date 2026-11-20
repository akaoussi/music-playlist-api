package com.musicstream.playlist.domain.shuffle;

import com.musicstream.playlist.domain.model.Genre;
import com.musicstream.playlist.domain.model.Song;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static com.musicstream.playlist.fixtures.TestSongs.song;
import static org.assertj.core.api.Assertions.assertThat;

class ShuffleStrategiesTest {
    @Test
    void smart_shuffle_never_plays_the_same_artist_twice_in_a_row() {
        List<Song> songs = List.of(
                song("1", "artist-a", Genre.ROCK), song("2", "artist-a", Genre.ROCK),
                song("3", "artist-a", Genre.ROCK), song("4", "artist-a", Genre.ROCK),
                song("5", "artist-b", Genre.POP), song("6", "artist-b", Genre.POP),
                song("7", "artist-c", Genre.POP), song("8", "artist-d", Genre.JAZZ));

        for (int seed = 0; seed < 50; seed++) {
            List<Song> result = new SmartShuffleStrategy().shuffle(songs, new Random(seed));
            for (int index = 1; index < result.size(); index++) {
                assertThat(result.get(index).artist()).isNotEqualTo(result.get(index - 1).artist());
            }
        }
    }
}
