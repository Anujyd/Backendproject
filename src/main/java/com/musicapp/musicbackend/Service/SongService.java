package com.musicapp.musicbackend.Service;

import com.musicapp.musicbackend.model.Artist;
import com.musicapp.musicbackend.model.ArtistDto;
import com.musicapp.musicbackend.model.Song;
import com.musicapp.musicbackend.model.SongDto;
import com.musicapp.musicbackend.repository.ArtistRepository;
import com.musicapp.musicbackend.repository.SongRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SongService {
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private ArtistRepository artistRepository;

    @Cacheable(value = "songsByTrackNumber", key = "#trackNumber")
    public Flux<Song> getSongsByTrackNumber(int trackNumber) {
        System.out.println("called from db");
        return songRepository.findByTrackNumber(trackNumber);
    }


//public SongDto createSong(@Valid SongDto songDto) {
//    if (isTrackNumberExists(songDto.getTrackNumber())) {
//        throw new IllegalArgumentException("Track number already exists.");
//    }
//
//    Song song = mapDtoToEntity(songDto);
//    List<Artist> artists = songDto.getArtists().stream()
//            .map(artistDto -> getOrCreateArtist(artistDto))
//            .collect(Collectors.toList());
//
//    song.setArtists(artists);
//    song = songRepository.save(song);
//    return mapEntityToDto(song);
//}
public Mono<SongDto> createSong(@Valid SongDto songDto) {
    if (isTrackNumberExists(songDto.getTrackNumber())) {
        return Mono.error(new IllegalArgumentException("Track number already exists."));
    }

    Song song = mapDtoToEntity(songDto);
    List<Artist> artists = songDto.getArtists().stream()
            .map(artistDto -> getOrCreateArtist(artistDto))
            .collect(Collectors.toList());
    song.setArtists(artists);

    return songRepository.save(song)
            .map(this::mapEntityToDto);
}
private Artist getOrCreateArtist(ArtistDto artistDto) {
    if (artistDto.getId() != null) {
        Optional<Artist> existingArtist = artistRepository.findById(UUID.fromString(artistDto.getId()));
        if (existingArtist.isPresent()) {
            return existingArtist.get();
        } else {
            throw new EntityNotFoundException("Artist with ID " + artistDto.getId() + " not found.");
        }
    }

    
    Artist newArtist = new Artist();
    newArtist.setArtistName(artistDto.getArtistName());
    newArtist.setCountry(artistDto.getCountry());
    newArtist.setRole(artistDto.getRole());
    newArtist.setImageUrl(artistDto.getImageUrl());
    return artistRepository.save(newArtist);
}

    public Mono<List<SongDto>> getAllSongs(int pageNumber, int pageSize) {
        return songRepository.findAll()
                .skip(pageNumber * pageSize)
                .take(pageSize)
                .collectList()
                .map(this::mapEntitiesToDto);
    }
//    public List<SongDto> getAllSongs(int pageNumber, int pageSize) {
//
//
//        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//
//        Page<Song> posts = songRepository.findAll(pageable);
//
//        List<Song> song = posts.getContent();
//
//        List<SongDto> content = song.stream().map(this::mapEntityToDto).toList();
//        return content;
//    }

    public Mono<SongDto> getSongById(UUID id) {
        return songRepository.findById(id)
                .map(this::mapEntityToDto);
    }
//    public SongDto getSongById(UUID id) {
//        Optional<Song> optionalSong = songRepository.findById(id);
//        return optionalSong.map(this::mapEntityToDto).orElse(null);
//    }
//    public SongDto getSongByFilename(String filename) {
//        Optional<Song> optionalSong = songRepository.findByFilename(filename);
//        return optionalSong.map(this::mapEntityToDto).orElse(null);
//    }

    public Mono<SongDto> getSongByFilename(String filename) {
        return songRepository.findByFilename(filename)
                .map(this::mapEntityToDto)
                .switchIfEmpty(Mono.empty()); // Return an empty Mono if not found
    }
    @CacheEvict(value = "songsById", key = "#id")
//    public SongDto updateSong(UUID id, @Valid SongDto updatedDto) {
//        Song existingSong = songRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Song not found."));
//
//
//        if (existingSong.getTrackNumber() != updatedDto.getTrackNumber() && isTrackNumberExists(updatedDto.getTrackNumber())) {
//            throw new IllegalArgumentException("Track number already exists.");
//        }
//
//        Song updatedSong = mapDtoToEntity(updatedDto);
//        updatedSong = songRepository.save(updatedSong);
//
//        return mapEntityToDto(updatedSong);
//    }

    public Mono<SongDto> updateSong(UUID id, SongDto updatedDTO) {
        return songRepository.findById(id)
                .flatMap(existingSong -> {
                    if (existingSong.getTrackNumber() != updatedDTO.getTrackNumber() && isTrackNumberExists(updatedDTO.getTrackNumber())) {
                        return Mono.error(new IllegalArgumentException("Track number already exists."));
                    } else {
                        Song updatedSong = mapDtoToEntity(updatedDTO);
                        updatedSong.setId(id.toString());
                        return songRepository.save(updatedSong)
                                .map(this::mapEntityToDto);
                    }
                });
    }

    public Mono<Void> deleteSong(UUID id) {
        return songRepository.deleteById(id);
    }
//public Mono<Void> deleteSong(UUID id) {
//    return songRepository.deleteById(id);
//}

    private boolean isTrackNumberExists(int trackNumber) {
        return false;
        //  return songRepository.existsByTrackNumber(trackNumber);
    }

    private SongDto mapEntityToDto(Song song) {
        SongDto songDTO = new SongDto();
        songDTO.setId(song.getId());
        songDTO.setFilename(song.getFilename());
        songDTO.setFavorite(song.isFavorite());
        songDTO.setTrackNumber(song.getTrackNumber());
        songDTO.setDuration(song.getDuration());
        if (song.getArtists() != null) {
            songDTO.setArtists(song.getArtists().stream().map(ArtistDto::from).collect(Collectors.toList()));
        }
        return songDTO;
    }

    private List<SongDto> mapEntitiesToDto(List<Song> songs) {
        return songs.stream().map(this::mapEntityToDto).collect(Collectors.toList());
    }

    private Song mapDtoToEntity(SongDto songDto) {
        Song song = new Song();
        song.setId(songDto.getId());
        song.setFilename(songDto.getFilename());
        song.setFavorite(songDto.isFavorite());
        song.setTrackNumber(songDto.getTrackNumber());
        song.setDuration(songDto.getDuration());
        return song;
    }
}
