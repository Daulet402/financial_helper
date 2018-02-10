package kz.techsolutions.bot.helper;

import kz.techsolutions.bot.api.dto.CategoryDTO;
import kz.techsolutions.bot.api.dto.Language;
import kz.techsolutions.bot.api.dto.SubcategoryDTO;
import kz.techsolutions.bot.api.dto.TextDTO;

import java.util.Objects;

public class LangHelper {


    public static String getCategoryTextByLang(Language language, CategoryDTO categoryDTO) {
        if (Objects.isNull(categoryDTO))
            return "";

        String defaultResult = categoryDTO.getNameRu();
        if (Objects.isNull(language))
            return defaultResult;

        switch (language) {
            case ENG:
                return categoryDTO.getNameEn();
            case KK:
                return categoryDTO.getNameKk();
        }
        return defaultResult;
    }

    public static String getSubcategoryTextByLang(Language language, SubcategoryDTO subcategoryDTO) {
        if (Objects.isNull(subcategoryDTO))
            return "";

        String defaultResult = subcategoryDTO.getNameRu();
        if (Objects.isNull(language))
            return defaultResult;

        switch (language) {
            case ENG:
                return subcategoryDTO.getNameEn();
            case KK:
                return subcategoryDTO.getNameKk();
        }
        return defaultResult;
    }

    public static String getTextByLang(Language language, TextDTO textDTO) {
        if (Objects.isNull(textDTO))
            return "";

        String defaultResult = textDTO.getTextRu();
        if (Objects.isNull(language))
            return defaultResult;

        switch (language) {
            case ENG:
                return textDTO.getTextEn();
            case KK:
                return textDTO.getTextKk();
        }
        return defaultResult;
    }
}