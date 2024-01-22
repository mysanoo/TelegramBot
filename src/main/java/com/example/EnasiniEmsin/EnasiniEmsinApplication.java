package com.example.EnasiniEmsin;

import com.example.EnasiniEmsin.telegram.TelegramBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class EnasiniEmsinApplication {


	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(EnasiniEmsinApplication.class, args);

		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(context.getBean(TelegramBot.class));
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

}
