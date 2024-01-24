package com.musicapp.musicbackend.model;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "song")
@NoArgsConstructor
@Getter
@Setter
public class Song implements Serializable {

    @Id
    private String id;

    @NotBlank
    private String filename;


    private boolean isFavorite;

    @DBRef
    private List<Artist> artists = new ArrayList<>();

    @NotBlank
    @Positive
    private int trackNumber;

    @Positive
    private double duration;

    @PostConstruct
    public void prePersist() {
        this.setId(UUID.randomUUID().toString());
    }
}
