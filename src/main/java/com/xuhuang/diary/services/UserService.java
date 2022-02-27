package com.xuhuang.diary.services;

import javax.security.auth.message.AuthException;

import com.xuhuang.diary.domains.RegisterRequest;
import com.xuhuang.diary.models.User;
import com.xuhuang.diary.models.UserRole;
import com.xuhuang.diary.repositories.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    public static final String USER_NOT_FOUND_MSG = "User with username %s not found.";
    public static final String USERNAME_ALREADY_TAKEN = "Username already taken.";
    public static final String EMAIL_ALREADY_TAKEN = "Email already taken.";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(
            () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
    }

    public void register(RegisterRequest request) throws AuthException {
        boolean usernameExists = userRepository.findByUsername(request.getUsername()).isPresent();
        if (usernameExists) {
            throw new AuthException(USERNAME_ALREADY_TAKEN);
        }

        boolean emailExists = userRepository.findByEmail(request.getEmail()).isPresent();
        if (emailExists) {
            throw new AuthException(EMAIL_ALREADY_TAKEN);
        }

        User user = new User (
            request.getUsername(),
            request.getEmail(),
            bCryptPasswordEncoder.encode(request.getPassword()),
            UserRole.USER);

        userRepository.save(user);
    }

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean isCurrentUser(User user) {
        return user.getId().equals(getCurrentUser().getId());
    }
    
}
