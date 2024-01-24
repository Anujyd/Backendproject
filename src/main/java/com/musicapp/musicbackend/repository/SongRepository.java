package com.musicapp.musicbackend.repository;

import com.musicapp.musicbackend.model.Song;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;


public interface SongRepository extends ReactiveMongoRepository<Song, UUID> {

    Mono<Boolean> existsByTrackNumber(int trackNumber);

    Flux<Song> findByTrackNumber(int trackNumber);

    List<Song> findByfilename(String keyword);

    Mono<Song> findByFilename(String filename);

}

