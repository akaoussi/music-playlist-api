package com.musicstream.playlist.application.port.out;

import com.musicstream.playlist.domain.model.Playlist;

public interface PlaylistExporter {
    String format();

    String contentType();

    String fileExtension();

    String export(Playlist playlist);
}
