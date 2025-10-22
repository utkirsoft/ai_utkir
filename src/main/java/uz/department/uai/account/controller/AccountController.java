package uz.department.uai.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.department.uai.account.dto.AccountDTO;
import uz.department.uai.account.service.AccountService;
import uz.department.uai.user.domain.User;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * Joriy autentifikatsiyadan o'tgan foydalanuvchining to'liq ma'lumotlarini qaytaradi.
     * Frontend dastur yuklanganda bir marta chaqiriladi.
     */
    @GetMapping("/account")
    public ResponseEntity<AccountDTO> getAccount(@AuthenticationPrincipal User currentUser) {
        AccountDTO accountDetails = accountService.getAccountDetails(currentUser);
        return ResponseEntity.ok(accountDetails);
    }
}