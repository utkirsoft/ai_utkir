package uz.department.uai.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "authorities")
@Data
public class Authority {
    @Id
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    // Getter va Setterlar
}
