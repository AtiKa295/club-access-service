package com.example.clubaccess.dto;

import jakarta.validation.constraints.NotBlank;


public record UpdateMemberRequest(
        @NotBlank(message = "Имя не должно быть пустым")
        String fullName
) {
}
