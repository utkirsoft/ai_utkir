package uz.department.uai.organization.domain;

import jakarta.persistence.*;
import lombok.Data;
import uz.department.uai.auth.domain.Role;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employments")
@Data
public class Employment {
    @Id
    private Long id;

    @Column(name = "position_uz_uz")
    private String positionUzUz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "employment_roles",
            joinColumns = @JoinColumn(name = "employment_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Getter va Setterlar
}