package com.musicapp.musicbackend.Service;

import com.musicapp.musicbackend.model.Artist;
import com.musicapp.musicbackend.model.ArtistDto;
import com.musicapp.musicbackend.model.Song;
import com.musicapp.musicbackend.model.SongDto;
import com.musicapp.musicbackend.repository.ArtistRepository;
import com.musicapp.musicbackend.repository.SongRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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

    @Cacheable(value = "songsByTitlename", key = "#titlename")
public Flux<Song> getSongByTitlename(String titlename) {
    System.out.println("calling from db");
    return songRepository.findByTitlename(titlename);
}
    public Mono<SongDto> createSong(@Valid SongDto songDto) {
        try {
            if (isTrackNumberExists(songDto.getTrackNumber())) {
                return Mono.error(new IllegalArgumentException("Track number already exists."));
            }

            Song song = mapDtoToEntity(songDto);
            Flux<Artist> artistFlux = Flux.fromIterable(songDto.getArtists())
                    .flatMap(artistDto -> {
                        if (artistDto.getId() != null && !artistDto.getId().isEmpty()) {
                            return artistRepository.findById(UUID.fromString(artistDto.getId()))
                                    .switchIfEmpty(createNewArtist(artistDto));
                        } else {
                            return createNewArtist(artistDto);
                        }
                    });

            return artistFlux.collectList()
                    .flatMap(artists -> {
                        song.setArtists(artists);
                        return songRepository.save(song)
                                .map(this::mapEntityToDto);
                    });
        }catch (Exception e){
            return Mono.error(e);
        }
    }
    private Mono<Artist> createNewArtist(ArtistDto artistDto) {
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

    @Cacheable(value = "songsById", key = "#id")
    public Mono<Song> getSongById(UUID id) {
        System.out.println("called from db");
        return songRepository.findById(id);
    }


    public Flux<SongDto> searchSongs(SongDto searchCriteria) {
        Criteria criteria = new Criteria();

        if (searchCriteria.getTitlename() != null && !searchCriteria.getTitlename().isEmpty()) {
            criteria = criteria.and("filename").is(searchCriteria.getTitlename());
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

    @CacheEvict(value = "songsByTitlename", key = "#titlename")
    public Flux<SongDto> updateSongByTitlename(String titlename, SongDto updatedDTO) {
        return songRepository.findByTitlename(titlename)
                .flatMap(existingSong -> {

                    existingSong.setTitlename(updatedDTO.getTitlename());
                    existingSong.setFavorite(updatedDTO.isFavorite());
                    existingSong.setTrackNumber(updatedDTO.getTrackNumber());
                    existingSong.setDuration(updatedDTO.getDuration());

                    return songRepository.save(existingSong)
                            .map(this::mapEntityToDto);
                });
    }
    @CacheEvict(value = "songsById", key = "#id")
    public Mono<SongDto> updateSong(UUID id, SongDto updatedDTO) {
        return songRepository.findById(id)
                .flatMap(existingSong -> {
                    existingSong.setTitlename(updatedDTO.getTitlename());
                    existingSong.setFavorite(updatedDTO.isFavorite());
                    existingSong.setTrackNumber(updatedDTO.getTrackNumber());
                    existingSong.setDuration(updatedDTO.getDuration());
                    return songRepository.save(existingSong)
                            .map(this::mapEntityToDto);
                });
    }

    public Mono<Void> deleteSong(UUID id) {
        return songRepository.deleteById(id);
    }
    private boolean isTrackNumberExists(int trackNumber) {
        return false;
    }
    public SongDto mapEntityToDto(Song song) {
        SongDto songDTO = new SongDto();
        songDTO.setId(song.getId());
        songDTO.setTitlename(song.getTitlename());
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
        song.setTitlename(songDto.getTitlename());
        song.setFavorite(songDto.isFavorite());
        song.setTrackNumber(songDto.getTrackNumber());
        song.setDuration(songDto.getDuration());
        return song;
    }
}
