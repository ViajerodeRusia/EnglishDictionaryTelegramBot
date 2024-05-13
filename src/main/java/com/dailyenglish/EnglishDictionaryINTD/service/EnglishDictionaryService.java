package com.dailyenglish.EnglishDictionaryINTD.service;

import com.dailyenglish.EnglishDictionaryINTD.configuration.EnglishDictionaryConfiguration;
import com.dailyenglish.EnglishDictionaryINTD.http.OxfordDictionaryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
@Slf4j
@Component
public class EnglishDictionaryService extends TelegramLongPollingBot {
    final EnglishDictionaryConfiguration englishDictionaryConfiguration;
    final OxfordDictionaryClient oxfordDictionaryClient;

    public EnglishDictionaryService(EnglishDictionaryConfiguration englishDictionaryConfiguration,
                                    OxfordDictionaryClient oxfordDictionaryClient) {
        this.englishDictionaryConfiguration = englishDictionaryConfiguration;
        this.oxfordDictionaryClient = oxfordDictionaryClient;
    }

    @Override
    public String getBotUsername() {
        return englishDictionaryConfiguration.getBotName();
    }

    @Override
    public String getBotToken() {
        return englishDictionaryConfiguration.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        //что должен делать бот, когда ему пишут
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch(messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
//                case "/test":
//                    ResponseEntity<String> translate = oxfordDictionaryClient.getTranslate("en", "ru", "ace");
//                    System.out.println(translate.getBody());
//                    break;

                default:sendMessage(chatId, "Sorry, required command is not supported");
            }
        }

    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!";
        log.info("Replied to " +  name);
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        //метод для отправки сообщений
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }
}
