package kz.techsolutions.bot.mapper;

import kz.techsolutions.bot.api.dto.CurrencyDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencyDTOMapper implements RowMapper {

    @Override
    public CurrencyDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        CurrencyDTO currencyDTO = new CurrencyDTO();
        currencyDTO.setId(rs.getLong("ID"));
        currencyDTO.setSign(rs.getString("SIGN"));
        currencyDTO.setNameRu(rs.getString("NAME_RU"));
        currencyDTO.setNameEn(rs.getString("NAME_EN"));
        currencyDTO.setNameKk(rs.getString("NAME_KK"));
        currencyDTO.setCode(CurrencyDTO.CurrencyCode.instance(rs.getString("CODE")));
        return currencyDTO;
    }
}