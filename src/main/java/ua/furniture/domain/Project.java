package ua.furniture.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document(collection = "projects")
@AllArgsConstructor
@Data
public class Project {

    @Id
    private String id;

    private String name;

    @DocumentReference(lazy = true)
    private Account account;

}
