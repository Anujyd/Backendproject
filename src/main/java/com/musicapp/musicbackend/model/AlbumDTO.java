package com.musicapp.musicbackend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class AlbumDTO implements Serializable {
    private String id;
    @NotBlank
    private String name;
//    @NotBlank
//    private String artist;
    @NotNull
    private ArtistDto artist;
    @NotBlank
    private String label;
    @NotBlank
    private String language;


    public static AlbumDTO from (Album album){
        AlbumDTO albumDTO  = new AlbumDTO();
        albumDTO.setId(album.getId());
        albumDTO.setName(album.getName());
//        albumDTO.setArtist(album.getArtist());
        albumDTO.setLabel(album.getLabel());
        albumDTO.setLanguage(album.getLanguage());

        if (album.getArtist() != null) {
            albumDTO.setArtist(ArtistDto.from(album.getArtist()));
        }
        return albumDTO;
    }
}


