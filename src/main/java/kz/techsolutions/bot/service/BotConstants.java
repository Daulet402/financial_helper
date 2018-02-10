package kz.techsolutions.bot.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Getter
@PropertySource("classpath:bot.properties")
public class BotConstants {

    @Value("${startText}")
    private String startText;

    @Value("${langRu}")
    private String langRu;

    @Value("${langEn}")
    private String langEn;

    @Value("${langKk}")
    private String langKk;

    @Value("${dateTimePattern}")
    private String dateTimePattern;


}