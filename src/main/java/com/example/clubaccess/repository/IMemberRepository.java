package com.example.clubaccess.repository;

import com.example.clubaccess.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMemberRepository extends JpaRepository<Member, Long> {
}
