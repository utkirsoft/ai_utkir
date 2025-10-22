package uz.department.uai.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uz.department.uai.auth.domain.RefreshToken;
import uz.department.uai.auth.repository.RefreshTokenRepository;
import uz.department.uai.user.domain.User;
import uz.department.uai.user.repository.UserRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh-token.expiration-ms}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // << --- ASOSIY O'ZGARISH SHU YERDA --- >>
        // Foydalanuvchining eski refresh token'ini o'chiramiz (agar mavjud bo'lsa)
        refreshTokenRepository.deleteByUser(user);
        // << --- O'ZGARISH TUGADI --- >>

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setUser(user);
        newRefreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        newRefreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(newRefreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        // Eng muhim qator:
        // Token'ning yashash muddati (expiryDate) hozirgi vaqtdan (Instant.now())
        // oldin kelganmi yoki yo'qmi tekshiriladi.
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {

            // Agar muddati o'tgan bo'lsa, token ma'lumotlar bazasidan o'chiriladi.
            refreshTokenRepository.delete(token);

            // Va foydalanuvchiga xatolik yuborilib, qayta login qilishi so'raladi.
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }

        // Agar muddati o'tmagan bo'lsa, token'ning o'zi qaytariladi.
        return token;
    }

    /**
     * Foydalanuvchining refresh token'ini o'chiradi (Logout).
     * @param login foydalanuvchining logini
     */
    public void deleteByLogin(String login) {
        userRepository.findByLogin(login).ifPresent(refreshTokenRepository::deleteByUser);
    }
}