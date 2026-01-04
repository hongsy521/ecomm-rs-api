package com.hongsy.ecommrsapi.user.entity;

import com.hongsy.ecommrsapi.user.dto.SignupRequestDto;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

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

    @Type(JsonBinaryType.class)
    @Column(name = "roles", columnDefinition = "jsonb")
    private Set<Role> roles;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    public static User createUser(SignupRequestDto signupRequestDto, Gender gender, String password,Set<Role> roles){
        User user = User.builder()
            .email(signupRequestDto.getEmail())
            .password(password)
            .name(signupRequestDto.getName())
            .age(signupRequestDto.getAge())
            .gender(gender)
            .phoneNumber(signupRequestDto.getPhoneNumber())
            .address(signupRequestDto.getAddress())
            .roles(roles)
            .build();

        return user;
    }

    public void editStatusByWithdrawn() {
        this.status=UserStatus.WITHDRAWN;
    }
}
