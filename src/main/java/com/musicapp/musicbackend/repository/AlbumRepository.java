package com.musicapp.musicbackend.repository;

import com.musicapp.musicbackend.model.Album;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface AlbumRepository extends ReactiveMongoRepository<Album, UUID> {
    Flux<Album> findByName(String name);
    Flux<Album> findByNameAndLabel(String name, String label);
    Flux<Album> findByLabel(String label);
}
