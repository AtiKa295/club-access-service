package com.example.clubaccess.service;

import com.example.clubaccess.dto.AccessResponse;
import com.example.clubaccess.dto.CreateMemberRequest;
import com.example.clubaccess.dto.MemberResponse;
import com.example.clubaccess.dto.UpdateMemberRequest;
import com.example.clubaccess.entity.Member;
import com.example.clubaccess.entity.QrCode;
import com.example.clubaccess.repository.IMemberRepository;
import com.example.clubaccess.repository.IQrCodeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubAccessService implements IClubAccessService{

    private final IMemberRepository memberRepository;
    private final IQrCodeRepository qrCodeRepository;

    @Override
    public MemberResponse registerMember(CreateMemberRequest request) {
        log.info("Регистрация нового участника с именем: {}", request.fullName());

        Member member = new Member();
        UUID code = UUID.randomUUID();
        member.setFullName(request.fullName());

        QrCode qrCode = new QrCode(code);
        member.addQrCode(qrCode);

        Member savedMember = memberRepository.save(member);

        log.info("Участник успешно зарегистрирован с ID: {}", savedMember.getId());

        return new MemberResponse(savedMember.getId(), savedMember.getFullName(), code);
    }

    @Transactional
    @Override
    public AccessResponse checkIn(UUID qrCode) {

        log.info("Запрос на вход по QR-коду");
        QrCode currentCode = qrCodeRepository.findByCode(qrCode)
                .orElseThrow(() -> new IllegalArgumentException("Qr-код не найден!"));

        if (!currentCode.isActive()) {
            log.warn("Попытка входа по неактивному QR-коду");
            throw new IllegalStateException("QR-код уже был использован!");
        }

        currentCode.setActive(false);
        Member member = currentCode.getMember();
        UUID newCode = UUID.randomUUID();
        member.addQrCode(new QrCode(newCode));
        memberRepository.save(member);

        log.info("Успешный проход! Старый код деактивирован, выдан новый");

        return new AccessResponse(member.getFullName(), newCode, "Вход разрешен");
    }

    @Override
    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAllWithQrCodes().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public MemberResponse getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Участник с таким id: " + id + " не найден"));
        return mapToResponse(member);
    }

    @Override
    @Transactional
    public MemberResponse updateMember(Long id, UpdateMemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Участник с таким id: " + id + " не найден"));
        member.setFullName(request.fullName());
        memberRepository.save(member);

        return mapToResponse(member);
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new IllegalArgumentException("Участник с таким id: " + id + " не найден");
        }
        memberRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UUID createQrCodeFOrMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Участник с Id: " + memberId + " не найден!"));

        UUID newCode = UUID.randomUUID();
        QrCode qrCode = new QrCode(newCode);
        member.addQrCode(qrCode);
        memberRepository.save(member);

        return newCode;
    }

    @Override
    @Transactional
    public void updateQrCodeStatus(Long qrCodeId, boolean active) {
        QrCode qrCode = qrCodeRepository.findById(qrCodeId)
                .orElseThrow(() -> new IllegalArgumentException("QR-код с ID:" + qrCodeId + " не найден!"));

        qrCode.setActive(active);
        qrCodeRepository.save(qrCode);
    }

    @Override
    @Transactional
    public void deleteQrCode(Long qrCodeId) {
        QrCode qrCode = qrCodeRepository.findById(qrCodeId)
                .orElseThrow(() -> new IllegalArgumentException("QR-код с ID:" + qrCodeId + " не найден!"));

        Member member = qrCode.getMember();
        if (member != null) {
            member.getQrCodes().remove(qrCode);
        }

        qrCodeRepository.delete(qrCode);
    }

    private MemberResponse mapToResponse(Member member) {
        UUID activeCode = member.getQrCodes().stream()
                .filter(QrCode::isActive)
                .map(QrCode::getCode)
                .findFirst()
                .orElse(null);

        return new MemberResponse(member.getId(), member.getFullName(), activeCode);
    }
}
