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
@Table(name = "verification_token", schema = "public", catalog = "mars")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"userAccount"})
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_token_id", nullable = false)
    private Long verificationTokenId;
    @Basic
    @Column(name = "verification_token", nullable = false, length = 20)
    private String verificationToken;
    @Basic
    @Column(name = "expired_date")
    private LocalDateTime expiredDate;
    @ManyToOne
    @JoinColumn(name = "user_account_id", referencedColumnName = "user_account_id", nullable = false)
    private UserAccount userAccount;

}
