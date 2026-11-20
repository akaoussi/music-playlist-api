package com.musicstream.playlist.domain.recommendation;

import com.musicstream.playlist.domain.model.Song;

public record Recommendation(Song song, double score) {
}
