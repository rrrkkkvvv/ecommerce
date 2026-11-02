package com.ecommerce.shop.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
@Table(name = "roles")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<UserEntity> users;

    @ManyToMany
    @JoinTable(
            name = "roles_privileges",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id")
    )
    private List<PrivilegeEntity>privileges;
    public RoleEntity() {}
    public RoleEntity(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public List<UserEntity> getUsers() {
        return users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PrivilegeEntity> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<PrivilegeEntity> privileges) {
        this.privileges = privileges;
    }
}
