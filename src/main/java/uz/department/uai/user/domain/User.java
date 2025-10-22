package uz.department.uai.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.department.uai.auth.domain.Authority;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    @JsonIgnore // Parolni API orqali qaytarmaslik uchun
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private boolean activated = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_authorities",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_name")
    )
    private Set<Authority> authorities = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    // =================================================================
    // UserDetails INTERFEYSI METODLARI
    // =================================================================

    /**
     * Foydalanuvchining huquqlarini (authorities) qaytaradi.
     * Bizning Authority entity'mizni Spring Security tushunadigan SimpleGrantedAuthority'ga o'giramiz.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());
    }
    @JsonIgnore
    public Set<Authority> getDomainAuthorities() {
        return this.getAuthorities().stream()
                .map(ga -> {
                    Authority a = new Authority();
                    a.setName(ga.getAuthority());
                    return a;
                })
                .collect(Collectors.toSet());
    }
//    Set<Authority> authorities = user.getAuthorities().stream()
//            .map(GrantedAuthority::getAuthority)
//            .map(authorityRepository::findByName) // returns Authority entity
//            .filter(Objects::nonNull)
//            .collect(Collectors.toSet());

    /**
     * Foydalanuvchining parolini qaytaradi.
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Foydalanuvchini autentifikatsiya qilish uchun uning unikal nomini (bizda bu login) qaytaradi.
     */
    @Override
    public String getUsername() {
        return this.login;
    }

    /**
     * Akkauntning amal qilish muddati tugamaganligini bildiradi.
     * Agar maxsus logika kerak bo'lmasa, odatda 'true' qaytariladi.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Akkaunt bloklanmaganligini bildiradi.
     * Agar maxsus logika kerak bo'lmasa, odatda 'true' qaytariladi.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Foydalanuvchi hisob ma'lumotlarining (parol) amal qilish muddati tugamaganligini bildiradi.
     * Agar maxsus logika kerak bo'lmasa, odatda 'true' qaytariladi.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Foydalanuvchi aktiv yoki aktiv emasligini bildiradi.
     * Biz buni o'zimizning 'activated' maydonimizga bog'laymiz.
     */
    @Override
    public boolean isEnabled() {
        return this.activated;
    }

//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "signing_serial", referencedColumnName = "serial")
//    private SigningInfo signingInfo;

    // Getter va Setterlar
}