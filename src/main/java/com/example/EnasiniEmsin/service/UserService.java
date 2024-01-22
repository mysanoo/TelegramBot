package com.example.EnasiniEmsin.service;

import com.example.EnasiniEmsin.entity.User;
import com.example.EnasiniEmsin.entity.enums.UserStep;
import com.example.EnasiniEmsin.repo.UserRepo;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

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
                .telegramId(update.getMessage().getChatId())
                .username(update.getMessage().getFrom().getFirstName())
                .build();
        userRepo.save(user);
        return user;
    }

    public List<User> allUsers(){
        return userRepo.findAll();
    }
}
