package com.musicstream.playlist.infrastructure.export;

import com.musicstream.playlist.application.port.out.PlaylistExporter;
import com.musicstream.playlist.domain.model.Playlist;
import com.musicstream.playlist.domain.model.Song;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Component
class JsonPlaylistExporter implements PlaylistExporter {
    private final JsonMapper jsonMapper;

    JsonPlaylistExporter(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public String format() {
        return "json";
    }

    @Override
    public String contentType() {
        return "application/json";
    }

    @Override
    public String fileExtension() {
        return "json";
    }

    @Override
    public String export(Playlist playlist) {
        List<Track> tracks = playlist.getSongs().stream().map(Track::of).toList();
        var document = new Document(playlist.getName(), playlist.getDescription(),
                playlist.totalDuration().toSeconds(), tracks);
        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(document);
    }

    record Document(String name, String description, long totalDurationSeconds, List<Track> tracks) {
    }

    record Track(String id, String title, String artist, String genre, long durationSeconds) {
        static Track of(Song song) {
            return new Track(song.id(), song.title(), song.artist(), song.genre().name(),
                    song.duration().toSeconds());
        }
    }
}
