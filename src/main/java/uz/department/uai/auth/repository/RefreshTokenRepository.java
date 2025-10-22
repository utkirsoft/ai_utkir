package uz.department.uai.auth.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.department.uai.auth.domain.RefreshToken;
import uz.department.uai.user.domain.User;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    @Transactional
        // O'chirish operatsiyasi uchun tranzaksiya kerak
    void deleteByUser(User user);
}