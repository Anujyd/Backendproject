package com.musicapp.musicbackend.repository;

import com.musicapp.musicbackend.model.Song;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface SongRepository extends ReactiveMongoRepository<Song, UUID> {

    Mono<Boolean> existsByTrackNumber(int trackNumber);
    Flux<Song> findByTrackNumber(int trackNumber);

    Flux<Song> findByTitlename(String titlename);
}



//    @PutMapping("/{id}/titlename")
//    public Mono<ResponseEntity<SongDto>> updateSongTitlename(@PathVariable UUID id, @RequestParam String titlename) {
//        return songService.updateSongTitlename(id, titlename)
//                .map(updatedSong -> new ResponseEntity<>(updatedSong, HttpStatus.OK))
//                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }

//        public Mono<SongDto> updateSongTitlename(UUID id, String titlename) {
//        return songRepository.findById(id)
//                .flatMap(existingSong -> {
//                    existingSong.setTitlename(titlename);
//                    return songRepository.save(existingSong).map(this::mapEntityToDto);
//                });
//    }