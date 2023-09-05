package com.HRMS.repository.entity;

import com.HRMS.repository.enums.EStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "quests")
public class User {
    @Id
    String id;
    Long authid;
    String nameSurname;
    String email;
    String password;
    String department;
    String title;
    String location;
    @Builder.Default
    EStatus status= EStatus.PENDING;

}
