package com.keanusantos.personalfinancemanager.domain.role;

import com.keanusantos.personalfinancemanager.domain.role.enums.RoleName;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false, name = "role_name")
    private RoleName role;

    public Role(){};

    public Role(Long id, RoleName role){
        this.id = id;
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleName getRole() {
        return role;
    }

    public void setRole(RoleName role) {
        this.role = role;
    }
}
