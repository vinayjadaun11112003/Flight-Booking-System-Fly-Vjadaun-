package com.microservice.UserService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="Users")

public class UserEntity {
    @Id
    private String userId;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role; // ROLE_USER or ROLE_ADMIN
}
