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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "role", schema = "public", catalog = "mars")
public class Role implements Serializable {

    @Id
    @Column(name = "role_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;
    @Basic
    @Column(name = "role_name", nullable = false, length = 45)
    private String roleName;
    @ManyToMany
    @JoinTable(name = "user_has_role",
            catalog = "mars",
            schema = "public",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_account_id", referencedColumnName = "user_account_id", nullable = false))
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<UserAccount> users;

}
