package com.musicapp.musicbackend.Service;

import com.musicapp.musicbackend.model.*;
import com.musicapp.musicbackend.repository.AlbumRepository;
import com.musicapp.musicbackend.repository.ArtistRepository;
import com.musicapp.musicbackend.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
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

    public Album createAlbum(AlbumDTO albumDTO) {
        if (albumDTO == null) {
            throw new IllegalArgumentException("AlbumDTO cannot be null.");
        }

        Album album = new Album();
        album.setName(albumDTO.getName());
        album.setLabel(albumDTO.getLabel());
        album.setLanguage(albumDTO.getLanguage());

        List<Mono<Song>> songMonos = albumDTO.getSongs().stream()
                .map(songDto -> createOrUpdateSong(songDto))
                .collect(Collectors.toList());

        // Use Flux to combine multiple Monos into one Flux
        Flux<Song> songFlux = Flux.merge(songMonos);

        // Collect the results into a List
        List<Song> songs = songFlux.collectList().block();

        album.setSongs(songs);
        album = albumRepository.save(album);
        return mapDTOToEntity(album, albumDTO);
    }

    private Mono<Song> createOrUpdateSong(SongDto songDto) {
        // Check if song exists by filename
        return songRepository.findByFilename(songDto.getFilename())
                .switchIfEmpty(Mono.defer(() -> Mono.just(new Song())))  // If not found, create a new Song
                .flatMap(existingSong -> {
                    // Set song properties
                    existingSong.setFilename(songDto.getFilename());
                    existingSong.setTrackNumber(songDto.getTrackNumber());
                    existingSong.setDuration(songDto.getDuration());
                    existingSong.setFavorite(songDto.isFavorite());

                    // Create or retrieve artists and set them in the song
                    List<Artist> artists = songDto.getArtists().stream()
                            .map(artistDto -> getOrCreateArtist(artistDto))
                            .collect(Collectors.toList());

                    existingSong.setArtists(artists);

                    // Save the song and return the Mono<Song>
                    return songRepository.save(existingSong);
                });
    }

    private Artist getOrCreateArtist(ArtistDto artistDto) {
        if (artistDto.getId() != null) {
            Optional<Artist> existingArtist = artistRepository.findById(UUID.fromString(artistDto.getId()));
            if (existingArtist.isPresent()) {
                return existingArtist.get();
            }
        }

        // If artist does not exist, create a new one
        Artist newArtist = new Artist();
        newArtist.setArtistName(artistDto.getArtistName());
        newArtist.setRole(artistDto.getRole());
        newArtist.setCountry(artistDto.getCountry());
        newArtist.setImageUrl(artistDto.getImageUrl());
        return artistRepository.save(newArtist);
    }

    public Page<Album> getAllAlbums(Pageable pageable) {
        return albumRepository.findAll(pageable);
    }

    public Album getAlbumById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Album ID cannot be null.");
        }
        return albumRepository.findById(id).orElse(null);
    }

    public Album updateAlbum(UUID id, AlbumDTO updatedDTO) {
        if (id == null) {
            throw new IllegalArgumentException("Album ID cannot be null.");
        }

        Album existingAlbum = albumRepository.findById(id).orElse(null);
        if (existingAlbum == null) {
            throw new IllegalArgumentException("Album not found.");
        }

        mapDTOToEntity(existingAlbum, updatedDTO);
        return albumRepository.save(existingAlbum);
    }

    @Cacheable(value = "albumByAlbumName", key = "#name")
    public List<Album> getAlbumByAlbumName(String name) {
        System.out.println("call from db");
        return albumRepository.findByName(name);
    }

    public List<Album> searchAlbumsByName(String name) {
        return albumRepository.findByName(name);
    }

    public void deleteAlbum(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Album ID cannot be null.");
        }
        albumRepository.deleteById(id);
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
