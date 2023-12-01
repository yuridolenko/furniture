package ua.furniture.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts")
@AllArgsConstructor
@Data
public class Account {

    @Id
    private String id;

    private String name;

    private String address;

    private String phone;
}
