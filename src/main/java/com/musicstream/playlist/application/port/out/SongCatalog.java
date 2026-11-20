package com.musicstream.playlist.application.port.out;

import com.musicstream.playlist.domain.model.Song;

import java.util.Collection;
import java.util.List;

public interface SongCatalog {
    List<Song> listAll();

    List<Song> findByIds(Collection<String> ids);
}
