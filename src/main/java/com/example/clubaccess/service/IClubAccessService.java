package com.example.clubaccess.service;

import com.example.clubaccess.dto.AccessResponse;
import com.example.clubaccess.dto.CreateMemberRequest;
import com.example.clubaccess.dto.MemberResponse;
import com.example.clubaccess.dto.UpdateMemberRequest;

import java.util.List;
import java.util.UUID;

public interface IClubAccessService {
    MemberResponse registerMember(CreateMemberRequest request);
    AccessResponse checkIn(UUID qrCode);
    List<MemberResponse> getAllMembers();
    MemberResponse getMemberById(Long id);
    MemberResponse updateMember(Long id, UpdateMemberRequest request);
    void deleteMember(Long id);
    UUID createQrCodeFOrMember(Long memberId);
    void updateQrCodeStatus(Long qrCodeId, boolean active);
    void deleteQrCode (Long qrCodeId);
}
