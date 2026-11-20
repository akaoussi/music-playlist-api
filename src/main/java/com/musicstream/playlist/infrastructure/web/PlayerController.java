package com.musicstream.playlist.infrastructure.web;

import com.musicstream.playlist.application.port.in.PlayerUseCase;
import com.musicstream.playlist.domain.shuffle.ShuffleMode;
import com.musicstream.playlist.infrastructure.web.Responses.PlayerView;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/player")
class PlayerController {
    private final PlayerUseCase player;

    PlayerController(PlayerUseCase player) {
        this.player = player;
    }

    @GetMapping
    PlayerView state() {
        return PlayerView.from(player.state());
    }

    @PostMapping("/play")
    PlayerView play(@Valid @RequestBody Requests.Play request) {
        ShuffleMode mode = request.shuffle() == null || request.shuffle().isBlank()
                ? null : ShuffleMode.fromApiName(request.shuffle());
        return PlayerView.from(player.play(request.playlistId(), mode));
    }

    @PostMapping("/stop")
    PlayerView stop() {
        return PlayerView.from(player.stop());
    }
}
