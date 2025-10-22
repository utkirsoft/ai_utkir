package uz.department.uai.user.domain;

import jakarta.persistence.*;
import lombok.Data;
import uz.department.uai.organization.domain.Employment;

import java.time.Instant;

@Entity
@Table(name = "employees")
@Data
public class Employee {
    @Id
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "birth_date")
    private Instant birthDate;

    @Column(name = "passport_serial", length = 2)
    private String passportSerial;

    @Column(name = "passport_number", length = 7)
    private String passportNumber;

    @Enumerated(EnumType.STRING)
    private Gender gender; // (Gender bu Enum: MALE, FEMALE)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employment_id")
    private Employment employment;

    // Getter va Setterlar
}