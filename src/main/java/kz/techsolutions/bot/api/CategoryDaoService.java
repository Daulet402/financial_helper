package kz.techsolutions.bot.api;

import kz.techsolutions.bot.api.dto.CategoryDTO;
import kz.techsolutions.bot.api.dto.SubcategoryDTO;

import java.util.List;

public interface CategoryDaoService {

    List<CategoryDTO> getAllCategories();

    List<SubcategoryDTO> getAllSubcategories();

    void addSubcategoryDTO(SubcategoryDTO subcategoryDTO);
}