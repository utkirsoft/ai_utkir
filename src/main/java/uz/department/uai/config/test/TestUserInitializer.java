package uz.department.uai.config.test;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.department.uai.user.domain.User;
import uz.department.uai.user.repository.UserRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Agar "testuser" mavjud bo'lmasa, uni yaratamiz
        if (userRepository.findByLogin("testuser").isEmpty()) {
            User testUser = new User();
            testUser.setId(UUID.randomUUID());
            testUser.setLogin("testuser");
            // Parolni HECH QACHON to'g'ridan-to'g'ri saqlamang! Faqat heshlangan holatda.
            testUser.setPassword(passwordEncoder.encode("password123"));
            testUser.setFirstName("Test");
            testUser.setLastName("Userov");
            testUser.setActivated(true); // Akkaunt aktiv bo'lishi shart

            userRepository.save(testUser);
            System.out.println(">>> Test user 'testuser' with password 'password123' created! <<<");
        }
    }
}