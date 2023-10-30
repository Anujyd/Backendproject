package com.musicapp.musicbackend.Service;
import com.musicapp.musicbackend.model.Album;
import com.musicapp.musicbackend.model.AlbumDTO;
import com.musicapp.musicbackend.repository.AlbumRepository;
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

    public Album createAlbum(AlbumDTO albumDTO) {
        if (albumDTO == null) {
            throw new IllegalArgumentException("AlbumDTO cannot be null.");
        }

        Album album = new Album();
        album.setName(albumDTO.getName());
        album.setArtist(albumDTO.getArtist());
        album.setLabel(albumDTO.getLabel());
        album.setLanguage(albumDTO.getLanguage());

        return albumRepository.save(album);
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

    private void mapDTOToEntity(Album album, AlbumDTO albumDTO) {
        album.setName(albumDTO.getName());
        album.setArtist(albumDTO.getArtist());
        album.setLabel(albumDTO.getLabel());
        album.setLanguage(albumDTO.getLanguage());
    }
}
