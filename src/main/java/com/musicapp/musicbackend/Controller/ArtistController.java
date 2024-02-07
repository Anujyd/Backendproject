package com.musicapp.musicbackend.Controller;

import com.musicapp.musicbackend.Service.ArtistService;
import com.musicapp.musicbackend.model.Artist;
import com.musicapp.musicbackend.model.ArtistDto;
import com.musicapp.musicbackend.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {
    @Autowired
    private ArtistService artistService;
    @Autowired
    private ArtistRepository artistRepository;


@PostMapping("/")
public Mono<ResponseEntity<Artist>> createArtist(@RequestBody ArtistDto artistDto) {
    return artistService.createArtist(artistDto);
}

@GetMapping("/")
public Flux<ResponseEntity<Artist>> getAllArtists(Pageable pageable) {
    return artistService.getAllArtists();
}


@GetMapping("/{id}")
public Mono<ResponseEntity<Artist>> getArtistById(@PathVariable UUID id) {
    return artistService.getArtistById(id);
}


@GetMapping("/artistName/{artistName}")
public Flux<ResponseEntity<Artist>> getArtistByArtistName(@PathVariable String artistName) {
    return artistService.getArtistByArtistName(artistName);
}


@PutMapping("/{id}")
public Mono<ResponseEntity<Artist>> updateArtist(@PathVariable UUID id, @RequestBody ArtistDto updatedDto) {
    return artistService.updateArtist(id, updatedDto);
}

@GetMapping("/search")
public Flux<ResponseEntity<Artist>> searchArtistsByName(@RequestParam String artistName) {
    return artistService.searchArtistsByName(artistName);
}


@DeleteMapping("/{id}")
public Mono<ResponseEntity<Void>> deleteArtist(@PathVariable UUID id) {
    return artistService.deleteArtist(id);
}
}
