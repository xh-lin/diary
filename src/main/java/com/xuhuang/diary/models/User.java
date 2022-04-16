package com.xuhuang.diary.models;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class User extends BaseEntity implements UserDetails {

    public static final int USERNAME_LENGTH = 63;
    public static final int EMAIL_LENGTH = 63;
    public static final int PASSWORD_LENGTH = 127;
    public static final int PASSWORD_ENCRYPTED_LENGTH = 63;
    public static final int USER_ROLE_LENGTH = 15;

    @Column(nullable = false, unique = true, length = USERNAME_LENGTH)
    @NonNull
    private String username;

    @Column(nullable = false, unique = true, length = EMAIL_LENGTH)
    @NonNull
    private String email;

    @Column(nullable = false, length = PASSWORD_ENCRYPTED_LENGTH)
    @JsonIgnore
    @ToString.Exclude
    @NonNull
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = USER_ROLE_LENGTH)
    @NonNull
    private UserRole userRole;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("id")
    @JsonIgnore
    @ToString.Exclude
    @NonNull
    private Set<Book> books = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("id")
    @JsonIgnore
    @ToString.Exclude
    @NonNull
    private Set<Tag> tags = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole.name());
        return Collections.singletonList(authority);
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
