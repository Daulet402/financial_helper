package kz.techsolutions.bot.utils;

import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class DateTimeUtils {

    public static Timestamp toTimestamp(LocalDateTime localDateTime) {
        return Objects.nonNull(localDateTime) ? Timestamp.valueOf(localDateTime) : null;
    }

    public static LocalDateTime fromTimestamp(Timestamp timestamp) {
        return Objects.nonNull(timestamp) ? timestamp.toLocalDateTime() : null;
    }

    public static LocalDateTime parseLocalDateTime(String toParse, String pattern) {
        if (StringUtils.isEmpty(toParse) || StringUtils.isEmpty(pattern))
            return null;

        try {
            return LocalDateTime.of(LocalDate.parse(toParse.replaceAll(" ", ""), DateTimeFormatter.ofPattern(pattern)), LocalTime.MIN);
        } catch (DateTimeParseException e) {
        }
        return null;
    }
}