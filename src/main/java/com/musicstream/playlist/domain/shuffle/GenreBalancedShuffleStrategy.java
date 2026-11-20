package com.musicstream.playlist.domain.shuffle;

import com.musicstream.playlist.domain.model.Genre;
import com.musicstream.playlist.domain.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

class GenreBalancedShuffleStrategy implements ShuffleStrategy {
    @Override
    public List<Song> shuffle(List<Song> songs, RandomGenerator random) {
        Map<Genre, List<Song>> songsByGenre = songs.stream().collect(Collectors.groupingBy(Song::genre));

        List<PositionedSong> positionedSongs = new ArrayList<>(songs.size());
        songsByGenre.values().forEach(songsOfOneGenre -> {
            List<Song> shuffled = new ArrayList<>(songsOfOneGenre);
            Collections.shuffle(shuffled, random);
            double spacing = 1.0 / shuffled.size();
            double startOffset = random.nextDouble() * spacing;
            for (int index = 0; index < shuffled.size(); index++) {
                positionedSongs.add(new PositionedSong(shuffled.get(index), startOffset + index * spacing));
            }
        });

        return positionedSongs.stream()
                .sorted(Comparator.comparingDouble(PositionedSong::position))
                .map(PositionedSong::song)
                .toList();
    }

    private record PositionedSong(Song song, double position) {
    }
}
