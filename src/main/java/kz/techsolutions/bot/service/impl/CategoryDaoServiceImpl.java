package kz.techsolutions.bot.service.impl;

import kz.techsolutions.bot.api.CategoryDaoService;
import kz.techsolutions.bot.api.dto.CategoryDTO;
import kz.techsolutions.bot.api.dto.SubcategoryDTO;
import kz.techsolutions.bot.mapper.CategoryResultSetExtractor;
import kz.techsolutions.bot.mapper.SubcategoryDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class CategoryDaoServiceImpl implements CategoryDaoService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Cacheable("allCategories")
    public List<CategoryDTO> getAllCategories() {
        return (List<CategoryDTO>) jdbcTemplate.query("SELECT " +
                "c.id as category_id, c.name as category_name, c.name_ru as category_name_ru, c.name_kk as category_name_kk, c.name_en as category_name_en, " +
                "s.id as subcategory_id, s.name as subcategory_name, s.name_ru as subcategory_name_ru, s.name_kk as subcategory_name_kk, s.name_en as subcategory_name_en " +
                "FROM FC.CATEGORY c " +
                "LEFT JOIN FC.SUBCATEGORY s " +
                "ON s.categoryId=c.id " +
                "ORDER BY category_id, subcategory_id ", new CategoryResultSetExtractor());
    }

    @Override
    @Cacheable("allSubcategories")
    public List<SubcategoryDTO> getAllSubcategories() {
        return jdbcTemplate.query("SELECT * FROM FC.SUBCATEGORY", new SubcategoryDTOMapper(getAllCategories()));
    }

    @Override
    public void addSubcategoryDTO(SubcategoryDTO subcategoryDTO) {
        jdbcTemplate.update(
                "INSERT INTO FC.SUBCATEGORY(NAME,CATEGORYID,NAME_RU,NAME_EN,NAME_KK) VALUES (?,?,?,?,?)",
                new Object[]{
                        subcategoryDTO.getSubcategory().name(),
                        Objects.nonNull(subcategoryDTO.getCategoryDTO()) ? subcategoryDTO.getCategoryDTO().getId() : null,
                        subcategoryDTO.getNameRu(),
                        subcategoryDTO.getNameEn(),
                        subcategoryDTO.getNameKk()
                }
        );
    }
}