package com.musicapp.musicbackend.Controller;

import com.musicapp.musicbackend.Service.SongService;
import com.musicapp.musicbackend.model.Song;
import com.musicapp.musicbackend.model.SongDto;
import com.musicapp.musicbackend.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/songs")
public class SongController {
    @Autowired
    private SongService songService;

    @Autowired
    private SongRepository songRepository;

//    public ResponseEntity<SongDto> createSong(@RequestBody SongDto songDto) {
//        SongDto createdSong = songService.createSong(songDto);
//        return new ResponseEntity<>(createdSong, HttpStatus.CREATED);
//    }
//    public Mono<ResponseEntity<Mono<SongDto>>> createSong(@RequestBody SongDto songDto) {
//        return Mono.just(songDto)
//                .map(songService::createSong)
//                .map(createdSong -> new ResponseEntity<>(createdSong, HttpStatus.CREATED));
//    }
    @PostMapping("/")
    public Mono<ResponseEntity<SongDto>> createSong(@RequestBody SongDto songDto) {
        return Mono.just(songDto)
                .flatMap(songService::createSong)
                .map(createdSong -> new ResponseEntity<>(createdSong, HttpStatus.CREATED));
    }
//    @GetMapping("/filename/{filename}")
//    public ResponseEntity<SongDto> getSongByFilename(@PathVariable String filename) {
//        SongDto song = songService.getSongByFilename(filename);
//        if (song != null) {
//            return new ResponseEntity<>(song, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
@GetMapping("/filename/{filename}")
public Mono<ResponseEntity<SongDto>> getSongByFilename(@PathVariable String filename) {
    return songService.getSongByFilename(filename)
            .map(songDto -> new ResponseEntity<>(songDto, HttpStatus.OK))
            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
}
//    @GetMapping("/{id}")
//    public ResponseEntity<SongDto> getSongById(@PathVariable UUID id) {
//
//        try {
//            SongDto song = songService.getSongById(id);
//            if (song != null) {
//                return new ResponseEntity<>(song, HttpStatus.OK);
//            } else {
//                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//            }
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
@GetMapping("/{id}")
public Mono<ResponseEntity<SongDto>> getSongById(@PathVariable UUID id) {
    return songService.getSongById(id)
            .map(song -> new ResponseEntity<>(song, HttpStatus.OK))
            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
}

    @GetMapping("/track/{trackNumber}")
    public Flux<Song> getSongsByTrackNumber(@PathVariable int trackNumber) {
        return songService.getSongsByTrackNumber(trackNumber);
    }


    @GetMapping("/songs")
    public Mono<ResponseEntity<List<SongDto>>> getAllSongs(@RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
                                                           @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        return songService.getAllSongs(pageNumber, pageSize)
                .map(songs -> new ResponseEntity<>(songs, HttpStatus.OK));
    }
//    @GetMapping("/songs")
//    public List<SongDto> getAllSongs(@RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber, @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
//
//        return songService.getAllSongs(pageNumber, pageSize);
//    }

//    @GetMapping("/{id}")
//    public ResponseEntity<SongDto> getSongById(@PathVariable String id) {
//        SongDto song = songService.getSongById(id);
//        if (song != null) {
//            return new ResponseEntity<>(song, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }


    @PutMapping("/{id}")
    public Mono<ResponseEntity<SongDto>> updateSong(@PathVariable UUID id, @RequestBody SongDto updatedDTO) {
        return songService.updateSong(id, updatedDTO)
                .map(updated -> new ResponseEntity<>(updated, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
//    @PutMapping("/{id}")
//    public ResponseEntity<SongDto> updateSong(@PathVariable UUID id, @RequestBody SongDto updatedDTO) {
//        SongDto updated = songService.updateSong(id, updatedDTO);
//        return new ResponseEntity<>(updated, HttpStatus.OK);
//    }


//    @DeleteMapping("/{id}")
//    public Mono<ResponseEntity<Void>> deleteSong(@PathVariable UUID id) {
//        return songService.deleteSong(id)
//                .then(Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT)))
//                .onErrorReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSong(@PathVariable UUID id) {
        return songService.deleteSong(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @GetMapping("/search")
    public List<SongDto> searchSongsByTitle(@RequestParam String keyword) {
        List<Song> songs = songRepository.findByfilename(keyword);
        return songs.stream().map(SongDto::from).collect(Collectors.toList());
    }

}
