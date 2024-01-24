package com.musicapp.musicbackend.Controller;

import com.musicapp.musicbackend.Service.ArtistService;

import com.musicapp.musicbackend.model.Artist;
import com.musicapp.musicbackend.model.ArtistDto;
import com.musicapp.musicbackend.repository.ArtistRepository;
import com.musicapp.musicbackend.repository.SongRepository;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {
    @Autowired
    private ArtistService artistService;
    @Autowired
    private ArtistRepository artistRepository;
//    @Autowired
//    private SongRepository songRepository;

    @GetMapping("/")
        public ResponseEntity<Page<Artist>> getAllArtists(@RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Artist> artists = artistService.getAllArtists(pageable);
            return ResponseEntity.ok(artists);
        }
//        List<Artist> artists = artistService.getAllArtists();
//        return new ResponseEntity<>(artists, HttpStatus.OK);

    @GetMapping("/{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable UUID id) {
        Artist artist = artistService.getArtistById(id).getBody();
        if (artist != null) {
            return new ResponseEntity<>(artist, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/artistName/{artistName}")
    public List<Artist> getArtistByArtistName(@PathVariable String artistName){
        return artistService.getArtistByArtistName(artistName);
    }

    @PostMapping("/")
    public ResponseEntity<Artist> createArtist(@RequestBody ArtistDto artistDto) {
        Artist artist = artistService.createArtist(artistDto).getBody();
        return new ResponseEntity<>(artist, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Artist> updateArtist(@PathVariable UUID id, @RequestBody ArtistDto updatedDto) {
        Artist updatedArtist = artistService.updateArtist(id, updatedDto).getBody();
        if (updatedArtist != null) {
            return new ResponseEntity<>(updatedArtist, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/search")
    public ResponseEntity<List<Artist>> searchArtistsByName(@RequestParam String artistName) {
        List<Artist> artists = artistService.searchArtistsByName(artistName);
        return ResponseEntity.ok(artists);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable UUID id) {
        artistService.deleteArtist(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
