package com.musicapp.musicbackend.model;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
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
@Document(collection = "artist")
public class Artist implements Serializable {
    @Id
    private String id;
    @NotBlank
    private String artistName;
    @DBRef
    private List<Song> songs ;
    @DBRef
    private List<Album> albums;
    @NotBlank
    private String country;

    @PostConstruct
    public void prePersist(){this.setId(UUID.randomUUID().toString());}

}
