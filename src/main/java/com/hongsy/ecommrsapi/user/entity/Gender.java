package com.hongsy.ecommrsapi.user.entity;

import com.hongsy.ecommrsapi.util.exception.CustomException;
import com.hongsy.ecommrsapi.util.exception.ErrorCode;

public enum Gender {
    Male("남성"),Female("여성");

    private final String genderKorean;


    Gender(String gender) {
        this.genderKorean = gender;
    }

    public String getGender() {
        return genderKorean;
    }

    public static Gender fromKorean(String korean){
        if(korean==null){
            throw new CustomException(ErrorCode.NULL_GENDER);
        }
        for (Gender gender : Gender.values()) {
            if(gender.genderKorean.equals(korean)){
                return gender;
            }
        }
        throw new CustomException(ErrorCode.WRONG_GENDER);
    }
}
