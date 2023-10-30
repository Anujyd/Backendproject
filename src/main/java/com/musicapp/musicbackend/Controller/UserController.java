package com.musicapp.musicbackend.Controller;

import com.musicapp.musicbackend.Service.UserService;
import com.musicapp.musicbackend.model.UserDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register(@ModelAttribute UserDto userDto, Model model) {
        model.addAttribute("UserDto", userDto);
        return "register";
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        System.out.println("we are here");
        return userService.createUser(userDto);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@Valid @RequestBody UserDto userDto) {
        return userService.updateUser(userDto);
    }

    @DeleteMapping("/delete/{Id}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

}
