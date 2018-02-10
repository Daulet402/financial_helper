package kz.techsolutions.bot.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

@Component
public class BotLoader {

    @Autowired
    private Logger log;

    @Autowired
    private TelegramBotsApi telegramBotsApi;

    @Autowired
    @Qualifier("fcBot")
    private TelegramLongPollingBot wordBot;

    public void loadBots() {
        try {
            telegramBotsApi.registerBot(wordBot);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}