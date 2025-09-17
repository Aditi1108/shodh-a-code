package com.shodhacode.service;

import com.shodhacode.dto.UserRequest;
import com.shodhacode.entity.User;
import com.shodhacode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    @Transactional
    public User registerUser(UserRequest userRequest) {
        // Validate email format
        if (userRequest.getEmail() == null || !EMAIL_PATTERN.matcher(userRequest.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        // Check if user already exists by username
        Optional<User> existingUser = userRepository.findByUsername(userRequest.getUsername());
        
        if (existingUser.isPresent()) {
            log.info("User already exists with username: {}", userRequest.getUsername());
            return existingUser.get();
        }
        
        // Create new user
        User newUser = new User();
        newUser.setUsername(userRequest.getUsername());
        newUser.setFullName(userRequest.getFullName());
        newUser.setEmail(userRequest.getEmail());
        newUser.setScore(0);
        newUser.setProblemsSolved(0);
        
        User savedUser = userRepository.save(newUser);
        log.info("New user registered: {} with email: {}", savedUser.getUsername(), savedUser.getEmail());
        
        return savedUser;
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
}