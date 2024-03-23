package com.musicapp.musicbackend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class  ArtistDto implements Serializable {
    private String id;

    @NotBlank
    private String artistName;

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
        return artistDto;
    }

    public ArtistDto() {
        this.id = UUID.randomUUID().toString();
    }
}
