package com.example.clubaccess.controller;

import com.example.clubaccess.dto.AccessResponse;
import com.example.clubaccess.dto.CreateMemberRequest;
import com.example.clubaccess.dto.MemberResponse;
import com.example.clubaccess.service.IClubAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access")
@RequiredArgsConstructor
public class ClubAccessController {
    private final IClubAccessService clubAccessService;

    @PostMapping("/check-in")
    public ResponseEntity<AccessResponse> checkIn(@RequestParam UUID code) {
        AccessResponse response = clubAccessService.checkIn(code);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/members/{memberId}/qrcodes")
    public ResponseEntity<UUID> createQrCode(@PathVariable Long memberId) {
        UUID newCode = clubAccessService.createQrCodeFOrMember(memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCode);
    }

    @PatchMapping("/qrcodes/{qrCodeId}")
    public ResponseEntity<Void> updateQrCodeStatus(
            @PathVariable Long qrCodeId,
            @RequestParam boolean active) {
        clubAccessService.updateQrCodeStatus(qrCodeId, active);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/qrcodes/{qrCodeId}")
    public ResponseEntity<Void> deleteQrCode(@PathVariable Long qrCodeId) {
        clubAccessService.deleteQrCode(qrCodeId);
        return ResponseEntity.noContent().build();
    }
}
