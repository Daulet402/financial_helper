package kz.techsolutions.bot.helper;

import kz.techsolutions.bot.api.dto.CategoryDTO;
import kz.techsolutions.bot.api.dto.SubcategoryDTO;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

public class CategoryHelper {

    public static CategoryDTO findCategoryDtoById(List<CategoryDTO> categoryDtoList, Long id) {
        if (CollectionUtils.isEmpty(categoryDtoList) || Objects.isNull(id))
            return null;

        return categoryDtoList
                .stream()
                .filter(categoryDTO -> Objects.equals(categoryDTO.getId(), id))
                .findFirst()
                .orElse(null);
    }

    public static SubcategoryDTO findSubcategoryDtoById(List<SubcategoryDTO> subcategoryDtoList, Long id) {
        if (CollectionUtils.isEmpty(subcategoryDtoList) || Objects.isNull(id))
            return null;

        return subcategoryDtoList
                .stream()
                .filter(subcategoryDTO -> Objects.equals(subcategoryDTO.getId(), id))
                .findFirst()
                .orElse(null);
    }
}