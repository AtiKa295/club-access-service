package com.example.clubaccess.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;


public record CreateMemberRequest(
        @NotBlank(message = "Имя не должно быть пустным")
        String fullName
) {
}
