package com.musicapp.musicbackend.repository;

import com.musicapp.musicbackend.model.UserDto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserDto, String> {
    UserDto findUserByEmail(String email);

}
