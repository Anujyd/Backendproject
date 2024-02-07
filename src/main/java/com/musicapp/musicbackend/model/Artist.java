package com.musicapp.musicbackend.model;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;

import java.io.Serializable;
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

//    @PostConstruct
//    public void prePersist(){this.setId(UUID.randomUUID().toString());}
public Mono<Void> prePersist() {
    this.setId(UUID.randomUUID().toString());
    return Mono.empty();
}
}
