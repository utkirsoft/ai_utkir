package uz.department.uai.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.department.uai.user.domain.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their login name.
     * This method is crucial for the UserDetailsService to load the user
     * during the authentication process.
     *
     * @param login the login name of the user to find.
     * @return an Optional containing the user if found, or an empty Optional otherwise.
     */
    Optional<User> findByLogin(String login);

}