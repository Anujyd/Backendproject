package com.musicapp.musicbackend.repository;

import com.musicapp.musicbackend.model.Album;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface AlbumRepository extends MongoRepository<Album , UUID> {
    List<Album> findByName(String name);
}
