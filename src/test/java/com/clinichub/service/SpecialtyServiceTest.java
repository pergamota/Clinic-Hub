package com.clinichub.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.clinichub.dto.SpecialtyRequestDTO;
import com.clinichub.dto.SpecialtyResponseDTO;
import com.clinichub.exception.BusinessRuleException;
import com.clinichub.exception.ResourceNotFoundException;
import com.clinichub.model.Specialty;
import com.clinichub.repository.SpecialtyRepository;
    
@ExtendWith(MockitoExtension.class)
public class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private SpecialtyService specialtyService;

    @Test
    void shouldCreateSpecialtySuccessfully() {
        SpecialtyRequestDTO dto = new SpecialtyRequestDTO("Cardiology");
        when(specialtyRepository.findByName("Cardiology")).thenReturn(Optional.empty());
        when(specialtyRepository.save(any(Specialty.class))).thenAnswer(inv -> inv.getArgument(0));

        SpecialtyResponseDTO result = specialtyService.create(dto);

        assertEquals("Cardiology", result.name());
    }

    @Test
    void shouldThrowExceptionWhenSpecialtyNameAlreadyExists() {
        SpecialtyRequestDTO dto = new SpecialtyRequestDTO("Cardiology");
        when(specialtyRepository.findByName("Cardiology"))
            .thenReturn(Optional.of(new Specialty()));

        assertThrows(BusinessRuleException.class, () -> specialtyService.create(dto));
    }


    @Test
    void shouldGetByIdSuccessfully() {
        // 1. Preparar dado de entrada — você já fez isso certo
        Specialty specialty = new Specialty();
        specialty.setId(1L);
        specialty.setName("Cardiology");

        // 2. Ensinar o mock — faltava o .thenReturn
        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));

        // 3. Chamar o método real — isso estava faltando inteiro
        SpecialtyResponseDTO result = specialtyService.getById(1L);

        // 4. Conferir o resultado — isso estava faltando inteiro
        assertEquals(1L, result.id());
        assertEquals("Cardiology", result.name());
    }

    @Test
    void shouldThrowExceptionWhenSpecialtyNotFound() {
        when(specialtyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> specialtyService.getById(1L));
    }


    @Test
    void shouldGetAllSuccessfully() {
        Specialty specialty1 = new Specialty();
        specialty1.setId(1L);
        specialty1.setName("Cardiology");

        Specialty specialty2 = new Specialty();
        specialty2.setId(2L);
        specialty2.setName("Geral");

        when(specialtyRepository.findAll()).thenReturn(List.of(specialty1, specialty2));

        // 3. Chamar o método real
        List<SpecialtyResponseDTO> result = specialtyService.getAll();

        // 4. Conferir — aqui checamos o TAMANHO da lista, já que são 2 itens
        assertEquals(2, result.size());
    }

    
    @Test
    void shouldUpdateSpecialtySuccessfully() {
        // 1. O objeto que "já existe no banco" (nome antigo)
        Specialty existingSpecialty = new Specialty();
        existingSpecialty.setId(1L);
        existingSpecialty.setName("Cardiology");

        // 2. O que o cliente está mandando (nome novo)
        SpecialtyRequestDTO dto = new SpecialtyRequestDTO("Cardiologia Pediátrica");

        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(existingSpecialty));
        when(specialtyRepository.save(any(Specialty.class))).thenAnswer(inv -> inv.getArgument(0));

        // 3. Chama o UPDATE de verdade (não getById)
        SpecialtyResponseDTO result = specialtyService.update(1L, dto);

        // 4. Confere que o nome mudou pro novo
        assertEquals("Cardiologia Pediátrica", result.name());
    }

    @Test
    void shouldGetByIdSpecialtyNotFound() {
        when(specialtyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> specialtyService.getById(1L));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentSpecialty() {
        when(specialtyRepository.findById(1L)).thenReturn(Optional.empty());

        SpecialtyRequestDTO dto = new SpecialtyRequestDTO("Cardiology");

        assertThrows(ResourceNotFoundException.class, () -> specialtyService.update(1L, dto));
    }

    @Test
    void shouldDeleteSpecialtySuccessfully() {
        Specialty specialty = new Specialty();
        specialty.setId(1L);
        specialty.setName("Cardiology");

        when(specialtyRepository.findById(1L)).thenReturn(Optional.of(specialty));

        specialtyService.delete(1L);

        verify(specialtyRepository).delete(specialty);
    }




}