package kz.techsolutions.bot.mapper;

import kz.techsolutions.bot.api.dto.FinancialControlDTO;
import kz.techsolutions.bot.api.dto.SubcategoryDTO;
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
public class FinancialControlSubcategoryDTOMapper implements RowMapper {

    private List<SubcategoryDTO> subcategoryDtoList;

    @Override
    public FinancialControlDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        FinancialControlDTO financialControlDTO = new FinancialControlDTO();
        financialControlDTO.setAmount(rs.getDouble("sum"));
        financialControlDTO.setSubcategoryDTO(
                CategoryHelper.findSubcategoryDtoById(subcategoryDtoList, rs.getLong("subcategoryid"))
        );
        return financialControlDTO;
    }
}