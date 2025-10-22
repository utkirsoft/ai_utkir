package uz.department.uai.organization.domain;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "branches")
@Data
public class Branch {
    @Id
    private Long id;

    @Column(name = "name_uz_uz")
    private String nameUzUz;

    @Column(name = "name_uz_crl")
    private String nameUzCrl;

    @Column(name = "name_ru_ru")
    private String nameRuRu;


}