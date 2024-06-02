package com.dailyenglish.EnglishDictionaryINTD.service;

import com.dailyenglish.EnglishDictionaryINTD.configuration.EnglishDictionaryConfiguration;
import com.dailyenglish.EnglishDictionaryINTD.http.FreeDictionaryAPI;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class EnglishDictionaryService extends TelegramLongPollingBot {
    private final EnglishDictionaryConfiguration englishDictionaryConfiguration;
    private final FreeDictionaryAPI freeDictionaryAPI;
    private final TranslationService translationService;

    public EnglishDictionaryService(@Autowired EnglishDictionaryConfiguration englishDictionaryConfiguration,
                                    @Autowired FreeDictionaryAPI freeDictionaryAPI,
                                    @Autowired TranslationService translationService) {
        this.englishDictionaryConfiguration = englishDictionaryConfiguration;
        this.freeDictionaryAPI = freeDictionaryAPI;
        this.translationService = translationService;
    }

    @Override
    public String getBotUsername() {
        return englishDictionaryConfiguration.getBotName();
    }

    @Override
    public String getBotToken() {
        return englishDictionaryConfiguration.getToken();
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!";
        log.info("Replied to " + name);
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error occurred while sending message: " + e.getMessage(), e);
        }
    }
    @Override
    public void onUpdateReceived(Update update) {
        //Обработка входящих сообщений
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:
                    getDefinition(chatId, messageText);
            }
        }
    }

    private void getDefinition(long chatId, String word) {
        try {
            // Отправляем запрос к API и преобразуем ответ в объект Java
            ResponseEntity<String> responseEntity = freeDictionaryAPI.getDefinition(word);
            log.info("Response from API: " + responseEntity.toString()); // Добавляем эту строку для вывода ответа в журналы
            String responseBody = responseEntity.getBody();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // Обрабатываем каждый элемент массива
            StringBuilder messageBuilder = new StringBuilder();
            for (JsonNode node : rootNode) {
                String wordRequired = node.path("word").asText();
                messageBuilder.append("Определение слова '").append(wordRequired).append("' - "
                        + translationService.translateEngToRus(wordRequired) + ":\n");

                // Извлекаем определения для каждого слова
                JsonNode meaningsNode = node.path("meanings");
                for (JsonNode meaningNode : meaningsNode) {
                    String partOfSpeech = meaningNode.path("partOfSpeech").asText();
                    messageBuilder.append("- (").append(partOfSpeech).append(")\n");

                    // Извлекаем определения для каждой части речи
                    JsonNode definitionsNode = meaningNode.path("definitions");
                    for (JsonNode definitionNode : definitionsNode) {
                        String definition = definitionNode.path("definition").asText();
                        messageBuilder.append("  • ").append(definition).append("\n");
                    }
                }
            }

            // Отправляем сообщение с определениями слова
            sendMessage(chatId, messageBuilder.toString());
        } catch (Exception e) {
            log.error("Ошибка при запросе определения слова '" + word + "': " + e.getMessage(), e);
            sendMessage(chatId, "Произошла ошибка при запросе определения слова '" + word + "'");
        }
    }
}
