package com.example.EnasiniEmsin.entity;


import com.example.EnasiniEmsin.entity.enums.UserStep;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

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

    @Enumerated(EnumType.STRING)
    private UserStep step;

    @ManyToOne
    private Word word;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Word> userWordList;

    private int countQuestion;

    private int countCorrectAnswer;

    private int countIncorrectAnswer;


}
