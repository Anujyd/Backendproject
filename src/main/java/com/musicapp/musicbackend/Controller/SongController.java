package com.musicapp.musicbackend.Controller;

import com.musicapp.musicbackend.Service.SongService;
import com.musicapp.musicbackend.model.Song;
import com.musicapp.musicbackend.model.SongDto;
import com.musicapp.musicbackend.repository.AlbumRepository;
import com.musicapp.musicbackend.repository.ArtistRepository;
import com.musicapp.musicbackend.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private ArtistRepository artistRepository;

    @PostMapping("/")
    public ResponseEntity<SongDto> createSong(@RequestBody SongDto songDto) {
        SongDto createdSong = songService.createSong(songDto);
        return new ResponseEntity<>(createdSong, HttpStatus.CREATED);
    }
    @GetMapping("/filename/{filename}")
    public ResponseEntity<SongDto> getSongByFilename(@PathVariable String filename) {
        SongDto song = songService.getSongByFilename(filename);
        if (song != null) {
            return new ResponseEntity<>(song, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<SongDto> getSongById(@PathVariable UUID id) {

        try {
//            String string = String.valueOf(id);
//            UUID uuid = UUID.fromString(string);
            SongDto song = songService.getSongById(id);
            if (song != null) {
                return new ResponseEntity<>(song, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/track/{trackNumber}")
    public List<Song> getSongsByTrackNumber(@PathVariable int trackNumber) {
        return songService.getSongsByTrackNumber(trackNumber);

    }



    @GetMapping("/songs")
    public List<SongDto> getAllSongs(@RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber, @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {

        return songService.getAllSongs(pageNumber, pageSize);
    }

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
    public ResponseEntity<SongDto> updateSong(@PathVariable UUID id, @RequestBody SongDto updatedDTO) {
        SongDto updated = songService.updateSong(id, updatedDTO);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable UUID id) {
        songService.deleteSong(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public List<SongDto> searchSongsByTitle(@RequestParam String keyword) {
        List<Song> songs = songRepository.findByfilename(keyword);

//        List<SongDto> response = new ArrayList<>();

//        for (Song song : songs) {
//           response.add(SongDto.from(song));
//        }

        return songs.stream().map(SongDto::from).collect(Collectors.toList());
    }

}
