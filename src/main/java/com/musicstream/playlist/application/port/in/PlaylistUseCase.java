package com.musicstream.playlist.application.port.in;

import com.musicstream.playlist.domain.analysis.PlaylistInsight;
import com.musicstream.playlist.domain.model.Playlist;
import com.musicstream.playlist.domain.model.Song;
import com.musicstream.playlist.domain.recommendation.Recommendation;
import com.musicstream.playlist.domain.shuffle.ShuffleMode;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PlaylistUseCase {
    Playlist create(String name, String description, List<String> songIds);

    Playlist get(UUID id);

    List<Playlist> list();

    Playlist update(UUID id, String name, String description);

    void delete(UUID id);

    Playlist addSong(UUID id, String songId);

    Playlist removeSong(UUID id, String songId);

    Playlist moveSong(UUID id, String songId, int position);

    List<Song> shuffle(UUID id, ShuffleMode mode);

    List<Recommendation> recommend(UUID id, int limit);

    Map<String, PlaylistInsight> analyze(UUID id);

    ExportedPlaylist export(UUID id, String format);

    List<Song> listSongs();

    record ExportedPlaylist(String content, String contentType, String fileName) {
    }
}
