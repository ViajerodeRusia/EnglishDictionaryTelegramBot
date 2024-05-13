package com.dailyenglish.EnglishDictionaryINTD.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.properties") //указать где свойства, которые будут счит через value
public class EnglishDictionaryConfiguration {
    @Value("${bot.name}")
    String botName;
    @Value("${bot.token}")
    String token;
}
