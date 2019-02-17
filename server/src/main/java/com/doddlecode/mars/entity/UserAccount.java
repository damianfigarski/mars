package com.doddlecode.mars.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_account", schema = "public", catalog = "mars")
public class UserAccount implements Serializable {

    @Id
    @Column(name = "user_account_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userAccountId;
    @Basic
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Basic
    @Column(name = "username", nullable = false)
    private String username;
    @Basic
    @Column(name = "password", nullable = false)
    private String password;
    @Basic
    @Column(name = "enabled", nullable = false)
    private boolean enabled;
    @Basic
    @Column(name = "created")
    private LocalDateTime created;
    @ManyToMany(mappedBy = "users")
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<Role> roles;
    @OneToMany(mappedBy = "userAccount")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<UserLog> logs;
    @OneToMany(mappedBy = "userAccount")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<VerificationToken> verificationTokenList;
    @OneToMany(mappedBy = "userAccount")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<PasswordResetToken> passwordResetTokenList;

}
