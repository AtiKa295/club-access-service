package com.example.clubaccess.service;

import com.example.clubaccess.dto.AccessResponse;
import com.example.clubaccess.dto.CreateMemberRequest;
import com.example.clubaccess.dto.MemberResponse;

import java.util.UUID;

public interface IClubAccessService {
    MemberResponse registerMember(CreateMemberRequest request);

    AccessResponse checkIn(UUID qrCode);
}
