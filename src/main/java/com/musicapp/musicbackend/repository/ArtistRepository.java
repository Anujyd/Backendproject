package com.musicapp.musicbackend.repository;

import com.musicapp.musicbackend.model.Artist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

public interface ArtistRepository extends ReactiveMongoRepository<Artist, UUID> {
    Flux<Artist> findByArtistName(String artistName);
}
