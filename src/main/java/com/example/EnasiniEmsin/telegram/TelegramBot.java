package com.example.EnasiniEmsin.telegram;


import com.example.EnasiniEmsin.entity.Question;
import com.example.EnasiniEmsin.entity.User;
import com.example.EnasiniEmsin.entity.Word;
import com.example.EnasiniEmsin.entity.enums.CallBackData;
import com.example.EnasiniEmsin.entity.enums.UserStep;
import com.example.EnasiniEmsin.service.QuestionService;
import com.example.EnasiniEmsin.service.UserService;
import com.example.EnasiniEmsin.service.WordService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.util.*;



@Component
@Slf4j
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private  UserService userService;

    @Autowired
    public WordService wordService;

    @Autowired
    private final QuestionService questionService;


    public UserService getUserService(){
        return userService;
    }



    @Override
    public String getBotUsername() {
        return "myvocabluarybot";
    }

    @Override
    public String getBotToken() {
        return "6408053097:AAEdXNz6sokr89ORlUg5RNdaUh870wswyFI";
    }


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            User user = secureUser(update);
            Message message = update.getMessage();
//            User user = userService.findUserByTelegramID(message.getChatId());

            System.out.println(questionService.createQuestion());
            if(message.getText().equals("/start")){
                game(user);
            }

        }
        if(update.hasCallbackQuery()){
            System.out.println(update.getCallbackQuery());
            String callBack = update.getCallbackQuery().getData();
            Long chatID = update.getCallbackQuery().getFrom().getId();
            User user = userService.findUserByTelegramID(chatID);
            Message message = (Message) update.getCallbackQuery().getMessage();

            if(user.getStep() == UserStep.QUERY){
                if(callBack.equals(CallBackData.YES.toString())){
                    game(user);
                }else {
                    sendMessage(chatID, "o'ynaysan naxxuy");
                    game(user);
                }
            }

            if(user.getStep() == UserStep.RESPOND){
                switch (callBack){
                    case ("ANSWER"): {
                        user.setCountCorrectAnswer(user.getCountCorrectAnswer() + 1);
                        sendMessage(chatID, "To'g'ri");
                        game(user);
                        break;
                    }default : {
                        List<Word> list = user.getUserWordList();
                        list.add(user.getWord());
                        user.setCountIncorrectAnswer(user.getCountIncorrectAnswer() + 1);
                        user.setUserWordList(list);
                        sendMessage(chatID, "Noto'g'ri!\n" +
                                "To'g'ri javob : " + user.getWord().getWord());
                        game(user);
                        break;
                    }
                }
                if(user.getCountQuestion() == 0){
                    sendMessage(chatID, "To'g'ri topilgan so'zlar soni : " + user.getCountCorrectAnswer()
                            + "\nTopolmagan so'zlar soni : " + user.getCountIncorrectAnswer());
                    sendMessage(chatID, "Topa olmagan so'zlar ro'yhati : " + user.getUserWordList());
                    user.setCountQuestion(19);
                    user.setCountCorrectAnswer(0);
                    user.setCountIncorrectAnswer(0);
                    user.setUserWordList(new ArrayList<>());
                    sendQuery(user);
                }else {
                    user.setCountQuestion(user.getCountQuestion()-1);
                }
                userService.updateUser(user);
            }
            deleteMessage(chatID, message.getMessageId());

        }
    }


    public void sendQuery(User user){

        userService.changeUserStep(user, UserStep.QUERY);

        InlineKeyboardButton yes = button(CallBackData.YES, "Ha");
        InlineKeyboardButton no = button(CallBackData.NO, "yo'q");

        List<InlineKeyboardButton> row = new ArrayList<>(Arrays.asList(yes, no));


        List<List<InlineKeyboardButton>> rows = new ArrayList<>(List.of(row));

        sendButtons(rows, user.getTelegramId(), "Boshidan boshlaysizmi? ");

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

    public void deleteMessage(Long chatId, int messageId){
        DeleteMessage deleteMessage = new DeleteMessage(chatId.toString(), messageId);
        try{
            execute(deleteMessage);
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


    public void game(User user){
        Question question = questionService.createQuestion();

        userService.changeUserStep(user, UserStep.RESPOND);
        userService.changeCorrectAnswer(user, question.getWord());

        InlineKeyboardButton firstButton = button(CallBackData.FIRST, question.getAnswers().get(0));
        InlineKeyboardButton secondButton = button(CallBackData.SECOND, question.getAnswers().get(1));
        InlineKeyboardButton thirdButton = button(CallBackData.THIRD, question.getAnswers().get(2));
        InlineKeyboardButton fourthButton = button(CallBackData.ANSWER, question.getWord().getWord());

        List<InlineKeyboardButton> buttons = new ArrayList<>(Arrays.asList(firstButton, secondButton, thirdButton, fourthButton));

        //buttons shuffle
        Collections.shuffle(buttons);

        //buttons add to List<List<InlineKeyboardButton>>
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(buttons);

        sendButtons(rows, user.getTelegramId(), "Shu so'zning tarjimasini toping : " + question.getWord().getTranslation());
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
