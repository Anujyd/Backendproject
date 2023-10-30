package com.musicapp.musicbackend;

import com.musicapp.musicbackend.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MusicbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusicbackendApplication.class, args);
	}

}
