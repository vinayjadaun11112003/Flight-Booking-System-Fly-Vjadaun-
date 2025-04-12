package com.microservice.UserService.controller;

import com.microservice.UserService.dto.AuthRequest;
import com.microservice.UserService.dto.Msg;
import com.microservice.UserService.entity.UserEntity;
import com.microservice.UserService.jwtUtils.JwtUtil;
import com.microservice.UserService.repository.UserRepository;
import com.microservice.UserService.utils.Notification;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Notification notification;



    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder encoder;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserEntity user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest req) {
        Optional<UserEntity> user = Optional.ofNullable(userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("Not found")));
        if (encoder.matches(req.getPassword(), user.get().getPassword())) {
            String token = jwtUtil.generateToken(user.get().getUsername(), String.valueOf(user.get().getRole()));
            return ResponseEntity.ok(Map.of("token", token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }



    @GetMapping("/getAllUsers")
    public List<UserEntity> getAllUsers() {
        Msg msg = new Msg();
        msg.setTo("vinayjadaun11112003@gmail.com");
        msg.setSubject("Alert : User is calling find all method");
        msg.setText("User is calling find all method of the user service to get all the user detail in once");
        notification.send(msg);
        return userRepository.findAll();
    }

    @GetMapping("/getUser/{id}")
    public UserEntity getUser(@PathVariable String id) {
        return userRepository.findById(id).orElse(null);
    }

    @PostMapping("/addNewUser")
    public UserEntity addNewUser(@RequestBody UserEntity user) {
          UUID uuid = UUID.randomUUID();
          user.setUserId(uuid.toString());
          userRepository.save(user);
          Msg msg = new Msg();
          msg.setTo("vinayjadaun11112003@gmail.com");
          msg.setSubject("User creation");
          msg.setText("New User Created");


          notification.send(msg);
          return user;
    }

    @PutMapping("updateUser")
    public UserEntity updateUser(@RequestBody UserEntity user) {
        userRepository.save(user);
        return user;
    }

    @DeleteMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable String id) {
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
            return "deleted user successfully with id " + id;
        }else{
            return "User not found";
        }
    }
}
