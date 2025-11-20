package com.hongsy.ecommrsapi.user.entity;

import com.hongsy.ecommrsapi.user.dto.UserRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "site_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User {

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

    public static User createUser(UserRequestDto userRequestDto, Gender gender, String password){
        User user = User.builder()
            .email(userRequestDto.getEmail())
            .password(password)
            .name(userRequestDto.getName())
            .age(userRequestDto.getAge())
            .gender(gender)
            .phoneNumber(userRequestDto.getPhoneNumber())
            .address(userRequestDto.getAddress())
            .build();

        return user;
    }
}
