package com.musicapp.musicbackend.Service;


import com.musicapp.musicbackend.model.Artist;
import com.musicapp.musicbackend.model.ArtistDto;
import com.musicapp.musicbackend.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
public class ArtistService {
    @Autowired
    private ArtistRepository artistRepository;


public Mono<ResponseEntity<Artist>> createArtist(ArtistDto artistDto) {
    Artist artist = new Artist();
    artist.setArtistName(artistDto.getArtistName());
       artist.setCountry(artistDto.getCountry());
        artist.setRole(artistDto.getRole());
        artist.setImageUrl(artistDto.getImageUrl());

    return artistRepository.save(artist)
            .map(savedArtist -> new ResponseEntity<>(savedArtist, HttpStatus.CREATED));
}

public Flux<ResponseEntity<Artist>> getAllArtists() {
    return artistRepository.findAll()
            .map(artist -> new ResponseEntity<>(artist, HttpStatus.OK));
}

//    @Cacheable(value = "artistsById", key = "#id")
public Mono<ResponseEntity<Artist>> getArtistById(UUID id) {
    return artistRepository.findById(id)
            .map(artist -> new ResponseEntity<>(artist, HttpStatus.OK))
            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
}


public Flux<ResponseEntity<Artist>> getArtistByArtistName(String artistName) {
    return artistRepository.findByArtistName(artistName)
            .map(artist -> new ResponseEntity<>(artist, HttpStatus.OK));
}


public Mono<ResponseEntity<Artist>> updateArtist(UUID id, ArtistDto updatedDto) {
    return artistRepository.findById(id)
            .flatMap(existingArtist -> {
                existingArtist.setArtistName(updatedDto.getArtistName());
                existingArtist.setCountry(updatedDto.getCountry());
                existingArtist.setRole(updatedDto.getRole());
                existingArtist.setImageUrl(updatedDto.getImageUrl());

                return artistRepository.save(existingArtist)
                        .map(updatedArtist -> new ResponseEntity<>(updatedArtist, HttpStatus.OK));
            })
            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
}

public Flux<ResponseEntity<Artist>> searchArtistsByName(String artistName) {
    return artistRepository.findByArtistName(artistName)
            .map(artist -> new ResponseEntity<>(artist, HttpStatus.OK));
}


public Mono<ResponseEntity<Void>> deleteArtist(UUID id) {
    return artistRepository.existsById(id)
            .flatMap(exists -> {
                if (exists) {
                    return artistRepository.deleteById(id)
                            .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
                } else {
                    return Mono.just(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
                }
            });
    }
}
