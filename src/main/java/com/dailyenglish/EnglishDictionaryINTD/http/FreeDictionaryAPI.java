package com.dailyenglish.EnglishDictionaryINTD.http;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "FreeDictionaryAPI",
        url = "https://api.dictionaryapi.dev/api/v2/entries/en/")
public interface FreeDictionaryAPI {
        @GetMapping("{word}")
        ResponseEntity<String> getDefinition(@PathVariable String word);

}
