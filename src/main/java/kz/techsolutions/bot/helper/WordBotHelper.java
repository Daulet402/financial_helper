package kz.techsolutions.bot.helper;

import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

public class WordBotHelper {
    public static KeyboardRow setOneButtonRow(String text) {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(text);
        return keyboardRow;
    }
}