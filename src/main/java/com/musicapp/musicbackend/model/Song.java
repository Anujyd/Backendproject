package com.musicapp.musicbackend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "song")
@Getter
@Setter
public class Song implements Serializable {

    @Id
    @Field("id")
    private String id;

    @NotBlank
    private String titlename;


    private boolean isFavorite;

    @DBRef
    private List<Artist> artists = new ArrayList<>();

    @NotBlank
    @Positive
    private int trackNumber;

    @Positive
    private double duration;

    public Song() {
        this.id = UUID.randomUUID().toString();
    }


}
