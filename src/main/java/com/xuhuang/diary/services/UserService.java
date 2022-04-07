package com.xuhuang.diary.services;

import java.util.ArrayList;
import java.util.List;

import com.xuhuang.diary.domains.RegisterRequest;
import com.xuhuang.diary.exceptions.RegisterException;
import com.xuhuang.diary.models.User;
import com.xuhuang.diary.models.UserRole;
import com.xuhuang.diary.repositories.UserRepository;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    /*
     * Throws:
     * RegisterException - if either username or email is taken
     */
    public void register(RegisterRequest request) throws RegisterException {
        List<String> exceptionMessages = new ArrayList<>();
        boolean usernameExists = userRepository.findByUsername(request.getUsername()).isPresent();
        boolean emailExists = userRepository.findByEmail(request.getEmail()).isPresent();

        if (usernameExists) {
            exceptionMessages.add(USERNAME_ALREADY_TAKEN);
        }

        if (emailExists) {
            exceptionMessages.add(EMAIL_ALREADY_TAKEN);
        }

        if (!exceptionMessages.isEmpty()) {
            throw new RegisterException(exceptionMessages);
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                bCryptPasswordEncoder.encode(request.getPassword()),
                UserRole.USER);

        userRepository.save(user);
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        return (User) auth.getPrincipal();
    }

    public boolean isCurrentUser(User user) {
        User currentUser = getCurrentUser();

        if (user == null) {
            return currentUser == null;
        } else if (currentUser == null) {
            return false;
        }

        return user.getId().equals(getCurrentUser().getId());
    }

}
