package kz.techsolutions.bot.api.mapper;

import kz.techsolutions.bot.api.dto.CategoryDTO;
import kz.techsolutions.bot.api.dto.FinancialControlDTO;
import kz.techsolutions.bot.helper.CategoryHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FinancialControlCategoryDTOMapper implements RowMapper {

    private List<CategoryDTO> categoryDtoList;

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        FinancialControlDTO financialControlDTO = new FinancialControlDTO();
        financialControlDTO.setAmount(rs.getDouble("sum"));
        financialControlDTO.setCategoryDTO(
                CategoryHelper.findCategoryDtoById(categoryDtoList, rs.getLong("categoryId"))
        );
        return financialControlDTO;
    }
}