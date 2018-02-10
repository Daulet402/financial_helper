package kz.techsolutions.bot.service.impl;

import kz.techsolutions.bot.api.CategoryDaoService;
import kz.techsolutions.bot.api.dto.CategoryDTO;
import kz.techsolutions.bot.api.dto.SubcategoryDTO;
import kz.techsolutions.bot.api.mapper.CategoryDTOMapper;
import kz.techsolutions.bot.api.mapper.SubcategoryDTOMapper;
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
        return jdbcTemplate.query("SELECT * FROM FC.CATEGORY", new CategoryDTOMapper());
    }

    @Override
    public void addCategory(CategoryDTO categoryDTO) {

    }

    @Override
    @Cacheable("allSubcategories")
    public List<SubcategoryDTO> getAllSubcategories() {
        return jdbcTemplate.query("SELECT * FROM FC.SUBCATEGORY", new SubcategoryDTOMapper());
    }

    @Override
    public void addSubcategoryDTO(SubcategoryDTO subcategoryDTO) {
        jdbcTemplate.update(
                "INSERT INTO FC.SUBCATEGORY(NAME,CATEGORYID,NAME_RU,NAME_EN,NAME_KK) VALUES (?,?,?,?,?)",
                new Object[]{
                        subcategoryDTO.getSubcategory().name(),
                        subcategoryDTO.getCategory().getId(),
                        subcategoryDTO.getNameRu(),
                        subcategoryDTO.getNameEn(),
                        subcategoryDTO.getNameKk()
                }
        );
    }
}