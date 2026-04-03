package net.engineeringdigest.journalApp.entity;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotNull;


@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private ObjectId id;

    @NotNull
    @Indexed(unique = true)
    private String userName;

    @NotNull
    private String password; 

    private String email;
    private boolean sentimentAnalysis;

    @DBRef
    private List<JournalEntity> userEntries = new ArrayList<>();
    private List<String> roles;


}
