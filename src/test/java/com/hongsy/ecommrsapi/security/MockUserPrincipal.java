package com.hongsy.ecommrsapi.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class MockUserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final List<GrantedAuthority> authorities;

    public MockUserPrincipal(Long id, String username, List<String> roles) {
        this.id = id;
        this.username = username;
        this.authorities = roles.stream()
            .map(role -> {
                String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                return new SimpleGrantedAuthority(roleName);
            })
            .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return "password"; // 테스트용 임의 값
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
