package com.example.clubaccess.service;

import com.example.clubaccess.dto.AccessResponse;
import com.example.clubaccess.entity.Member;
import com.example.clubaccess.entity.QrCode;
import com.example.clubaccess.repository.IMemberRepository;
import com.example.clubaccess.repository.IQrCodeRepository;
import com.example.clubaccess.service.ClubAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ClubAccessServiceTest {
    @Mock
    private IMemberRepository memberRepository;

    @Mock
    private IQrCodeRepository qrCodeRepository;
    @InjectMocks
    private ClubAccessService clubAccessService;


    @Test
    void chekIn_WhenCodeIsValid_ShouldAllowAccessAndGenerateNewCode() {
        UUID testCode = UUID.randomUUID();

        Member member = new Member();
        member.setId(1L);
        member.setFullName("Устюгов Михаил");

        QrCode qrCode = new QrCode(testCode);
        qrCode.setMember(member);
        qrCode.setActive(true);

        org.mockito.Mockito.when(qrCodeRepository.findByCode(testCode))
                .thenReturn(java.util.Optional.of(qrCode));

        AccessResponse response = clubAccessService.checkIn(testCode);

        org.junit.jupiter.api.Assertions.assertNotNull(response);
        org.junit.jupiter.api.Assertions.assertEquals("Вход разрешен", response.message());
        org.junit.jupiter.api.Assertions.assertFalse(qrCode.isActive());
        org.junit.jupiter.api.Assertions.assertNotNull(response.newQrCode());

        org.mockito.Mockito.verify(memberRepository).save(member);
    }

    @Test
    void checkIn_WhenCodeIsInactive_ShouldThrowIllegalStateException() {
        UUID testCode = UUID.randomUUID();
        QrCode inactiveCode = new QrCode(testCode);
        inactiveCode.setActive(false);

        org.mockito.Mockito.when(qrCodeRepository.findByCode(testCode))
                .thenReturn(java.util.Optional.of(inactiveCode));

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> clubAccessService.checkIn(testCode)
        );


        org.mockito.Mockito.verifyNoInteractions(memberRepository);
    }

    @Test
    void checkIn_WhenCodeNotFound_ShouldThrowIllegalArgumentException() {
        UUID nonExistenCode = UUID.randomUUID();
        org.mockito.Mockito.when(qrCodeRepository.findByCode(nonExistenCode))
                .thenReturn(java.util.Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> clubAccessService.checkIn(nonExistenCode)
        );

        org.mockito.Mockito.verifyNoInteractions(memberRepository);
    }

    @Test
    void checkIn_ShouldDeactiveOldCode_CreateNewCode_AndRejectReuse() {
        UUID oldCodeCalue = UUID.randomUUID();

        Member member = new Member();
        member.setId(1L);
        member.setFullName("Михаил Устюгов");

        QrCode oldQrCode = new QrCode(oldCodeCalue);
        member.addQrCode(oldQrCode);

        org.mockito.Mockito.when(qrCodeRepository.findByCode(oldCodeCalue))
                .thenReturn(java.util.Optional.of(oldQrCode));


        AccessResponse response = clubAccessService.checkIn(oldCodeCalue);

        org.junit.jupiter.api.Assertions.assertNotNull(response);
        org.junit.jupiter.api.Assertions.assertEquals("Вход разрешен", response.message());
        org.junit.jupiter.api.Assertions.assertNotNull(response.newQrCode());
        org.junit.jupiter.api.Assertions.assertNotEquals(oldCodeCalue, response.newQrCode());
        org.junit.jupiter.api.Assertions.assertFalse(oldQrCode.isActive(), "Старый QR-код должен быть деактивирован");

        org.junit.jupiter.api.Assertions.assertEquals(2, member.getQrCodes().size());
        boolean hasNewActiveCode = member.getQrCodes().stream()
                .anyMatch(q -> q.getCode().equals(response.newQrCode()) && q.isActive());
        org.junit.jupiter.api.Assertions.assertTrue(hasNewActiveCode, "Новый код должен быть добавлен в список участника и быть активным");

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> clubAccessService.checkIn(oldCodeCalue),
                "Повторное использование кода должно приводить к ошибке"
        );
    }

    @Test
    void createQrCodeForMember_WhenMemberAlreadyHasActiveQr_ShouldThrowException() {
        Long memberId = 1L;
        Member member = new Member();
        org.springframework.test.util.ReflectionTestUtils.setField(member, "id", memberId);
        member.setFullName("Виктория Устюгова");

        QrCode existingActiveQr = new QrCode(UUID.randomUUID());
        member.addQrCode(existingActiveQr);

        org.mockito.Mockito.when(memberRepository.findById(memberId))
                .thenReturn(java.util.Optional.of(member));

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> clubAccessService.createQrCodeFOrMember(memberId),
                "Нельзч создать второй активный Qr-код, если у участика он уже есть"
        );
    }

    @Test
    void UpdateQrCodeStatus_WhenActivatingSecondQrForMember_ShouldThrowException() {
        Member member = new Member();
        org.springframework.test.util.ReflectionTestUtils.setField(member, "id", 1L);
        member.setFullName("Михаил Устюгов");

        QrCode activeQr = new QrCode(UUID.randomUUID());
        org.springframework.test.util.ReflectionTestUtils.setField(activeQr, "id", 10L);
        activeQr.setActive(true);
        member.addQrCode(activeQr);

        QrCode inactiveQr = new QrCode(UUID.randomUUID());
        org.springframework.test.util.ReflectionTestUtils.setField(inactiveQr, "id", 20L);
        inactiveQr.setActive(false);
        member.addQrCode(inactiveQr);

        org.mockito.Mockito.when(qrCodeRepository.findById(20L))
                .thenReturn(java.util.Optional.of(inactiveQr));

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> clubAccessService.updateQrCodeStatus(20L, true),
                "Нельзя активировать Qr-код, если у участника уже есть активный Qr"
        );
    }

}


