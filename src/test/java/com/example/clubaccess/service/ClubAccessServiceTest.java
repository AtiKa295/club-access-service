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

}


