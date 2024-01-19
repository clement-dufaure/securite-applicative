package re.dufau.securiteapplicative.securitynightmare.dao;

import jakarta.annotation.PostConstruct;
import org.springframework.data.jpa.repository.JpaRepository;
import re.dufau.securiteapplicative.securitynightmare.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}