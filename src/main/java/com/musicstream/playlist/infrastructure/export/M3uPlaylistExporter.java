package com.musicstream.playlist.infrastructure.export;

import com.musicstream.playlist.application.port.out.PlaylistExporter;
import com.musicstream.playlist.domain.model.Playlist;
import com.musicstream.playlist.domain.model.Song;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class M3uPlaylistExporter implements PlaylistExporter {
    private final String baseUrl;

    M3uPlaylistExporter(@Value("${music.streaming.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String format() {
        return "m3u";
    }

    @Override
    public String contentType() {
        return "audio/x-mpegurl; charset=UTF-8";
    }

    @Override
    public String fileExtension() {
        return "m3u8";
    }

    @Override
    public String export(Playlist playlist) {
        StringBuilder m3u = new StringBuilder("#EXTM3U\n#PLAYLIST:").append(playlist.getName()).append('\n');
        for (Song song : playlist.getSongs()) {
            m3u.append("#EXTINF:").append(song.duration().toSeconds()).append(',').append(song.artist()).append(" - ").append(song.title())
                    .append('\n').append(baseUrl).append(song.id()).append('\n');
        }
        return m3u.toString();
    }
}
