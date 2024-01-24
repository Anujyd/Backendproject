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
    public ResponseEntity<Album> createAlbum(@RequestBody AlbumDTO albumDTO) {
        if (albumDTO == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Album createdAlbum = albumService.createAlbum(albumDTO);
        return ResponseEntity.ok(createdAlbum);
    }

    @GetMapping("/")
    public ResponseEntity<Page<Album>> getAllAlbums(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Album> albums = albumService.getAllAlbums(pageable);
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbumById(@PathVariable UUID id) {
        if (id == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Album album = albumService.getAlbumById(id);
        if (album != null) {
            return ResponseEntity.ok(album);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Album> updateAlbum(@PathVariable UUID id, @RequestBody AlbumDTO updatedDTO) {
        if (id == null || updatedDTO == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Album updatedAlbum = albumService.updateAlbum(id, updatedDTO);
        if (updatedAlbum != null) {
            return ResponseEntity.ok(updatedAlbum);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Album>> searchAlbumsByName(@RequestParam String name) {
        List<Album> albums = albumService.searchAlbumsByName(name);
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/albumName/{name}")
    public List<Album> getAlbumByAlbumName(@PathVariable String name) {

        return albumService.getAlbumByAlbumName(name);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable UUID id) {
        if (id == null) {
            return ResponseEntity.badRequest().build();
        }
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }
}
