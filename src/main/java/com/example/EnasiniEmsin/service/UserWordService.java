package com.example.EnasiniEmsin.service;


import com.example.EnasiniEmsin.dto.RequestDto;
import com.example.EnasiniEmsin.entity.User;
import com.example.EnasiniEmsin.entity.UserWord;
import com.example.EnasiniEmsin.repo.UserWordRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserWordService {

    private final UserWordRepo userWordRepo;
    private final UserService userService;

    String update = "/update ";
    String delete = "/delete ";

    public void saveWord(User user, String text){

        if(text.substring(0,4).equals("/add")){
            UserWord newWord = new UserWord();

            for (int i = 0; i < text.length() - 2; i++) {
                if(text.charAt(i) == '-'){
                    newWord.setWord(text.substring(5, i-1));
                    newWord.setTranslation(text.substring(i + 2));
                }
            }

            userWordRepo.saveAndFlush(newWord);

            List<UserWord> userWordList = user.getUserVocabulary();
            userWordList.add(newWord);
            user.setUserVocabulary(userWordList);
            userService.updateUser(user);
        }
    }

    public RequestDto deleteWord(String text, User user){
        if(text.substring(0, delete.length()).equals("/delete ") && text.substring(delete.length()).length() > 3){
            List<UserWord> userWordList = userWordRepo.findAll();
            userWordList.forEach(System.out::println);
            String word = text.substring(delete.length()-1);
            UserWord theWord = null;

            for (UserWord userWord : userWordList) {
                if(userWord.getWord().equals(word)){
                    theWord = userWord;
                    break;
                }
            }

            if(theWord != null){
                deleteFromUserList(theWord, user);
                userWordRepo.delete(theWord);
                return new RequestDto("O'chirildi", true, theWord);
            }else return new RequestDto("Bunday so'z topilmadi!", false);
        }
        return null;
    }

    public RequestDto deleteFromUserList(UserWord userWord, User user){
        List<UserWord> userWords = user.getUserVocabulary();

        userWords.remove(userWord);
        user.setUserVocabulary(userWords);
        userService.updateUser(user);

        return new RequestDto(true);
    }
}
