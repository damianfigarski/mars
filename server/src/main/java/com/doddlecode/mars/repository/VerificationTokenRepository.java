package com.doddlecode.mars.repository;

import com.doddlecode.mars.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByVerificationToken(String verificationToken);
}
