package com.ecommerce.shop.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
@Table(name = "privileges")
public class PrivilegeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
    @ManyToMany(mappedBy = "privileges")
    private List<RoleEntity> roles;
    public PrivilegeEntity() {}
    public PrivilegeEntity(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<RoleEntity> getRoles() {
        return roles;
    }
}
