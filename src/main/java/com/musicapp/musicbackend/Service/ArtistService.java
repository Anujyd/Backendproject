package com.musicapp.musicbackend.Service;


import com.musicapp.musicbackend.model.Artist;
import com.musicapp.musicbackend.model.ArtistDto;
import com.musicapp.musicbackend.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public class ArtistService {
    @Autowired
    private ArtistRepository artistRepository;


    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    public ResponseEntity<Artist> getArtistById(UUID id) {
        Artist artist = artistRepository.findById(id).orElse(null);
        if (artist != null) {
            return ResponseEntity.ok(artist);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Artist> createArtist(ArtistDto artistDto) {
        Artist artist = new Artist();
        artist.setArtistName(artistDto.getArtistName());
        artist.setCountry(artistDto.getCountry());
        artist = artistRepository.save(artist);
        return ResponseEntity.status(HttpStatus.CREATED).body(artist);
    }

    public ResponseEntity<Artist> updateArtist(UUID id, ArtistDto updatedDto) {
        Artist existingArtist = artistRepository.findById(id).orElse(null);
        if (existingArtist != null) {
            existingArtist.setArtistName(updatedDto.getArtistName());
            existingArtist.setCountry(updatedDto.getCountry());
            artistRepository.save(existingArtist);
            return ResponseEntity.ok(existingArtist);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public List<Artist> searchArtistsByName(String artistName) {
        return artistRepository.findByArtistName(artistName);
    }
    public ResponseEntity<Void> deleteArtist(UUID id) {
        if (artistRepository.existsById(id)) {
            artistRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
