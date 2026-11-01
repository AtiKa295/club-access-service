package com.example.clubaccess.repository;

import com.example.clubaccess.entity.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IQrCodeRepository extends JpaRepository<QrCode, Long> {
    Optional<QrCode> findByCode(UUID code);
}
