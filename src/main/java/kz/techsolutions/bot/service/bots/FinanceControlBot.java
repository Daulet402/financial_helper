package kz.techsolutions.bot.service.bots;

import kz.techsolutions.bot.api.BotService;
import kz.techsolutions.bot.api.exception.BotAppException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.Objects;

@Component("fcBot")
@PropertySource("classpath:secret-info.properties")
public class FinanceControlBot extends TelegramLongPollingBot {

    @Value("${bot.token:''}")
    private String botToken;

    @Value("${bot.username:''}")
    private String botUsername;

    @Autowired
    private Logger log;

    @Autowired
    private BotService botService;

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = null;
        try {
            sendMessage = botService.processAndGetSendMessage(update);
        } catch (BotAppException e) {
            log.error(e.getMessage());
            if (Objects.nonNull(update.getMessage()) && Objects.nonNull(update.getMessage().getChatId())) {
                if (Objects.isNull(sendMessage)) {
                    sendMessage = new SendMessage();
                }
                sendMessage.setChatId(update.getMessage().getChatId());
                sendMessage.setText("Something went wrong. Please try later");
            }
        }
        try {
            if (Objects.nonNull(sendMessage)) {
                this.sendApiMethod(sendMessage);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}