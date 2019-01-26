package com.doddlecode.mars.repository;

import com.doddlecode.mars.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByVerificationToken(String verificationToken);
}
