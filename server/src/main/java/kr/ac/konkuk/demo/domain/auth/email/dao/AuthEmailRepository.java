package kr.ac.konkuk.demo.domain.auth.email.dao;

import kr.ac.konkuk.demo.domain.auth.email.domain.AuthEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AuthEmailRepository extends JpaRepository<AuthEmail, Long> {

    Optional<AuthEmail> findFirstByTargetOrderByCreatedAtDesc(String target);

    Long countByTargetAndCreatedAtAfter(String target, LocalDateTime localDateTime);

}
