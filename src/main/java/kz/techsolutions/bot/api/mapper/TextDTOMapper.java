package kz.techsolutions.bot.api.mapper;

import kz.techsolutions.bot.api.dto.Text;
import kz.techsolutions.bot.api.dto.TextDTO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TextDTOMapper implements RowMapper {

    @Override
    public TextDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        TextDTO textDTO = new TextDTO();
        textDTO.setKey(Text.instance(rs.getString("KEY")));
        textDTO.setTextRu(rs.getString("TEXT_RU"));
        textDTO.setTextEn(rs.getString("TEXT_EN"));
        textDTO.setTextKk(rs.getString("TEXT_KK"));
        return textDTO;
    }
}