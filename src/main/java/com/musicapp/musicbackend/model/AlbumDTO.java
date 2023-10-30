package com.musicapp.musicbackend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class AlbumDTO implements Serializable {
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String artist;
    @NotBlank
    private String label;
    @NotBlank
    private String language;


    public static AlbumDTO from (Album album){
        AlbumDTO albumDTO  = new AlbumDTO();
        albumDTO.setId(album.getId());
        albumDTO.setName(album.getName());
        albumDTO.setArtist(album.getArtist());
        albumDTO.setLabel(album.getLabel());
        albumDTO.setLanguage(album.getLanguage());
        return albumDTO;
    }
}


