package com.musicapp.musicbackend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class  ArtistDto implements Serializable {
    private String id;

    @NotBlank
    private String artistName;
//    @NotNull
//    private  SongDto song;
    private String imageUrl;
    @NotBlank
    private String country;
    @NotBlank
    private String role;

    public static ArtistDto from (Artist artist){
        ArtistDto artistDto = new ArtistDto();
        artistDto.setId(artist.getId());
        artistDto.setArtistName(artist.getArtistName());
        artistDto.setCountry(artist.getCountry());
        artistDto.setRole(artist.getRole());
        artistDto.setImageUrl(artist.getImageUrl());
//        artistDto.setImageData(artist.getImageData());
//        if (artist.getSong() != null) {
//            artistDto.setSong(SongDto.from(artist.getSong()));
//        }
        return artistDto;
    }

}
