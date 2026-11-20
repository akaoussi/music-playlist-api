package com.musicstream.playlist.application.service;

import com.musicstream.playlist.application.port.in.PlayerUseCase;
import com.musicstream.playlist.application.port.in.PlaylistUseCase;
import com.musicstream.playlist.application.port.out.PlaylistExporter;
import com.musicstream.playlist.application.port.out.PlaylistRepository;
import com.musicstream.playlist.application.port.out.SongCatalog;
import com.musicstream.playlist.domain.analysis.PlaylistAnalyzer;
import com.musicstream.playlist.domain.analysis.PlaylistInsight;
import com.musicstream.playlist.domain.exception.BusinessException;
import com.musicstream.playlist.domain.exception.NotFoundException;
import com.musicstream.playlist.domain.model.Playlist;
import com.musicstream.playlist.domain.model.Song;
import com.musicstream.playlist.domain.recommendation.Recommendation;
import com.musicstream.playlist.domain.recommendation.RecommendationEngine;
import com.musicstream.playlist.domain.shuffle.ShuffleMode;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PlaylistService implements PlaylistUseCase {
    private final PlaylistRepository playlists;
    private final SongCatalog catalog;
    private final RecommendationEngine recommendations;
    private final List<PlaylistAnalyzer> analyzers;
    private final Map<String, PlaylistExporter> exporters = new TreeMap<>();
    private final PlayerUseCase player;
    private final Clock clock;

    public PlaylistService(PlaylistRepository playlists, SongCatalog catalog, RecommendationEngine recommendations,
                           List<PlaylistAnalyzer> analyzers, List<PlaylistExporter> exporters, PlayerUseCase player,
                           Clock clock) {
        this.playlists = playlists;
        this.catalog = catalog;
        this.recommendations = recommendations;
        this.analyzers = analyzers;
        exporters.forEach(exporter -> this.exporters.put(exporter.format(), exporter));
        this.player = player;
        this.clock = clock;
    }

    @Override
    public Playlist create(String name, String description, List<String> songIds) {
        Playlist playlist = Playlist.create(name, description, clock.instant());
        findSongs(songIds).forEach(playlist::addSong);
        return playlists.save(playlist);
    }

    @Override
    public Playlist get(UUID id) {
        return playlists.findById(id).orElseThrow(() -> new NotFoundException("Playlist %s not found".formatted(id)));
    }

    @Override
    public List<Playlist> list() {
        return playlists.findAll();
    }

    @Override
    public Playlist update(UUID id, String name, String description) {
        Playlist playlist = get(id);
        playlist.rename(name, description);
        return save(playlist);
    }

    @Override
    public void delete(UUID id) {
        get(id);
        playlists.deleteById(id);
        player.stopIfPlaying(id);
    }

    @Override
    public Playlist addSong(UUID id, String songId) {
        Playlist playlist = get(id);
        playlist.addSong(findSongs(List.of(songId)).getFirst());
        return save(playlist);
    }

    @Override
    public Playlist removeSong(UUID id, String songId) {
        Playlist playlist = get(id);
        playlist.removeSong(songId);
        return save(playlist);
    }

    @Override
    public Playlist moveSong(UUID id, String songId, int position) {
        Playlist playlist = get(id);
        playlist.moveSong(songId, position);
        return save(playlist);
    }

    @Override
    public List<Song> shuffle(UUID id, ShuffleMode mode) {
        return mode.shuffle(get(id).getSongs());
    }

    @Override
    public List<Recommendation> recommend(UUID id, int limit) {
        return recommendations.recommend(get(id), catalog.listAll(), limit);
    }

    @Override
    public Map<String, PlaylistInsight> analyze(UUID id) {
        Playlist playlist = get(id);
        return analyzers.stream().map(analyzer -> analyzer.analyze(playlist))
                .collect(Collectors.toMap(PlaylistInsight::name, Function.identity()));
    }

    @Override
    public ExportedPlaylist export(UUID id, String format) {
        PlaylistExporter exporter = exporterFor(format);
        Playlist playlist = get(id);
        String fileName = playlist.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-")
                + "." + exporter.fileExtension();
        return new ExportedPlaylist(exporter.export(playlist), exporter.contentType(), fileName);
    }

    private PlaylistExporter exporterFor(String format) {
        PlaylistExporter exporter = exporters.get(format.toLowerCase());
        if (exporter == null) {
            throw new BusinessException("Unsupported export format '%s'. Available: %s"
                    .formatted(format, String.join(", ", exporters.keySet())));
        }
        return exporter;
    }

    @Override
    public List<Song> listSongs() {
        return catalog.listAll();
    }

    private List<Song> findSongs(List<String> ids) {
        Map<String, Song> foundSongsById = catalog.findByIds(new LinkedHashSet<>(ids)).stream()
                .collect(Collectors.toMap(Song::id, Function.identity()));
        Set<String> missing = ids.stream().filter(id -> !foundSongsById.containsKey(id))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (!missing.isEmpty()) {
            throw new NotFoundException("Songs not found: " + String.join(", ", missing));
        }
        return ids.stream().map(foundSongsById::get).toList();
    }

    private Playlist save(Playlist playlist) {
        playlist.markUpdated(clock.instant());
        return playlists.save(playlist);
    }
}
