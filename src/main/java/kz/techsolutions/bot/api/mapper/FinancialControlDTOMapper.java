package kz.techsolutions.bot.api.mapper;

import kz.techsolutions.bot.api.dto.FinancialControlDTO;
import kz.techsolutions.bot.api.dto.SubcategoryDTO;
import kz.techsolutions.bot.helper.CategoryHelper;
import kz.techsolutions.bot.utils.DateTimeUtils;
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
public class FinancialControlDTOMapper implements RowMapper {

    private PersonDTOMapper personDTOMapper;
    private List<SubcategoryDTO> subcategoryDtoList;

    @Override
    public FinancialControlDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        FinancialControlDTO financialControlDTO = new FinancialControlDTO();
        financialControlDTO.setId(rs.getLong("FC_ID"));
        financialControlDTO.setAmount(rs.getDouble("AMOUNT"));
        financialControlDTO.setPersonDTO(personDTOMapper.mapRow(rs, rowNum));
        financialControlDTO.setSubcategoryDTO(
                CategoryHelper.findSubcategoryDtoById(subcategoryDtoList, rs.getLong("SUBCATEGORYID"))
        );
        financialControlDTO.setEventTime(DateTimeUtils.fromTimestamp(rs.getTimestamp("EVENTTIME")));
        financialControlDTO.setGeneratedTime(DateTimeUtils.fromTimestamp(rs.getTimestamp("INSERTEDTIME")));
        return financialControlDTO;
    }
}