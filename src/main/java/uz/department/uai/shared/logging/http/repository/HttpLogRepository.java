package uz.department.uai.shared.logging.http.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.department.uai.shared.logging.http.domain.HttpLog;

@Repository
public interface HttpLogRepository extends JpaRepository<HttpLog, Long> {
}