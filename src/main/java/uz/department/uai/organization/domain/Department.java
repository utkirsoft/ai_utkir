package uz.department.uai.organization.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "departments")
@Data

public class Department {
    @Id
    private Long id;

    @Column(name = "name_uz_uz")
    private String nameUzUz;

    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    // Getter va Setterlar
}