package kz.techsolutions.bot.utils;

import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;

public class BotCollectionUtils {

    public static <T> T getKeyFromSingleMap(LinkedHashMap<T, Object> command) {
        return !CollectionUtils.isEmpty(command) ? command.keySet().stream().findFirst().get() : null;
    }
}