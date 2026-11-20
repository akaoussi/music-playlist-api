package com.musicstream.playlist.domain.shuffle;

import com.musicstream.playlist.domain.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

class RandomShuffleStrategy implements ShuffleStrategy {
    @Override
    public List<Song> shuffle(List<Song> songs, RandomGenerator random) {
        List<Song> shuffled = new ArrayList<>(songs);
        Collections.shuffle(shuffled, random);
        return shuffled;
    }
}
