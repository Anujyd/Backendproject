package com.musicapp.musicbackend.Service;
import com.musicapp.musicbackend.model.Album;
import com.musicapp.musicbackend.model.AlbumDTO;
import com.musicapp.musicbackend.model.Artist;
import com.musicapp.musicbackend.model.ArtistDto;
import com.musicapp.musicbackend.repository.AlbumRepository;
import com.musicapp.musicbackend.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AlbumService {
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private ArtistRepository artistRepository;
    public Album createAlbum(AlbumDTO albumDTO) {
        if (albumDTO == null) {
            throw new IllegalArgumentException("AlbumDTO cannot be null.");
        }

        Album album = new Album();
        album.setName(albumDTO.getName());
        album.setLabel(albumDTO.getLabel());
        album.setLanguage(albumDTO.getLanguage());
//       album.setArtist(albumDTO.getArtist());

        Artist artist = getOrCreateArtist(String.valueOf(albumDTO.getArtist()));
        album.setArtist(artist);


        album = albumRepository.save(album);
        return mapDTOToEntity(album,albumDTO);
    }
    private Artist getOrCreateArtist(String artistName) {
        List<Artist> existingArtists = artistRepository.findByArtistName(artistName);
        if (!existingArtists.isEmpty()) {
            return existingArtists.get(0);
        } else {
            Artist newArtist = new Artist();
            newArtist.setArtistName(artistName);
            return artistRepository.save(newArtist);
        }
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
    @Cacheable(value = "albumByAlbumName" , key = "#name")
    public List<Album> getAlbumByAlbumName(String name){
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
        album.setName(albumDTO.getName());
//        album.setArtist(albumDTO.getArtist());
        album.setLabel(albumDTO.getLabel());
        album.setLanguage(albumDTO.getLanguage());
        if (album.getArtist() != null) {
            albumDTO.setArtist(ArtistDto.from(album.getArtist()));
        }

//        if (albumDTO.getArtist() != null) {
//            ArtistDto artistDto = albumDTO.getArtist();
//            Artist artist = new Artist();
//            artist.setArtistName(artistDto.getArtistName());
//            artist.setCountry(artistDto.getCountry());
//            album.setArtist(artist);
//        }
        return album;
    }
}
