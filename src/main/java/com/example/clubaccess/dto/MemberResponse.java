package com.example.clubaccess.dto;

import java.util.UUID;

public record MemberResponse(
        Long id,
        String fullName,
        UUID activeQrCode
) {
}
