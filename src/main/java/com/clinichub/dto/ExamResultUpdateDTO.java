package com.clinichub.dto;

import jakarta.validation.constraints.NotNull;

public record ExamResultUpdateDTO(@NotNull(message = 
    "The result is required")String resultUrl) {
    
}
