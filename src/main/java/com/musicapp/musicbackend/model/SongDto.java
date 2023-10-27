package com.musicapp.musicbackend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.io.Serializable;

@Data
public class SongDto implements Serializable {
    private String id;
    @NotBlank
    private String filename;

    @NotBlank
    private String artist;

    private boolean isFavorite;


    @NotBlank
    private String producer;

    @NotNull
    @Positive
    private int trackNumber;

    @Positive
    private double duration;

    public static SongDto from(Song song) {
        SongDto songDto = new SongDto();
        songDto.setFavorite(song.isFavorite());
        songDto.setArtist(song.getArtist());
        songDto.setProducer(song.getProducer());
        songDto.setTrackNumber(song.getTrackNumber());
        songDto.setDuration(song.getDuration());
        return songDto;
    }
}

