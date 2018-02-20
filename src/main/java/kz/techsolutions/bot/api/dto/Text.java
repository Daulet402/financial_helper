package kz.techsolutions.bot.api.dto;

import java.util.Objects;

public enum Text {
    ADD_RECORD_MENU_ITEM,
    AMOUNT_SPENT_TIME_TEXT,
    BACK_TO_MENU_ITEM,
    LANGUAGE_MENU_ITEM,
    CHOOSE_CATEGORY_TEXT,
    CHOOSE_LANGUAGE_TEXT,
    CHOOSE_CURRENCY_TEXT,
    CHOOSE_SUBCATEGORY_TEXT,
    COMMANDS_NOT_SAVED_TEXT,
    INVALID_AMOUNT_ERROR_TEXT,
    INVALID_TIME_ERROR_TEXT,
    MAIN_MENU_TEXT,
    RECORD_SAVED_TEXT,
    SPENT_AMOUNT_TEXT,
    UNKNOWN_COMMAND_TEXT,
    CHANGES_SAVED_TEXT,
    INVALID_INPUT_ERROR_TEXT,
    RECORDS_MENU_ITEM,
    DETAILED_RECORDS_MENU_ITEM,
    CATEGORY_RECORDS_MENU_ITEM,
    SUBCATEGORY_RECORDS_MENU_ITEM,
    DATE_RANGE_FOR_RECORDS,
    SETTINGS_MENU_ITEM,
    CURRENCY_MENU_ITEM,
    NO_RECORDS_FOUND,
    TOTAL_TEXT,
    YES_TEXT,
    NO_TEXT,
    TYPE_COMMENT_TEXT,
    ADD_COMMENT_TEXT;

    public static Text instance(String key) {
        for (Text text : Text.values()) {
            if (Objects.equals(text.name(), key)) {
                return text;
            }
        }
        return null;
    }
}