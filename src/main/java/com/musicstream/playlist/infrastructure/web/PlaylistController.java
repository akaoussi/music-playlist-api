package com.musicstream.playlist.infrastructure.web;

import com.musicstream.playlist.application.port.in.PlaylistUseCase;
import com.musicstream.playlist.application.port.in.PlaylistUseCase.ExportedPlaylist;
import com.musicstream.playlist.domain.analysis.PlaylistInsight;
import com.musicstream.playlist.domain.shuffle.ShuffleMode;
import com.musicstream.playlist.infrastructure.web.Responses.PlaylistView;
import com.musicstream.playlist.infrastructure.web.Responses.RecommendationView;
import com.musicstream.playlist.infrastructure.web.Responses.SongView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
class PlaylistController {
    private final PlaylistUseCase playlists;

    PlaylistController(PlaylistUseCase playlists) {
        this.playlists = playlists;
    }

    @GetMapping("/api/songs")
    List<SongView> listSongs() {
        return playlists.listSongs().stream().map(SongView::from).toList();
    }

    @PostMapping("/api/playlists")
    ResponseEntity<PlaylistView> create(@Valid @RequestBody Requests.CreatePlaylist request) {
        var playlist = playlists.create(request.name(), request.description(),
                Objects.requireNonNullElse(request.songIds(), List.of()));
        return ResponseEntity.created(location(playlist.getId())).body(PlaylistView.from(playlist));
    }

    @GetMapping("/api/playlists")
    List<PlaylistView> list() {
        return playlists.list().stream().map(PlaylistView::from).toList();
    }

    @GetMapping("/api/playlists/{id}")
    PlaylistView get(@PathVariable UUID id) {
        return PlaylistView.from(playlists.get(id));
    }

    @PutMapping("/api/playlists/{id}")
    PlaylistView update(@PathVariable UUID id, @Valid @RequestBody Requests.UpdatePlaylist request) {
        return PlaylistView.from(playlists.update(id, request.name(), request.description()));
    }

    @DeleteMapping("/api/playlists/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        playlists.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/playlists/{id}/songs")
    PlaylistView addSong(@PathVariable UUID id, @Valid @RequestBody Requests.AddSong request) {
        return PlaylistView.from(playlists.addSong(id, request.songId()));
    }

    @DeleteMapping("/api/playlists/{id}/songs/{songId}")
    PlaylistView removeSong(@PathVariable UUID id, @PathVariable String songId) {
        return PlaylistView.from(playlists.removeSong(id, songId));
    }

    @PutMapping("/api/playlists/{id}/songs/{songId}/position")
    PlaylistView moveSong(@PathVariable UUID id, @PathVariable String songId,
                          @Valid @RequestBody Requests.MoveSong request) {
        return PlaylistView.from(playlists.moveSong(id, songId, request.position()));
    }

    @PostMapping("/api/playlists/{id}/shuffle")
    List<SongView> shuffle(@PathVariable UUID id, @RequestParam(defaultValue = "random") String strategy) {
        return playlists.shuffle(id, ShuffleMode.fromApiName(strategy)).stream().map(SongView::from).toList();
    }

    @GetMapping("/api/playlists/{id}/recommendations")
    List<RecommendationView> recommendations(@PathVariable UUID id,
                                             @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {
        return playlists.recommend(id, limit).stream().map(RecommendationView::from).toList();
    }

    @GetMapping("/api/playlists/{id}/analysis")
    Map<String, PlaylistInsight> analysis(@PathVariable UUID id) {
        return playlists.analyze(id);
    }

    @GetMapping("/api/playlists/{id}/export")
    ResponseEntity<String> export(@PathVariable UUID id, @RequestParam(defaultValue = "json") String format) {
        ExportedPlaylist exported = playlists.export(id, format);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(exported.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + exported.fileName() + "\"")
                .body(exported.content());
    }

    private static URI location(UUID id) {
        return URI.create("/api/playlists/" + id);
    }
}
