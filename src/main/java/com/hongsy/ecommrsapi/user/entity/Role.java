package com.hongsy.ecommrsapi.user.entity;

import com.hongsy.ecommrsapi.util.exception.CustomException;
import com.hongsy.ecommrsapi.util.exception.ErrorCode;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ROLE_ADMIN("관리자"),ROLE_BUYER("구매자"),ROLE_SELLER("판매자");

    private final String roleKorean;

    public static Set<Role> roleFromKorean(Set<String> roles){
        if(roles==null){
            throw new CustomException(ErrorCode.NULL_ROLE);
        }
        Set<Role> roleSet = roles.stream().map(
            role-> {
                for (Role r : Role.values()) {
                    if(role.equals(r.getRoleKorean())){
                        return r;
                    }
                }
                throw new CustomException(ErrorCode.INVALID_ROLE);
        }).collect(Collectors.toSet());

        return roleSet;
    }
}
