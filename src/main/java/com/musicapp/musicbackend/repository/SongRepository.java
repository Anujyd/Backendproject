package com.musicapp.musicbackend.repository;

import com.musicapp.musicbackend.model.Song;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;


public interface SongRepository extends MongoRepository<Song, UUID> {
    boolean existsByTrackNumber(int trackNumber);

    List<Song> findByTrackNumber(int trackNumber);


    List<Song> findByfilename(String keyword);

}

