package com.hongsy.ecommrsapi.profile.entity;

public enum Gender {
    Male("남성"),Female("여성");

    private final String gender;


    Gender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }
}
