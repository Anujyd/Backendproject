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

//    public ResponseEntity<Artist> createArtist(ArtistDto artistDto)  {
//
//        Artist artist = new Artist();
//        artist.setArtistName(artistDto.getArtistName());
//        artist.setCountry(artistDto.getCountry());
//        artist.setRole(artistDto.getRole());
//        artist.setImageUrl(artistDto.getImageUrl());
//        artist = artistRepository.save(artist);
//        return ResponseEntity.status(HttpStatus.CREATED).body(artist);
//    }
public Mono<ResponseEntity<Artist>> createArtist(ArtistDto artistDto) {
    Artist artist = new Artist();
    artist.setArtistName(artistDto.getArtistName());
       artist.setCountry(artistDto.getCountry());
        artist.setRole(artistDto.getRole());
        artist.setImageUrl(artistDto.getImageUrl());

    return artistRepository.save(artist)
            .map(savedArtist -> new ResponseEntity<>(savedArtist, HttpStatus.CREATED));
}

//    public Page<Artist> getAllArtists(Pageable pageable)
//    {
//        return artistRepository.findAll(pageable);
//    }
public Flux<ResponseEntity<Artist>> getAllArtists() {
    return artistRepository.findAll()
            .map(artist -> new ResponseEntity<>(artist, HttpStatus.OK));
}


//    public ResponseEntity<Artist> getArtistById(UUID id) {
//        Artist artist = artistRepository.findById(id).orElse(null);
//        if (artist != null) {
//            return ResponseEntity.ok(artist);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
public Mono<ResponseEntity<Artist>> getArtistById(UUID id) {
    return artistRepository.findById(id)
            .map(artist -> new ResponseEntity<>(artist, HttpStatus.OK))
            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
}

//    @Cacheable(value = "artistsByArtistName", key = "#artistName")
//    public List<Artist> getArtistByArtistName(String artistName){
//        System.out.println("call from db");
//        return artistRepository.findByArtistName(artistName);
//    }
public Flux<ResponseEntity<Artist>> getArtistByArtistName(String artistName) {
    return artistRepository.findByArtistName(artistName)
            .map(artist -> new ResponseEntity<>(artist, HttpStatus.OK));
}

//    public ResponseEntity<Artist> updateArtist(UUID id, ArtistDto updatedDto) {
//        Artist existingArtist = artistRepository.findById(id).orElse(null);
//        if (existingArtist != null) {
//            existingArtist.setArtistName(updatedDto.getArtistName());
//            existingArtist.setCountry(updatedDto.getCountry());
//            existingArtist.setRole(updatedDto.getRole());
//            existingArtist.setImageUrl(updatedDto.getImageUrl());
//            artistRepository.save(existingArtist);
//            return ResponseEntity.ok(existingArtist);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
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

//    public List<Artist> searchArtistsByName(String artistName) {
//
//        return artistRepository.findByArtistName(artistName);
//    }
public Flux<ResponseEntity<Artist>> searchArtistsByName(String artistName) {
    return artistRepository.findByArtistName(artistName)
            .map(artist -> new ResponseEntity<>(artist, HttpStatus.OK));
}


//    public ResponseEntity<Void> deleteArtist(UUID id) {
//        if (artistRepository.existsById(id)) {
//            artistRepository.deleteById(id);
//            return ResponseEntity.noContent().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
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
