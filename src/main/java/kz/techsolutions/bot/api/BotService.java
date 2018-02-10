package kz.techsolutions.bot.api;

import kz.techsolutions.bot.api.exception.BotAppException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import javax.validation.constraints.NotNull;

public interface BotService {
    SendMessage processAndGetSendMessage(@NotNull Update update) throws BotAppException;

    Long getChatId(@NotNull Update update) throws BotAppException;
}