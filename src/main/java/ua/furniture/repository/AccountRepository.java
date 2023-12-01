package ua.furniture.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.furniture.domain.Account;
import ua.furniture.domain.Project;

import java.util.List;

public interface AccountRepository extends MongoRepository<Account, String> {
    List<Account> findByName(String name);
}
