package com.dailyenglish.EnglishDictionaryINTD.http;

import feign.HeaderMap;
import feign.Headers;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;
@FeignClient(name = "oxford-dictionary",
        url = "https://od-api-sandbox.oxforddictionaries.com/api/v2")
@Headers({"app_id: b246b661", "app_key: fc6a1a1bb6104e169d5498296c174fd7"})
public interface OxfordDictionaryClient {
        @GetMapping("/translations/{sourceLang}/{targetLang}/{wordId}")
        ResponseEntity<String> getTranslate(@PathVariable String sourceLang, @PathVariable String targetLang,
                                            @PathVariable String wordId);

}
