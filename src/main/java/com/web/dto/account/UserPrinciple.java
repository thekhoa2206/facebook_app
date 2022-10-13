package com.web.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.web.model.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

public class UserPrinciple implements UserDetails {
    private static final long serialVersionUID = 1L;

    private int id;

    private String name;

    private String phoneNumber;

    @JsonIgnore
    private String password;

    public UserPrinciple(int id, String name,
                         String phoneNumber, String password) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.password = password;
    }

    public static UserPrinciple build(Account user) {


        return new UserPrinciple(
                user.getId(),
                user.getName(),
                user.getPhoneNumber(),
                user.getPassword()
        );
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getUsername() {
        return phoneNumber;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserPrinciple user = (UserPrinciple) o;
        return Objects.equals(id, user.id);
    }
}
