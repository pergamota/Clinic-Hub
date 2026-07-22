package com.clinichub.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.clinichub.dto.ChangePasswordDTO;
import com.clinichub.dto.UserRequestDTO;
import com.clinichub.dto.UserResponseDTO;
import com.clinichub.exception.BusinessRuleException;
import com.clinichub.exception.ResourceNotFoundException;
import com.clinichub.mapper.UserMapper;
import com.clinichub.model.User;
import com.clinichub.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) { 
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO create(UserRequestDTO dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new BusinessRuleException("This email is already registered");
        }

        User user = UserMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        return UserMapper.toResponseDTO(saved);
    }

    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserMapper.toResponseDTO(user);
    }

    public List<UserResponseDTO> getAll() {
        return userRepository.findAll()
        .stream()
        .map(UserMapper::toResponseDTO)
        .toList();
    }

    public void changePassword(Long id, ChangePasswordDTO dto) {
        User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new BusinessRuleException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.findByEmail(dto.email())
        .filter(existingUser -> !existingUser.getId().equals(id))
        .ifPresent(existingUser -> {
            throw new BusinessRuleException("This email is already registered");
        });

        user.setName(dto.name());
        user.setEmail(dto.email());

        User updated = userRepository.save(user);
        return UserMapper.toResponseDTO(updated);
    }

    public void delete(Long id) {
        User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.delete(user);
    }







}
