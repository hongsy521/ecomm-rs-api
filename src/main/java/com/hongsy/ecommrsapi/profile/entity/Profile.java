package com.hongsy.ecommrsapi.profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile")
@Getter
@NoArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    private String name;
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(columnDefinition = "TEXT")
    private String address;

}
