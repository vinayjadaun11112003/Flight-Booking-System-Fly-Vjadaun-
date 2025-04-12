package com.javatechie.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="UserData")
public class UserCredential {

    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String role;
}
