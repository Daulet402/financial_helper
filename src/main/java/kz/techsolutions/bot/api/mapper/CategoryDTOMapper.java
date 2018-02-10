package kz.techsolutions.bot.api.mapper;

import kz.techsolutions.bot.api.dto.Category;
import kz.techsolutions.bot.api.dto.CategoryDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryDTOMapper implements RowMapper {

    @Override
    public CategoryDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(rs.getLong("ID"));
        categoryDTO.setNameRu(rs.getString("NAME_RU"));
        categoryDTO.setNameEn(rs.getString("NAME_EN"));
        categoryDTO.setNameKk(rs.getString("NAME_KK"));
        categoryDTO.setCategory(Category.instance(rs.getString("NAME")));
        // TODO: 12/15/17 get all subcategories in list
        return categoryDTO;
    }

}