package kz.techsolutions.bot.api.dto;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public enum Category {
    HOUSE(1l),
    CAR(2l),
    REST(3l),
    CLOTHES(4l),
    TRANSPORT(5l),
    FOOD_OUT(6l),
    HEALTH(7l),
    ENTERTAINMENT(8l),
    SPORT(9l),
    HOBBY(10l),
    EVENT(11l),
    COSMETICS(12l),
    WORK(13l),
    LOAN(14l),
    OTHER(79l),
    INVESTMENTS(15l);

    @Getter
    private Long id;

    Category(Long id) {
        this.id = id;
    }

    public static Category instance(Long id) {
        for (Category category : Category.values()) {
            if (Objects.equals(category.getId(), id)) {
                return category;
            }
        }
        return null;
    }

    public static Category instance(String str) {
        for (Category category : Category.values()) {
            if (StringUtils.equalsIgnoreCase(category.name(), str)) {
                return category;
            }
        }
        return null;
    }
}