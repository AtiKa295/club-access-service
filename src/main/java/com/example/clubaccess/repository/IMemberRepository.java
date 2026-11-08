package com.example.clubaccess.repository;

import com.example.clubaccess.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IMemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT DISTINCT m FROM Member m LEFT JOIN FETCH m.qrCodes")
    List<Member> findAllWithQrCodes();
}
