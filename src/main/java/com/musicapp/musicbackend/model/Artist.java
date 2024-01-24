package com.musicapp.musicbackend.model;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

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

    @NotBlank
    private String role;

    private  String imageUrl;
    @NotBlank
    private String country;

    @PostConstruct
    public void prePersist(){this.setId(UUID.randomUUID().toString());}

}
