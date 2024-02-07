package com.musicapp.musicbackend.model;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
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

@NoArgsConstructor
@Getter
@Setter
@Document(collection = "Album")
public class Album implements Serializable {
    @Id
    private String id;
    @NotBlank
    private String name;

    @DBRef
    private List<Song> songs = new ArrayList<>();
    @NotBlank
    private String label;
    @NotBlank
    private String language;


    @PostConstruct
    public void prePersist() {
        this.setId(UUID.randomUUID().toString());
    }
}
