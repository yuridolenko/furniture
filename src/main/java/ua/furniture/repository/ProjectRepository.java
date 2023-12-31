package ua.furniture.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.furniture.domain.Account;
import ua.furniture.domain.Project;

import java.util.Optional;

public interface ProjectRepository extends MongoRepository<Project, String> {

    Optional<Project> findByAccount(Account account);
}
