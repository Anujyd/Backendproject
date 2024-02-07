package com.musicapp.musicbackend.Controller;

import com.musicapp.musicbackend.Service.AlbumService;
import com.musicapp.musicbackend.model.Album;
import com.musicapp.musicbackend.model.AlbumDTO;
import com.musicapp.musicbackend.repository.AlbumRepository;
import com.musicapp.musicbackend.repository.ArtistRepository;
import com.musicapp.musicbackend.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {
    @Autowired
    private AlbumService albumService;
    @Autowired
    private AlbumRepository albumRepository;

@PostMapping("/")
public Mono<ResponseEntity<Album>> createAlbum(@RequestBody AlbumDTO albumDTO) {
    return albumService.createAlbum(albumDTO)
            .map(album -> ResponseEntity.ok(album))
            .defaultIfEmpty(ResponseEntity.notFound().build());
}

    @GetMapping("/")
    public Flux<Album> getAllAlbums(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return albumService.getAllAlbums(PageRequest.of(page, size));
    }

@GetMapping("/{id}")
public Mono<ResponseEntity<Album>> getAlbumById(@PathVariable UUID id) {
    return albumService.getAlbumById(id)
            .map(album -> ResponseEntity.ok(album))
            .defaultIfEmpty(ResponseEntity.notFound().build());
}

    @PutMapping("/{name}")
    public Flux<ResponseEntity<Album>> updateAlbum(@PathVariable String name, @RequestBody AlbumDTO updatedDTO) {
        return albumService.updateAlbum(name, updatedDTO)
                .map(album -> ResponseEntity.ok(album))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/searchAlbumsByName")
    public Flux<Album> searchAlbumsByName(@RequestParam String name) {
        return albumService.searchAlbumsByName(name);
    }

    @GetMapping("/searchAlbumsByLabel")
    public  Flux<Album> searchAlbumsByLabel(@RequestParam String label){return albumService.searchAlbumsByLabel(label);}
    @GetMapping("/search")
    public Flux<Album> searchAlbums(@ModelAttribute AlbumDTO searchCriteria) {
        return albumService.searchAlbums(searchCriteria);
    }


    @GetMapping("/albumName/{name}")
    public Flux<Album> getAlbumByAlbumName(@PathVariable String name) {
        return albumService.getAlbumByAlbumName(name);
    }


    @DeleteMapping("/{id}")
    public Mono<Void> deleteAlbum(@PathVariable UUID id) {
        return albumService.deleteAlbum(id);
    }
}
