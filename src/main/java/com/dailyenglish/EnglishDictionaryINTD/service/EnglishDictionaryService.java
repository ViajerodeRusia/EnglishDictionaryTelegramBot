package com.dailyenglish.EnglishDictionaryINTD.service;

import com.dailyenglish.EnglishDictionaryINTD.configuration.EnglishDictionaryConfiguration;
import com.dailyenglish.EnglishDictionaryINTD.http.OxfordDictionaryClient;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class EnglishDictionaryService extends TelegramLongPollingBot {
    private final EnglishDictionaryConfiguration englishDictionaryConfiguration;
    private final OxfordDictionaryClient oxfordDictionaryClient;

    public EnglishDictionaryService(@Autowired EnglishDictionaryConfiguration englishDictionaryConfiguration,
                                    @Autowired OxfordDictionaryClient oxfordDictionaryClient) {
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
                case "/test":
                    try {
                        ResponseEntity<String> translate = oxfordDictionaryClient
                                .getTranslate("en", "ru", "ace",
                                        "b246b661", "fc6a1a1bb6104e169d5498296c174fd7");
                        String message = parseResponse(translate.getBody());
                        sendMessage(chatId, message);
                    } catch (Exception e) {
                        log.error("Error occurred while getting translation: " + e.getMessage(), e);
                        sendMessage(chatId, "An error occurred while fetching translation.");
                    }
                    break;

                default:sendMessage(chatId, "Sorry, required command is not supported");
            }
        }

    }
    private boolean isSingleWord(String text) {
        return text.trim().split("\\s+").length == 1;
    }
    private String parseResponse(String responseBody) {
        JSONObject json = new JSONObject(responseBody);
        String word = json.getString("word");

        JSONArray results = json.getJSONArray("results");
        StringBuilder builder = new StringBuilder();
        builder.append("*").append("Слово: ").append(word).append("*").append("\n");

        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            JSONArray lexicalEntries = result.getJSONArray("lexicalEntries");
            for (int j = 0; j < lexicalEntries.length(); j++) {
                JSONObject lexicalEntry = lexicalEntries.getJSONObject(j);
                JSONArray entries = lexicalEntry.getJSONArray("entries");
                for (int k = 0; k < entries.length(); k++) {
                    JSONObject entry = entries.getJSONObject(k);
                    JSONArray senses = entry.getJSONArray("senses");
                    for (int m = 0; m < senses.length(); m++) {
                        JSONObject sense = senses.getJSONObject(m);
                        if (sense.has("translations")) {
                            JSONArray translations = sense.getJSONArray("translations");
                            for (int n = 0; n < translations.length(); n++) {
                                JSONObject translation = translations.getJSONObject(n);
                                if (translation.has("text")) {
                                    builder.append("*").append("Перевод: ").append("*").append(translation.getString("text")).append("\n");
                                }
                            }
                        }
                        if (sense.has("examples")) {
                            JSONArray examples = sense.getJSONArray("examples");
                            for (int p = 0; p < examples.length(); p++) {
                                JSONObject example = examples.getJSONObject(p);
                                if (example.has("text")) {
                                    builder.append("_").append("Пример: ").append("_").append(example.getString("text")).append("\n");
                                }
                            }
                        }
                    }
                }
            }
        }

        return builder.toString();
    }
    private void sendMessage(String message) {
        // Для отправки сообщения в телеграм
        System.out.println("Отправлено сообщение в телеграм: " + message);
    }
    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!";
        log.info("Replied to " +  name);
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        // Создаем объект SendMessage
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        // Устанавливаем тип разметки
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error occurred while sending message: " + e.getMessage(), e);
        }
    }
}
