package com.javatechie.controller;

import com.javatechie.entity.UserCredential;
import com.javatechie.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin1")
public class AdminController {
    @Autowired
    private AuthService authService;

    @GetMapping("/getAllUsers")
    public List<UserCredential> getAllUsers(){
        return authService.getAllUsers();
    }

    @GetMapping("/getUserById/{id}")
    public Optional<UserCredential> getUserById(@PathVariable String id){
        return authService.getUserById(id);
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<Map<String,String>> updateUser(@PathVariable String id, @RequestBody UserCredential userCredential){
        return authService.updateUser(id,userCredential);
    }

    @DeleteMapping("/deleteUser/{id}")
    public Map<String,String> deleteUser(@PathVariable String id){
        return authService.deleteUser(id);
    }

    @GetMapping("/getUserByName/{name}")
    public Optional<UserCredential> getUserByName(@PathVariable String name){
        return authService.getUserByName(name);
    }
}
