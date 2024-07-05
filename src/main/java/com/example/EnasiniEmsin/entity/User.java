package com.example.EnasiniEmsin.entity;


import com.example.EnasiniEmsin.entity.enums.UserStep;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long telegramId;

    private String username;

    @Enumerated(EnumType.STRING)
    private UserStep step;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<UserWord> userVocabulary;

    @ManyToOne
    private Word word;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Word> userWordList;

    private int countQuestion;

    private int countCorrectAnswer;

    private int countIncorrectAnswer;


}
