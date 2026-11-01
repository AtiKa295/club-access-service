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

    @PostMapping("/members")
    public ResponseEntity<MemberResponse> registerMember(@RequestBody CreateMemberRequest request) {
        MemberResponse response = clubAccessService.registerMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/check-in")
    public ResponseEntity<AccessResponse> checkIn(@RequestParam UUID code) {
        AccessResponse response = clubAccessService.checkIn(code);
        return ResponseEntity.ok(response);
    }
}
