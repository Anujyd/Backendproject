package com.musicapp.musicbackend.Service;

import com.musicapp.musicbackend.model.*;
import com.musicapp.musicbackend.repository.AlbumRepository;
import com.musicapp.musicbackend.repository.ArtistRepository;
import com.musicapp.musicbackend.repository.SongRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AlbumService {
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private SongService songService;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private SongRepository songRepository;
    
public Mono<Album> createAlbum(AlbumDTO albumDTO) {
    if (albumDTO == null) {
        return Mono.error(new IllegalArgumentException("AlbumDTO cannot be null."));
    }

    Album album = new Album();
    album.setName(albumDTO.getName());
    album.setLabel(albumDTO.getLabel());
    album.setLanguage(albumDTO.getLanguage());

    List<Flux<Song>> songMonos = albumDTO.getSongs().stream()
            .map(songDto -> createOrUpdateSong(songDto))
            .collect(Collectors.toList());

    Flux<Song> songFlux = Flux.merge(songMonos);

    return songFlux.collectList()
            .flatMap(songs -> {
                album.setSongs(songs);
                return albumRepository.save(album);
            })
            .map(savedAlbum -> mapDTOToEntity(savedAlbum, albumDTO));
}

    private Flux<Song> createOrUpdateSong(SongDto songDto) {
        // Check if song exists by filename
        return songRepository.findByFilename(songDto.getFilename())
                .switchIfEmpty(Mono.defer(() -> Mono.just(new Song())))  // If not found, create a new Song
                .flatMap(existingSong -> {
                    // Set song properties
                    existingSong.setFilename(songDto.getFilename());
                    existingSong.setTrackNumber(songDto.getTrackNumber());
                    existingSong.setDuration(songDto.getDuration());
                    existingSong.setFavorite(songDto.isFavorite());

//                    // Create or retrieve artists and set them in the song
                    Flux<Mono<Artist>> artistMonos = Flux.fromIterable(songDto.getArtists())
                            .map(artistDto -> getOrCreateArtist(artistDto));

                    // Use Flux to combine multiple Monos into one Flux
                    Flux<Artist> artistFlux = Flux.merge(artistMonos);

                    // Collect the results into a List
                    return artistFlux.collectList()
                            .flatMap(artists -> {
                                existingSong.setArtists(artists);

                                // Save the song and return the Mono<Song>
                                return songRepository.save(existingSong);
                            });
                });
    }


    private Mono<Artist> getOrCreateArtist(ArtistDto artistDto) {

        if (artistDto.getId() != null) {
            return artistRepository.findById(UUID.fromString(artistDto.getId()))
                    .switchIfEmpty(Mono.error(new EntityNotFoundException("Artist with ID " + artistDto.getId() + " not found.")));
        }

        Artist newArtist = new Artist();
        newArtist.setArtistName(artistDto.getArtistName());
        newArtist.setRole(artistDto.getRole());
        newArtist.setCountry(artistDto.getCountry());
        newArtist.setImageUrl(artistDto.getImageUrl());
        return artistRepository.save(newArtist);
    }

public Flux<Album> getAllAlbums(Pageable pageable) {
    return albumRepository.findAll()
            .skip(pageable.getPageNumber() * pageable.getPageSize())
            .take(pageable.getPageSize());
}

public Mono<Album> getAlbumById(UUID id) {
    if (id == null) {
        return Mono.error(new IllegalArgumentException("Album ID cannot be null."));
    }
    return albumRepository.findById(id);
}
@CacheEvict(value = "albumByAlbumName", key = "#name")
public Flux<Album> updateAlbum(String name, AlbumDTO updatedDTO) {
    if (name == null) {
        return Flux.error(new IllegalArgumentException("Album name cannot be null."));
    }
    return albumRepository.findByName(name)
            .flatMap(existingAlbum -> {
                mapDTOToEntity(existingAlbum, updatedDTO);
                return albumRepository.save(existingAlbum);
            });
}


//    @Cacheable(value = "albumByAlbumName", key = "#name")
//    public List<Album> getAlbumByAlbumName(String name) {
//        System.out.println("call from db");
//        return albumRepository.findByName(name);
//    }
    @Cacheable(value = "albumByAlbumName", key = "#name")
    public Flux<Album> getAlbumByAlbumName(String name) {
        System.out.println("call from db");
        return albumRepository.findByName(name);
    }



    public Flux<Album> searchAlbumsByName(String name) {
        return albumRepository.findByName(name);
    }

    public Flux<Album> searchAlbumsByLabel(String label) {
        return albumRepository.findByLabel(label);
    }
//    public Flux<Album> searchAlbums(String name, String label) {
//        return albumRepository.findByNameAndLabel(name, label);
//    }
public Flux<Album> searchAlbums(AlbumDTO searchCriteria) {
    if (searchCriteria != null) {
        if (StringUtils.hasText(searchCriteria.getName()) && StringUtils.hasText(searchCriteria.getLabel())) {
            return albumRepository.findByNameAndLabel(searchCriteria.getName(), searchCriteria.getLabel());
        } else if (StringUtils.hasText(searchCriteria.getName())) {
            return albumRepository.findByName(searchCriteria.getName());
        } else if (StringUtils.hasText(searchCriteria.getLabel())) {
            return albumRepository.findByLabel(searchCriteria.getLabel());
        }
    }
    return getAllAlbums(PageRequest.of(0, 10));
}
public Mono<Void> deleteAlbum(UUID id) {
    if (id == null) {
        return Mono.error(new IllegalArgumentException("Album ID cannot be null."));
    }

    return albumRepository.deleteById(id).then();
}

    private Album mapDTOToEntity(Album album, AlbumDTO albumDTO) {
//        album.setId(album.getId());
        album.setName(albumDTO.getName());
        album.setLabel(albumDTO.getLabel());
        album.setLanguage(albumDTO.getLanguage());
        if (album.getSongs() != null) {
            albumDTO.setSongs(album.getSongs().stream().map(SongDto::from).collect(Collectors.toList()));
        }

        return album;
    }
}
