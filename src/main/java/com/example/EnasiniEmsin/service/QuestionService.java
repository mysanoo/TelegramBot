package com.example.EnasiniEmsin.service;

import com.example.EnasiniEmsin.entity.Question;
import com.example.EnasiniEmsin.entity.Word;
import com.example.EnasiniEmsin.repo.WordRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final WordRepo wordRepo;

    public Random random = new Random();
    public List<String> answers(List<Word> wordList){
        return wordList.stream()
                .map(Word::getWord)
                .toList();
    }

    public List<Word> wordList(){
        return wordRepo.findAll();
    }


    public Question createQuestion(){
        Question question = new Question();
        question.setWord(wordRepo.findAll().get(random.nextInt(wordList().size())));
        Set<String> answers = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            answers.add(wordList().get(random.nextInt(wordList().size())).getWord());
            if(answers.size() > 5) break;
        }

        question.setAnswers(answers.stream().toList());
        return question;
    }

}
