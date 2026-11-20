package com.musicstream.playlist.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface PlaylistJpaRepository extends JpaRepository<PlaylistEntity, UUID> {
    List<PlaylistEntity> findAllByOrderByUpdatedAtDesc();
}
