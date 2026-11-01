package com.example.clubaccess.dto;

import java.util.UUID;

public record AccessResponse(
        String memberName,
        UUID newQrCode,
        String message
) {
}
