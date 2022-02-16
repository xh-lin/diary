package com.xuhuang.diary.services;

import com.xuhuang.diary.domains.AuthRequest;
import com.xuhuang.diary.models.User;
import com.xuhuang.diary.models.UserRole;
import com.xuhuang.diary.repositories.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final String USER_NOT_FOUND_MSG = "User with username %s not found";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(
            () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
    }

    public void register(AuthRequest request) {
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalStateException("Passwords do not match");
        }

        User user = new User (
            request.getUsername(),
            request.getEmail(),
            request.getPassword(),
            UserRole.USER);

        boolean usernameExists = userRepository.findByUsername(user.getUsername()).isPresent();
        if (usernameExists) {
            throw new IllegalStateException("Username already taken");
        }

        boolean emailExists = userRepository.findByEmail(user.getEmail()).isPresent();
        if (emailExists) {
            throw new IllegalStateException("Email already taken");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
    
}
