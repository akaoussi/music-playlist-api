package com.musicstream.playlist.domain.shuffle;

import com.musicstream.playlist.domain.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

class SmartShuffleStrategy implements ShuffleStrategy {
    @Override
    public List<Song> shuffle(List<Song> songs, RandomGenerator random) {
        List<Song> shuffled = new ArrayList<>(songs);
        Collections.shuffle(shuffled, random);

        Map<String, List<Song>> songsByArtist = new LinkedHashMap<>();
        for (Song song : shuffled) {
            songsByArtist.computeIfAbsent(song.artist().strip().toLowerCase(), artistKey -> new ArrayList<>())
                    .add(song);
        }

        List<Song> result = new ArrayList<>(songs.size());
        String previousArtist = null;
        while (result.size() < songs.size()) {
            String artistToAvoid = previousArtist;
            String artist = songsByArtist.entrySet().stream()
                    .filter(entry -> !entry.getValue().isEmpty() && !entry.getKey().equals(artistToAvoid))
                    .max(Comparator.comparingInt(entry -> entry.getValue().size()))
                    .map(Map.Entry::getKey)
                    .orElse(artistToAvoid);
            result.add(songsByArtist.get(artist).removeLast());
            previousArtist = artist;
        }
        return result;
    }
}
