package com.javatechie.service;

import com.javatechie.Exceptions.UserAlreadyExistsException;
import com.javatechie.dto.Msg;
import com.javatechie.entity.UserCredential;
import com.javatechie.repository.UserCredentialRepository;
import com.javatechie.utils.Notification;
import org.aspectj.lang.annotation.AfterThrowing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactoryFriend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.l;

@Service
public class AuthService {

    @Autowired
    private static Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserCredentialRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private Notification notification;

    // Method to return all users from system.
    public List<UserCredential> getAllUsers(){
        logger.info("Get All User Called, returned all users from the system");
        return repository.findAll();
    }

    // Method to get user by id from the system
    public Optional<UserCredential> getUserById(String id){
        logger.info("Get user by id called, returned the user with id : "+id);
        Optional<UserCredential> op = repository.findById(id);
        return op;
    }

    // Method to update the user data to the system
    public ResponseEntity<Map<String,String>> updateUser(String id, UserCredential userCredential){
        logger.info("Update user called");
        Map<String,String> mp = new HashMap<>();
        return repository.findById(id).map(user -> {
            user.setName(userCredential.getName());
            user.setEmail(userCredential.getEmail());
            user.setPassword(userCredential.getPassword());
            user.setRole(userCredential.getRole());
            repository.save(user);
            mp.put("message","successfully udated user credentials with id : "+id);
            logger.info("successfully udated user credentials with id : "+id);
            return ResponseEntity.ok(mp);
        }).orElseGet(()->{
            mp.put("message","user not found with the id : "+ id);
            logger.warn("user not found with the id : "+ id);
            return ResponseEntity.ok(mp);
        });

    }

    // Method to get user by name from the system
    public Optional<UserCredential> getUserByName(String id){
        logger.info("get by user name called");
        Optional<UserCredential> uc = repository.findByName(id);
        return uc;
    }


    // Method to delete user from the system
    public Map<String,String> deleteUser(String id){
        logger.info("Delete by user called");
        Map<String,String> mp = new HashMap<>();
        Optional<UserCredential> userCredential = repository.findById(id);
        if(!userCredential.isEmpty()){
            repository.deleteById(id);
            mp.put("message","successfully Deleted user credentials with id : "+id);
            logger.info("successfully Deleted user credentials with id : "+id);
            return mp;
        }else{
            mp.put("message","user not found with the id : "+ id);
            logger.warn("message","user not found with the id : "+ id);
            return mp;
        }
    }


    // Method to save user to the system
    public ResponseEntity<Map<String,String>> saveUser(UserCredential credential) {
        logger.info("save user to the db called");
        if (repository.existsByEmail(credential.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered.");
        }

        if (repository.existsByName(credential.getName())) {
            throw new UserAlreadyExistsException("Name is already registered.");
        }
        UUID uuid = UUID.randomUUID();
        credential.setId(uuid.toString());
        Msg msg = new Msg();
        msg.setTo(credential.getEmail());

        msg.setSubject("Successfully Registered With Fly VJadaun");
        String htmlContent = generateHtmlContent(credential.getName(), credential.getPassword());
        msg.setText(htmlContent);

        notification.send(msg);
        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        repository.save(credential);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User created successfully!");
        logger.info("User created successfully!"+credential.getId());
        return ResponseEntity.ok(response);
    }

    // Method to generate tokens
    public ResponseEntity<Map<String,String>>  generateToken(String username) {
        logger.info("Generate token called");
        String jwt = jwtService.generateToken(username);
        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        return ResponseEntity.ok(response);
    }

    // Method for validation of token
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    private String generateHtmlContent(String username, String password) {
        return """
        <div style="font-family: Arial, sans-serif; background: #f5f5f5; padding: 20px;">
            <div style="max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
                <h2 style="color: #4a90e2;">🎉 Thank You for Registering with <span style="color:#333;">VJ-FLY</span>!</h2>
                <p>Hello <strong>%s</strong>,</p>
                <p>We're excited to have you onboard. Please keep your credentials safe:</p>
                <ul style="line-height: 1.7;">
                    <li><strong>Username:</strong> %s</li>
                    <li><strong>Password:</strong> %s</li>
                </ul>
                <p>We hope you enjoy using our service!</p>
                <hr style="margin: 20px 0;">
                <p style="font-size: 14px; color: #666;">✈️ VJ-FLY Team</p>
            </div>
        </div>
        """.formatted(username, username, password);
    }
}
