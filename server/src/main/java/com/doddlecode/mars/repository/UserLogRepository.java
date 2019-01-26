package com.doddlecode.mars.repository;

import com.doddlecode.mars.entity.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLogRepository extends JpaRepository<UserLog, Long> {
}
