package com.example.EnasiniEmsin.telegram;

import com.example.EnasiniEmsin.TelegramBotComponent;
import com.example.EnasiniEmsin.entity.User;
import com.example.EnasiniEmsin.entity.Word;
import com.example.EnasiniEmsin.entity.enums.CallBackData;
import com.example.EnasiniEmsin.service.UserService;
import com.example.EnasiniEmsin.service.WordService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.util.*;


@TelegramBotComponent
@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private  UserService userService;

    @Autowired
    public WordService wordService;


    public UserService getUserService(){
        return userService;
    }



    @Override
    public String getBotUsername() {
        return "absolutly_nothing_bot";
    }

    @Override
    public String getBotToken() {
        return "5812350550:AAGqazy4yYr3UWrejafg2kUvOBGFNhJb2w8";
    }

    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasMessage()) {
            User user = secureUser(update);
            Message message = update.getMessage();





            if(user.getTelegramId() == 1919052694){
                if("/new".equals(message.getText().substring(0,4))){
                    if(!saveWord(message.getText())){
                        sendMessage(message, "You enter new word incorrect way!");
                    }else sendMessage(message, "Successfully saved!");
                }
                switch (message.getText()) {
                    case "/list" -> sendMessage(message, String.valueOf(userService.allUsers()));
                    case "/words" -> sendMessage(message, String.valueOf(wordService.getAll()));
                }
            }

            if(message.getText().equals("/start")){
                game(wordService.getAll(), message.getChatId());
            }




        }else{
            User user = secureUser(update, update.getCallbackQuery().getFrom().getId());
            String data = update.getCallbackQuery().getData();
            if(data.equals(CallBackData.ANSWER.toString())){
                sendMessage(user.getTelegramId(), "To'g'ri");
                game(wordService.getAll(), user.getTelegramId());
            }else {
                sendMessage(user.getTelegramId(), "Noto'g'ri");
                game(wordService.getAll(), user.getTelegramId());
            }
        }
    }


    public void sendMessage(Message message, String text){
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendMessage(Long chatId, String text){
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    public void sendButtons(List<List<InlineKeyboardButton>> rows, Long chatId, String text){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rows);

        SendMessage sendMessage = new SendMessage();

        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setText(text);
        sendMessage.setChatId(chatId.toString());

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }



    public InlineKeyboardButton button(CallBackData callBack, String text){

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(callBack.toString());

        return inlineKeyboardButton;
    }

    public User secureUser(Update update){
        List<User> users = getUserService().allUsers();

        if(!users.isEmpty()){
            for (User user : users) {
                if(user.getTelegramId().equals(update.getMessage().getChatId())){
                    return user;
                }
            }
        }

        return getUserService().addUser(update);
    }

    public User secureUser(Update update, Long chatId){
        List<User> users = getUserService().allUsers();

        if(!users.isEmpty()){
            for (User user : users) {
                if(user.getTelegramId().equals(chatId)){
                    return user;
                }
            }
        }

        return getUserService().addUser(update);
    }


    public void game(List<Word> words, Long chatId){

        Word word = words.get((int) (Math.random() * words.size()));



        String question = word.getWord();
        String first;
        String second;
        String third;
        String fourth;

        do{
            first = words.get((int) (Math.random() * words.size())).getTranslation();
            second = words.get((int) (Math.random() * words.size())).getTranslation();
            third = words.get((int) (Math.random() * words.size())).getTranslation();
            fourth = word.getTranslation();
        }while(first.equals(second) && second.equals(third) && third.equals(fourth));



        InlineKeyboardButton firstButton = button(CallBackData.FIRST, first);
        InlineKeyboardButton secondButton = button(CallBackData.SECOND, second);
        InlineKeyboardButton thirdButton = button(CallBackData.THIRD, third);
        InlineKeyboardButton fourthButton = button(CallBackData.ANSWER, fourth);

        List<InlineKeyboardButton> buttons = new ArrayList<>(Arrays.asList(firstButton, secondButton, thirdButton, fourthButton));

        //buttons shuffle
        Collections.shuffle(buttons);

        //buttons add to List<List<InlineKeyboardButton>>
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(buttons);

        sendButtons(rows, chatId, "Shu so'zning tarjimasini toping : " + question);

    }



    public boolean saveWord(@NotNull String text){
        Word word = new Word();
        for (int i = 0; i < text.length(); i++) {
            if(text.charAt(i)==':'){
                word.setWord(text.substring(5,i));
                word.setTranslation(text.substring(i+1));
                wordService.addWord(word);
                return true;
            }
        }
        return false;
    }





//    public static List<InlineKeyboardButton> getRandomElements(List<InlineKeyboardButton> buttons) {
//        List<InlineKeyboardButton> randomElements = new ArrayList<>();
//        Random rand = new Random();
//
//        for (int i = 0; i < 4; i++) {
//            int randomIndex = rand.nextInt(buttons.size());
//            InlineKeyboardButton element = buttons.get(randomIndex);
//            randomElements.add(element);
//            buttons.remove(randomIndex); // Optional: To avoid duplicate selections if elements are non-unique
//        }
//
//        return randomElements;
//    }


}
