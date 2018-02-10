package kz.techsolutions.bot.app.configuration;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.TelegramBotsApi;

@Configuration
public class BotConfiguration {

    @Bean
    public TelegramBotsApi telegramBotsApi() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        return telegramBotsApi;
    }

    @Bean
    public Logger logger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass());
    }
}