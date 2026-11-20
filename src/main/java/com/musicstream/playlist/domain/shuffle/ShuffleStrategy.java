package com.musicstream.playlist.domain.shuffle;

import com.musicstream.playlist.domain.model.Song;

import java.util.List;
import java.util.random.RandomGenerator;

public interface ShuffleStrategy {
    List<Song> shuffle(List<Song> songs, RandomGenerator random);
}
