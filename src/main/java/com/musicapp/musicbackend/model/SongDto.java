package com.musicapp.musicbackend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SongDto implements Serializable {
    private String id;
    @NotBlank
    private String filename;

//    @NotBlank
//    private String artist;
    private boolean isFavorite;

//    @NotNull
//    private ArtistDto artist;

    private List<ArtistDto> artists;
//    @NotBlank
//    private String producer;

    @NotNull
    @Positive
    private int trackNumber;

    @Positive
    private double duration;

//    public SongDto() {
//        this.artists = new ArrayList<>();
//    }
    public static SongDto from(Song song) {
        SongDto songDto = new SongDto();
        songDto.setFavorite(song.isFavorite());
        songDto.setId(song.getId());
//        songDto.setArtist(song.getArtist());
//        songDto.setProducer(song.getProducer());
        songDto.setTrackNumber(song.getTrackNumber());
        songDto.setDuration(song.getDuration());
//        if (song.getArtist() != null) {
//            songDto.setArtist(ArtistDto.from(song.getArtist()));
//        }

        if (song.getArtists() != null) {
            songDto.setArtists(song.getArtists().stream().map(ArtistDto::from).collect(Collectors.toList()));
        }


        return songDto;
    }
}

