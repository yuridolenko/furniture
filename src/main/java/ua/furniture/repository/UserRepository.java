package ua.furniture.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.furniture.domain.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);
}
