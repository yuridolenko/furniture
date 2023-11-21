package ua.furniture.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.furniture.domain.Project;

public interface ProjectRepository extends MongoRepository<Project, String> {

}
