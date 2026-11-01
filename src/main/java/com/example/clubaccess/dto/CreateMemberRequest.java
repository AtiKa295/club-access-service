package com.example.clubaccess.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@NotBlank
public record CreateMemberRequest(String fullName) {
}
