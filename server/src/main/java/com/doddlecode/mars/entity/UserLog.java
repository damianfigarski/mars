package com.doddlecode.mars.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "user_log", schema = "public", catalog = "mars")
public class UserLog implements Serializable {

    @Id
    @Column(name = "user_log_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userLogId;
    @Basic
    @Column(name = "log_date", nullable = false)
    private LocalDateTime logDate;
    @Basic
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    @ManyToOne
    @JoinColumn(name = "user_account_id", referencedColumnName = "user_account_id", nullable = false)
    private UserAccount userAccount;

}
