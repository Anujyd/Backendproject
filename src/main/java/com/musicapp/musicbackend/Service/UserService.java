package com.musicapp.musicbackend.Service;

import com.musicapp.musicbackend.model.UserDto;
import com.musicapp.musicbackend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
public class UserService {
    private final  UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {this.userRepository = userRepository; }

    @Transactional
    public Optional<UserDto> findUserByEmail(String email) {return Optional.ofNullable(userRepository.findUserByEmail(email));}

    public boolean userExists(String email){
        return findUserByEmail(email).isPresent();
    }

    public UserDto createUser( UserDto userDto) {

       // if (userRepository.findUserByEmail(userDto.getEmail()) != null) {
       //     throw new IllegalArgumentException("Email is already registered");
       // }

        return userRepository.save(userDto);
    }
    public UserDto updateUser(@Valid UserDto userDto) {
        UserDto existingUser = userRepository.findById(String.valueOf(userDto.getId()))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        existingUser.setFirstname(userDto.getFirstname());
        existingUser.setLastname(userDto.getLastname());
        existingUser.setEmail(userDto.getEmail());



        return userRepository.save(existingUser);
    }

    public void deleteUser(Long userId) {

        UserDto existingUser = userRepository.findById(String.valueOf(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        userRepository.delete(existingUser);
    }
}
