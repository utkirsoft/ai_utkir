package uz.department.uai.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.department.uai.auth.domain.RefreshToken;
import uz.department.uai.auth.dto.JwtResponseDTO;
import uz.department.uai.auth.dto.LoginRequestDTO;
import uz.department.uai.auth.dto.RefreshTokenRequestDTO;
import uz.department.uai.auth.repository.RefreshTokenRepository;
import uz.department.uai.auth.service.JwtService;
import uz.department.uai.auth.service.RefreshTokenService;
import uz.department.uai.user.domain.User;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> authenticateUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getLogin(), loginRequestDTO.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        return ResponseEntity.ok(new JwtResponseDTO(accessToken, refreshToken.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO request) {
        // Eski refresh token'ni topamiz
        return refreshTokenService.findByToken(request.getToken())
                // Muddati o'tmaganligini tekshiramiz
                .map(refreshTokenService::verifyExpiration)
                .map(refreshToken -> {
                    // Token'ga bog'liq foydalanuvchini olamiz
                    User user = refreshToken.getUser();

                    // 1. Eski token'ni o'chiramiz

                    refreshTokenRepository.delete(refreshToken);

                    // 2. Yangi access token yaratamiz
                    String newAccessToken = jwtService.generateToken(user);

                    // 3. Yangi refresh token yaratamiz
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getLogin());

                    // Foydalanuvchiga ikkala yangi token'ni qaytaramiz
                    return ResponseEntity.ok(new JwtResponseDTO(newAccessToken, newRefreshToken.getToken()));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    /**
     * Foydalanuvchini tizimdan chiqaradi (refresh token'ni bekor qiladi).
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.badRequest().body("No authenticated user found to log out.");
        }

        // Eng to'g'ri usul: .getName() metodidan foydalanish
        String login = authentication.getName();

        refreshTokenService.deleteByLogin(login);
        return ResponseEntity.ok("User has been logged out successfully!");
    }
}