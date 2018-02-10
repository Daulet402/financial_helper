package kz.techsolutions.bot.api.dto;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public enum Subcategory {
    HOT_ROOM(10l),
    MASSAGE(11l),
    REST_OTHER(12l),
    COSMETICS_PURCHASE(13l),
    COSMETICS_OTHER(14l),
    CLOTHES_PURCHASE(15l),
    RESTORATION(16l),
    CLOTHES_OTHER(17l),
    CAR_REPAIR(18l),
    FUEL(19l),
    OIL_REPLACEMENT(20l),
    CAR_OTHER(21l),
    UTILITIES(22l),
    FOOD(23l),
    RENT(24l),
    HOUSE_REPAIR(25l),
    HOUSE_OTHER(26l),
    BIRTHDAY(27l),
    FUNERAL(28l),
    MEETING(29l),
    WEDDING(30l),
    CHARITY(31l),
    EVENT_OTHER(32l),
    ICE_SKATING(33l),
    SKIING(34l),
    PING_PONG(35l),
    HOBBY_OTHER(36l),
    CROSSFIT(37l),
    BOX(38l),
    RUNNING(39l),
    FOOTBALL(40l),
    BASKETBALL(41l),
    VOLLEYBALL(42l),
    SPORT_OTHER(43l),
    CINEMA(44l),
    THEATRE(45l),
    MUSEUM(46l),
    KARAOKE(47l),
    BOOKS(48l),
    ENTERTAINMENT_OTHER(49l),
    DENTIST(50l),
    HOSPITAL(51l),
    PHARMACY(52l),
    HEALTH_OTHER(53l),
    BREAKFAST(54l),
    LUNCH(55l),
    DINNER(56l),
    SNACK(57l),
    FOOD_OUT_OTHER(58l),
    TAXI(59l),
    TROLLEY_BUS(60l),
    PLANE(61l),
    BUS(62l),
    BICYCLE(63l),
    TRAIN(64l),
    TRANSPORT_OTHER(65l),
    OTHER(66l),
    MORTGAGE(68l),
    CAR_LOAN(69l),
    MONTH_LOAN(70l),
    DEBT(71l),
    CORPORATE(72l),
    HANG_OUT(73l),
    WORK_EVENT(67l);

    @Getter
    private Long id;

    Subcategory(Long id) {
        this.id = id;
    }


    public static Long getId(Subcategory subcategory) {
        return Objects.nonNull(subcategory) ? subcategory.getId() : null;
    }

    public static Subcategory instance(Long id) {
        for (Subcategory subcategory : Subcategory.values()) {
            if (Objects.equals(subcategory.getId(), id)) {
                return subcategory;
            }
        }
        return null;
    }

    public static Subcategory instance(String str) {
        for (Subcategory subcategory : Subcategory.values()) {
            if (StringUtils.equalsIgnoreCase(subcategory.name(), str)) {
                return subcategory;
            }
        }
        return null;
    }
}