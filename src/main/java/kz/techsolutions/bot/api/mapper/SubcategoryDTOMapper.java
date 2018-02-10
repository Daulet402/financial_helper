package kz.techsolutions.bot.api.mapper;

import kz.techsolutions.bot.api.dto.Category;
import kz.techsolutions.bot.api.dto.Subcategory;
import kz.techsolutions.bot.api.dto.SubcategoryDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SubcategoryDTOMapper implements RowMapper {

    @Override
    public SubcategoryDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        SubcategoryDTO subcategoryDTO = new SubcategoryDTO();
        subcategoryDTO.setId(rs.getLong("ID"));
        subcategoryDTO.setNameRu(rs.getString("NAME_RU"));
        subcategoryDTO.setNameEn(rs.getString("NAME_EN"));
        subcategoryDTO.setNameKk(rs.getString("NAME_KK"));
        subcategoryDTO.setCategory(Category.instance(rs.getLong("CATEGORYID")));
        subcategoryDTO.setSubcategory(Subcategory.instance(rs.getString("NAME")));
        return subcategoryDTO;
    }
}