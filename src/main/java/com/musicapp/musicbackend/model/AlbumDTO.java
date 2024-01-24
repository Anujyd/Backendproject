package com.musicapp.musicbackend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class AlbumDTO implements Serializable {
    private String id;
    @NotBlank
    private String name;
    private List<SongDto> songs;

    @NotBlank
    private String label;
    @NotBlank
    private String language;


    public static AlbumDTO from(Album album) {
        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setId(album.getId());
        albumDTO.setName(album.getName());
        albumDTO.setLabel(album.getLabel());
        albumDTO.setLanguage(album.getLanguage());
        albumDTO.setSongs(album.getSongs().stream().map(SongDto::from).collect(Collectors.toList()));
        return albumDTO;
    }
}


