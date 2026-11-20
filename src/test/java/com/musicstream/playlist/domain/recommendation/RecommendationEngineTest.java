package com.musicstream.playlist.domain.recommendation;

import com.musicstream.playlist.domain.model.Genre;
import com.musicstream.playlist.domain.model.Playlist;
import com.musicstream.playlist.domain.model.Song;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static com.musicstream.playlist.fixtures.TestSongs.song;
import static org.assertj.core.api.Assertions.assertThat;

class RecommendationEngineTest {
    private final RecommendationEngine engine = new RecommendationEngine();

    @Test
    void ranks_same_genre_then_compatible_genre_then_unrelated_and_skips_known_songs() {
        Song alreadyThere = song("p1", "artist-1", Genre.BLUES);
        Playlist playlist = Playlist.create("test-playlist", null, Instant.EPOCH);
        playlist.addSong(alreadyThere);
        playlist.addSong(song("p2", "artist-2", Genre.BLUES));

        Song sameGenre = song("c1", "artist-3", Genre.BLUES);
        Song compatibleGenre = song("c2", "artist-4", Genre.JAZZ);
        Song unrelated = song("c3", "artist-5", Genre.POP);

        List<Recommendation> result = engine.recommend(playlist,
                List.of(unrelated, alreadyThere, compatibleGenre, sameGenre), 10);

        assertThat(result).extracting(Recommendation::song).containsExactly(sameGenre, compatibleGenre, unrelated);
        assertThat(result).extracting(Recommendation::score).containsExactly(1.0, 0.6, 0.1);
    }
}
