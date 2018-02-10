package kz.techsolutions.bot.helper;

import kz.techsolutions.bot.api.dto.CategoryDTO;
import kz.techsolutions.bot.api.dto.PersonDTO;
import kz.techsolutions.bot.api.dto.SubcategoryDTO;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CategoryHelper {

    public static CategoryDTO findCategoryDtoById(List<CategoryDTO> categoryDtoList, Long id) {
        if (CollectionUtils.isEmpty(categoryDtoList))
            return null;

        return categoryDtoList
                .stream()
                .filter(categoryDTO -> Objects.equals(categoryDTO.getId(), id))
                .findFirst()
                .orElse(null);
    }

    public static SubcategoryDTO findSubcategoryDtoById(List<SubcategoryDTO> subcategoryDtoList, Long id) {
        if (CollectionUtils.isEmpty(subcategoryDtoList))
            return null;

        return subcategoryDtoList
                .stream()
                .filter(subcategoryDTO -> Objects.equals(subcategoryDTO.getId(), id))
                .findFirst()
                .orElse(null);
    }

    public static Optional<CategoryDTO> getCategoryByUserInputText(List<CategoryDTO> categoryDtoList, PersonDTO personDTO, String text) {
        if (CollectionUtils.isEmpty(categoryDtoList) || Objects.isNull(personDTO))
            return Optional.empty();

        return categoryDtoList
                .stream()
                .filter(c -> Objects.equals(LangHelper.getCategoryTextByLang(personDTO.getLanguage(), c), text))
                .findFirst();
    }

    public static Optional<SubcategoryDTO> getSubcategoryByUserInputText(List<SubcategoryDTO> subcategoryDtoList,
                                                                         PersonDTO personDTO,
                                                                         String text) {
        if (CollectionUtils.isEmpty(subcategoryDtoList) || Objects.isNull(personDTO))
            return Optional.empty();

        return subcategoryDtoList
                .stream()
                .filter(s -> Objects.equals(LangHelper.getSubcategoryTextByLang(personDTO.getLanguage(), s), text))
                .findFirst();
    }
}