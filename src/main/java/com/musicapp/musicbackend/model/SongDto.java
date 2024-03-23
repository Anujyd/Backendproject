package com.musicapp.musicbackend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class SongDto implements Serializable {
    private String id;
    @NotBlank
    private String titlename;


    private boolean isFavorite;

    private List<ArtistDto> artists;

    @NotNull
    @Positive
    private int trackNumber;

    @Positive
    private double duration;


    public SongDto() {
        this.id = UUID.randomUUID().toString();
    }

    public static SongDto from(Song song) {
        SongDto songDto = new SongDto();
        songDto.setFavorite(song.isFavorite());
        songDto.setId(song.getId());
        songDto.setTitlename(song.getTitlename());
        songDto.setTrackNumber(song.getTrackNumber());
        songDto.setDuration(song.getDuration());


        if (song.getArtists() != null) {
            songDto.setArtists(song.getArtists().stream().map(ArtistDto::from).collect(Collectors.toList()));
        }

        return songDto;
    }

}

