package com.hongsy.ecommrsapi.security;

import com.hongsy.ecommrsapi.user.entity.Role;
import com.hongsy.ecommrsapi.user.entity.User;
import com.hongsy.ecommrsapi.util.UserDetailsImpl;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements
    WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Set<String> roleNames = Arrays.stream(customUser.roles()).collect(Collectors.toSet());

        User mockUser = User.builder()
            .id(customUser.id())
            .name(customUser.name())
            .email(customUser.email())
            .password("mockuser123")
            .roles(Role.roleFromKorean(roleNames))
            .build();

        UserDetailsImpl principal = new UserDetailsImpl(mockUser);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            principal,
            principal.getPassword(),
            principal.getAuthorities()
        );

        System.out.println("권한 : "+auth.getAuthorities());

        context.setAuthentication(auth);

        return context;
    }
}
