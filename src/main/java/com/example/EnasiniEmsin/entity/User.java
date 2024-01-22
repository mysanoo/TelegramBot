package com.example.EnasiniEmsin.entity;


import com.example.EnasiniEmsin.entity.enums.UserStep;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "telegramUser")
@Builder
public class User {

    @Id
    private Long telegramId;

    private String username;

    private UserStep step;


}
