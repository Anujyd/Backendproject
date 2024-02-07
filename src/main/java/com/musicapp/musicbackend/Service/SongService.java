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
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SongService {
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Cacheable(value = "songsByTrackNumber", key = "#trackNumber")
    public Flux<Song> getSongsByTrackNumber(int trackNumber) {
        System.out.println("called from db");
        return songRepository.findByTrackNumber(trackNumber);
    }

    public Mono<SongDto> createSong(@Valid SongDto songDto) {
        if (isTrackNumberExists(songDto.getTrackNumber())) {
            return Mono.error(new IllegalArgumentException("Track number already exists."));
        }

        Song song = mapDtoToEntity(songDto);

        List<Mono<Artist>> artistMonos = songDto.getArtists().stream()
                .map(this::getOrCreateArtist)
                .collect(Collectors.toList());

        return Flux.concat(artistMonos)
                .collectList()
                .flatMap(artists -> {
                    song.setArtists(artists);
                    return songRepository.save(song).map(this::mapEntityToDto);
                });
    }

    private Mono<Artist> getOrCreateArtist(ArtistDto artistDto) {
        if (artistDto.getId() != null) {
            return artistRepository.findById(UUID.fromString(artistDto.getId()))
                    .switchIfEmpty(Mono.error(new EntityNotFoundException("Artist with ID " + artistDto.getId() + " not found.")));
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

    public Mono<SongDto> getSongById(UUID id) {
        return songRepository.findById(id)
                .map(this::mapEntityToDto);
    }

    public Flux<SongDto> getSongByFilename(String filename) {
        return songRepository.findByFilename(filename)
                .map(this::mapEntityToDto)
                .switchIfEmpty(Flux.empty());
    }

    public Flux<SongDto> searchSongs(SongDto searchCriteria) {
        Criteria criteria = new Criteria();

        if (searchCriteria.getFilename() != null && !searchCriteria.getFilename().isEmpty()) {
            criteria = criteria.and("filename").is(searchCriteria.getFilename());
        }

        if (searchCriteria.getArtists() != null && !searchCriteria.getArtists().isEmpty()) {
            for (ArtistDto artistDto : searchCriteria.getArtists()) {
                criteria = criteria.and("artists.artistName").is(artistDto.getArtistName());
            }
        }

        if (searchCriteria.getTrackNumber() > 0) {
            criteria = criteria.and("trackNumber").is(searchCriteria.getTrackNumber());
        }

        if (searchCriteria.getDuration() > 0) {
            criteria = criteria.and("duration").is(searchCriteria.getDuration());
        }

        Query query = new Query(criteria);
        Flux<Song> songs = reactiveMongoTemplate.find(query, Song.class);
        return songs.map(this::mapEntityToDto);
    }

    @CacheEvict(value = "songsById", key = "#id")
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

    private boolean isTrackNumberExists(int trackNumber) {
        return false;
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
