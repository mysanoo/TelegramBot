package com.example.EnasiniEmsin.service;

import com.example.EnasiniEmsin.entity.User;
import com.example.EnasiniEmsin.entity.Word;
import com.example.EnasiniEmsin.entity.enums.UserStep;
import com.example.EnasiniEmsin.repo.UserRepo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class UserService {

    @Autowired
     private final UserRepo userRepo;

    public User addUser(Update update){
        User user = User.builder()
                .step(UserStep.NEW)
                .userVocabulary(new ArrayList<>())
                .telegramId(update.getMessage().getChatId())
                .username(update.getMessage().getFrom().getFirstName())
                .userWordList(new ArrayList<>())
                .countCorrectAnswer(0)
                .countIncorrectAnswer(0)
                .countQuestion(20)
                .build();
        userRepo.save(user);
        return user;
    }

    public List<User> allUsers(){
        return userRepo.findAll();
    }

    public User findUserByTelegramID(Long telegramID){

        Optional<User> user = allUsers().parallelStream()
                .filter(user1 -> user1.getTelegramId().equals(telegramID))
                .findFirst();
        assert user.isPresent();
        return user.get();
    }

    public void changeUserStep(User user, UserStep userStep){
        user.setStep(userStep);
        userRepo.save(user);
    }
    public void changeCorrectAnswer(User user, Word word){
        user.setWord(word);
        userRepo.save(user);
    }

    public void updateUser(User user){
        userRepo.save(user);
    }

//    public void addWordToWordList(User user, Word word){
//        List<Word> wordList = user.getUserWordList();
//        wordList.add(word);
//        user.setUserWordList(wordList);
//        userRepo.save(user);
//    }
}
