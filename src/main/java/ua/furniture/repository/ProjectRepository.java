package ua.furniture.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.furniture.domain.Account;
import ua.furniture.domain.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends MongoRepository<Project, String> {

    List<Project> findByAccount(Account account);
}
