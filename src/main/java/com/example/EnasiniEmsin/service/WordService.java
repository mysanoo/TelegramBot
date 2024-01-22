package com.example.EnasiniEmsin.service;

import com.example.EnasiniEmsin.entity.Word;
import com.example.EnasiniEmsin.repo.WordRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepo wordRepo;

    public List<Word> getAll(){
        return wordRepo.findAll();
    }

    public Word addWord(Word word){
        return wordRepo.save(word);
    }
}
