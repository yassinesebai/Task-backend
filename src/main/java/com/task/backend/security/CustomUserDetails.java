package com.task.backend.security;

import com.task.backend.model.UserManagement;
import com.task.backend.model.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails extends UserManagement implements UserDetails {

    private final String username;
    private final String password;
    Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(UserManagement byUsername) {
        this.username = byUsername.getUserCode();
        this.password= byUsername.getPassword();
        this.setBlockAccess(byUsername.getBlockAccess());

        List<GrantedAuthority> auths = new ArrayList<>();
        auths.add(byUsername.getRole().toGrantedAuthority());
        this.authorities = auths;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
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
        System.out.println("is the account active : " + this.getBlockAccess().equals('N'));
        return this.getBlockAccess().equals('N');
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