package kz.techsolutions.bot.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

public class DateTimeUtils {

    public static Timestamp toTimestamp(LocalDateTime localDateTime) {
        return Objects.nonNull(localDateTime) ? Timestamp.valueOf(localDateTime) : null;
    }

    public static LocalDateTime fromTimestamp(Timestamp timestamp) {
        return Objects.nonNull(timestamp) ? timestamp.toLocalDateTime() : null;
    }
}