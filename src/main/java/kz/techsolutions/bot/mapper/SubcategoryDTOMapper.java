package kz.techsolutions.bot.mapper;

import kz.techsolutions.bot.api.dto.CategoryDTO;
import kz.techsolutions.bot.api.dto.Subcategory;
import kz.techsolutions.bot.api.dto.SubcategoryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SubcategoryDTOMapper implements RowMapper {

    private List<CategoryDTO> categoryDtoList;

    @Override
    public SubcategoryDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        SubcategoryDTO subcategoryDTO = new SubcategoryDTO();
        subcategoryDTO.setId(rs.getLong("ID"));
        subcategoryDTO.setNameRu(rs.getString("NAME_RU"));
        subcategoryDTO.setNameEn(rs.getString("NAME_EN"));
        subcategoryDTO.setNameKk(rs.getString("NAME_KK"));
        if (!CollectionUtils.isEmpty(categoryDtoList)) {
            Long categoryId = rs.getLong("CATEGORYID");
            CategoryDTO categoryDto = categoryDtoList
                    .stream()
                    .filter(c -> Objects.equals(c.getId(), categoryId))
                    .findFirst()
                    .orElse(null);
            subcategoryDTO.setCategoryDTO(categoryDto);
        }
        subcategoryDTO.setSubcategory(Subcategory.instance(rs.getString("NAME")));
        return subcategoryDTO;
    }
}