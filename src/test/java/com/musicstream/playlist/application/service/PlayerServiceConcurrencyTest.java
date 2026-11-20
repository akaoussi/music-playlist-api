package com.musicstream.playlist.application.service;

import com.musicstream.playlist.application.port.out.PlaylistRepository;
import com.musicstream.playlist.domain.model.Playlist;
import com.musicstream.playlist.domain.player.PlaybackSession;
import com.musicstream.playlist.domain.player.PlayerStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.musicstream.playlist.fixtures.TestSongs.song;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerServiceConcurrencyTest {
    @Test
    void readers_never_see_a_half_updated_player_while_many_threads_switch_playlists() throws InterruptedException {
        // 8 playlists of different sizes: a consistent snapshot has the queue size of ITS playlist
        Map<UUID, Playlist> playlistsById = IntStream.rangeClosed(1, 8)
                .mapToObj(size -> Playlist.restore(UUID.randomUUID(), "P" + size, null,
                        IntStream.range(0, size).mapToObj(index -> song(size + "-" + index)).toList(), Instant.EPOCH))
                .collect(Collectors.toMap(Playlist::getId, Function.identity()));
        PlaylistRepository repository = mock(PlaylistRepository.class);
        when(repository.findById(any()))
                .thenAnswer(invocation -> Optional.of(playlistsById.get(invocation.getArgument(0, UUID.class))));
        PlayerService player = new PlayerService(repository);
        List<UUID> ids = List.copyOf(playlistsById.keySet());

        ConcurrentLinkedQueue<String> inconsistencies = new ConcurrentLinkedQueue<>();
        CountDownLatch start = new CountDownLatch(1);
        try (ExecutorService executor = Executors.newFixedThreadPool(16)) {
            for (int threadNumber = 0; threadNumber < 16; threadNumber++) {
                int workerNumber = threadNumber;
                executor.submit(() -> {
                    start.await();
                    for (int attempt = 0; attempt < 500; attempt++) {
                        if (workerNumber % 2 == 0) {
                            player.play(ids.get((workerNumber + attempt) % ids.size()), null);
                        } else {
                            PlaybackSession seen = player.state();
                            if (seen.status() == PlayerStatus.PLAYING
                                    && seen.queue().size() != playlistsById.get(seen.playlistId()).getSongs().size()) {
                                inconsistencies.add(seen.toString());
                            }
                        }
                    }
                    return null;
                });
            }
            start.countDown();
            executor.shutdown();
            assertThat(executor.awaitTermination(10, TimeUnit.SECONDS)).isTrue();
        }

        assertThat(inconsistencies).isEmpty();
        assertThat(player.state().status()).isEqualTo(PlayerStatus.PLAYING);
    }
}
