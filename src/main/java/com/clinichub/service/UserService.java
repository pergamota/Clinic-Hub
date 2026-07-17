package com.clinichub.service;

import java.util.List;

import org.springframework.stereotype.Service;
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
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO create(UserRequestDTO dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new BusinessRuleException("This email is already registered");
        }

        User user = UserMapper.toEntity(dto);
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

    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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
