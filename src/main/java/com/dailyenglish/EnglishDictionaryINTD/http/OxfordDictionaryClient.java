package com.dailyenglish.EnglishDictionaryINTD.http;

import feign.HeaderMap;
import feign.Headers;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "oxford-dictionary",
        url = "https://od-api-sandbox.oxforddictionaries.com/api/v2")
public interface OxfordDictionaryClient {
        @GetMapping("/translations/{sourceLang}/{targetLang}/{wordId}")
        ResponseEntity<String> getTranslate(@PathVariable String sourceLang,
                                            @PathVariable String targetLang,
                                            @PathVariable String wordId,
                                            @RequestHeader("app_id") String appId,
                                            @RequestHeader("app_key") String appKey);

}
