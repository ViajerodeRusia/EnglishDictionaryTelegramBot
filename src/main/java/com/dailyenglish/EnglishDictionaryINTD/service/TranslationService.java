package com.dailyenglish.EnglishDictionaryINTD.service;

import com.cloudmersive.client.LanguageTranslationApi;
import com.cloudmersive.client.invoker.ApiClient;
import com.cloudmersive.client.invoker.ApiException;
import com.cloudmersive.client.model.LanguageTranslationRequest;
import com.cloudmersive.client.model.LanguageTranslationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class TranslationService {
    private final LanguageTranslationApi apiInstance;

    @Autowired
    public TranslationService(ApiClient apiClient) {
        this.apiInstance = new LanguageTranslationApi(apiClient);
    }

    public String translateEngToRus(String sourceText) {
        LanguageTranslationRequest input = new LanguageTranslationRequest();
        input.setTextToTranslate(sourceText);
        try {
            LanguageTranslationResponse response = apiInstance.languageTranslationTranslateEngToRus(input);
            log.info("Translated response for the word - " + sourceText + " is " + response.getTranslatedTextResult());
            return response.getTranslatedTextResult();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        }
    }
}
