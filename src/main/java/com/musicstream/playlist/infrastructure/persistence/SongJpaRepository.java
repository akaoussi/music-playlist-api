package com.musicstream.playlist.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface SongJpaRepository extends JpaRepository<SongEntity, String> {
}
