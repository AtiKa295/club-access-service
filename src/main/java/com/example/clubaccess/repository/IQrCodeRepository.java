package com.example.clubaccess.repository;

import com.example.clubaccess.entity.QrCode;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface IQrCodeRepository extends JpaRepository<QrCode, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<QrCode> findByCode(UUID code);
}
