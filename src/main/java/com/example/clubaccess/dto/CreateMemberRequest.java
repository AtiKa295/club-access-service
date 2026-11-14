package com.example.clubaccess.dto;

import jakarta.validation.constraints.NotBlank;




public record CreateMemberRequest(
        @NotBlank(message = "Имя не должно быть пустным")
        String fullName
) {
}
