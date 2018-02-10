package kz.techsolutions.bot.api.dto;

import lombok.Getter;

import java.util.Objects;

public enum Language {
    RUS(1l),
    ENG(2l),
    KK(3l);

    @Getter
    private Long id;


    Language(Long id) {
        this.id = id;
    }

    public static Language instance(Long id) {
        for (Language language : Language.values()) {
            if (Objects.equals(language.getId(), id)) {
                return language;
            }
        }
        return null;
    }

    public static Long getId(Language language) {
        return Objects.nonNull(language) ? language.getId() : null;
    }

}