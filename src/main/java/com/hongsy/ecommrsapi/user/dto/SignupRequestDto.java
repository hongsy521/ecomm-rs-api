package com.hongsy.ecommrsapi.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    @Email(message = "유효하지 않은 이메일 형식입니다.")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
    private String password;
    private String name;
    private Integer age;
    private String gender;
    private String phoneNumber;
    private String address;
    @NotBlank(message = "역할을 입력해주세요.")
    private Set<String> roles;

}
