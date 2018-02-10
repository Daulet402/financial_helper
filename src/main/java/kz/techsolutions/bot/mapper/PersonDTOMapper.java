package kz.techsolutions.bot.mapper;


import kz.techsolutions.bot.api.dto.CurrencyDTO;
import kz.techsolutions.bot.api.dto.Language;
import kz.techsolutions.bot.api.dto.PersonDTO;
import kz.techsolutions.bot.helper.CurrencyHelper;
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
public class PersonDTOMapper implements RowMapper {

    private List<CurrencyDTO> currencyDtoList;

    @Override
    public PersonDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        PersonDTO personDTO = new PersonDTO();
        CurrencyDTO currencyDTO = CurrencyHelper.findCurrencyDTOById(currencyDtoList, rs.getLong("CURRENCY_ID"));
        personDTO.setId(rs.getLong("ID"));
        personDTO.setFirstName(rs.getString("NAME"));
        personDTO.setLastName(rs.getString("SURNAME"));
        personDTO.setUsername(rs.getString("USERNAME"));
        personDTO.setLanguage(Language.instance(rs.getLong("LANGID")));
        personDTO.setCurrencyDTO(currencyDTO);
        return personDTO;
    }
}