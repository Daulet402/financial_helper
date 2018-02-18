package kz.techsolutions.bot.mapper;

import kz.techsolutions.bot.api.dto.Category;
import kz.techsolutions.bot.api.dto.CategoryDTO;
import kz.techsolutions.bot.api.dto.Subcategory;
import kz.techsolutions.bot.api.dto.SubcategoryDTO;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryResultSetExtractor implements ResultSetExtractor {
    @Override
    public List<CategoryDTO> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, CategoryDTO> categoryDtoMap = new HashMap<>();
        while (rs.next()) {
            Long categoryId = rs.getLong("CATEGORY_ID");
            CategoryDTO categoryDto = categoryDtoMap.get(categoryId);
            if (categoryDto == null) {
                categoryDto = new CategoryDTO();
                categoryDto.setId(categoryId);
                categoryDto.setNameRu(rs.getString("CATEGORY_NAME_RU"));
                categoryDto.setNameEn(rs.getString("CATEGORY_NAME_EN"));
                categoryDto.setNameKk(rs.getString("CATEGORY_NAME_KK"));
                categoryDto.setCategory(Category.instance(rs.getString("CATEGORY_NAME")));
                categoryDtoMap.put(categoryId, categoryDto);
            }
            SubcategoryDTO subcategoryDto = new SubcategoryDTO();
            subcategoryDto.setId(rs.getLong("SUBCATEGORY_ID"));
            subcategoryDto.setNameRu(rs.getString("SUBCATEGORY_NAME_RU"));
            subcategoryDto.setNameEn(rs.getString("SUBCATEGORY_NAME_EN"));
            subcategoryDto.setNameKk(rs.getString("SUBCATEGORY_NAME_KK"));
            subcategoryDto.setCategoryDTO(categoryDto);
            subcategoryDto.setSubcategory(Subcategory.instance(rs.getString("SUBCATEGORY_NAME")));
            List<SubcategoryDTO> subcategoryDtoList = categoryDto.getSubcategoryDtoList();
            if (subcategoryDtoList == null) {
                subcategoryDtoList = new ArrayList<>();
                categoryDto.setSubcategoryDtoList(subcategoryDtoList);
            }
            subcategoryDtoList.add(subcategoryDto);
        }
        return new ArrayList<>(categoryDtoMap.values());
    }
}