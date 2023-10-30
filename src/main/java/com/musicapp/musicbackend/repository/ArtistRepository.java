package com.musicapp.musicbackend.repository;

import com.musicapp.musicbackend.model.Artist;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface ArtistRepository extends MongoRepository<Artist, UUID> {
    List<Artist> findByArtistName(String artistName);
}
