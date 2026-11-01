package com.example.clubaccess.service;

import com.example.clubaccess.dto.AccessResponse;
import com.example.clubaccess.dto.CreateMemberRequest;
import com.example.clubaccess.dto.MemberResponse;
import com.example.clubaccess.entity.Member;
import com.example.clubaccess.entity.QrCode;
import com.example.clubaccess.repository.IMemberRepository;
import com.example.clubaccess.repository.IQrCodeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubAccessService implements IClubAccessService{

    private final IMemberRepository memberRepository;
    private final IQrCodeRepository qrCodeRepository;

    @Override
    public MemberResponse registerMember(CreateMemberRequest request) {
        Member member = new Member();
        UUID code = UUID.randomUUID();
        member.setFullName(request.fullName());

        QrCode qrCode = new QrCode(code);
        member.addQrCode(qrCode);

        Member savedMember = memberRepository.save(member);

        return new MemberResponse(savedMember.getId(), savedMember.getFullName(), code);
    }

    @Transactional
    @Override
    public AccessResponse checkIn(UUID qrCode) {
        QrCode currentCode = qrCodeRepository.findByCode(qrCode)
                .orElseThrow(() -> new IllegalArgumentException("Qr-код не найден!"));

        if (!currentCode.isActive()) {
            throw new IllegalStateException("QR-код уже был использован!");
        }

        currentCode.setActive(false);
        Member member = currentCode.getMember();
        UUID newCode = UUID.randomUUID();
        member.addQrCode(new QrCode(newCode));
        memberRepository.save(member);

        return new AccessResponse(member.getFullName(), newCode, "Вход разрешен");
    }
}
