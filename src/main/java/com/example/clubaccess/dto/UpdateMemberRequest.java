package com.example.clubaccess.dto;

import jakarta.validation.constraints.NotBlank;

@NotBlank
public record UpdateMemberRequest(String fullName) {
}
