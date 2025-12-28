package com.hongsy.ecommrsapi.user.entity;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE("정상", "활동 중인 계정입니다."),
    PENDING("대기", "이메일 인증 등 승인이 필요한 상태입니다."),
    SUSPENDED("정지", "운영 정책에 의해 이용이 제한된 계정입니다."),
    WITHDRAWN("탈퇴", "회원 탈퇴가 완료된 계정입니다.");

    private final String label;
    private final String description;

    UserStatus(String label, String description) {
        this.label = label;
        this.description = description;
    }
}
