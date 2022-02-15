package com.xuhuang.diary.services;

import com.xuhuang.diary.repositories.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final String USER_NOT_FOUND_MSG = "user with username %s not found";

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(
            () -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
    }
    
}
