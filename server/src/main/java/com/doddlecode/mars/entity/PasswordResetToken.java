package com.doddlecode.mars.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_token", schema = "public", catalog = "mars")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"userAccount"})
public class PasswordResetToken {

    @Id
    @Column(name = "password_reset_token_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long passwordResetTokenId;
    @Basic
    @Column(name = "token", nullable = false, length = 60)
    private String token;
    @Basic
    @Column(name = "used", nullable = false)
    private boolean used;
    @Basic
    @Column(name = "expired_date", nullable = false)
    private LocalDateTime expiredDate;
    @ManyToOne
    @JoinColumn(name = "user_account_id", referencedColumnName = "user_account_id", nullable = false)
    private UserAccount userAccount;

}
