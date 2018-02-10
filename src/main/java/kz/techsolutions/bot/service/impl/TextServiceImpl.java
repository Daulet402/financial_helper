package kz.techsolutions.bot.service.impl;

import com.google.common.collect.Maps;
import kz.techsolutions.bot.api.TextService;
import kz.techsolutions.bot.api.dto.Text;
import kz.techsolutions.bot.api.dto.TextDTO;
import kz.techsolutions.bot.api.mapper.TextDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TextServiceImpl implements TextService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Cacheable("textDtoList")
    public List<TextDTO> getAllTextDTOs() {
        return jdbcTemplate.query("SELECT * FROM FC.TEXTS", new TextDTOMapper());
    }

    @Override
    @Cacheable("textDto")
    public TextDTO getTextDTOByKey(String key) {
        return (TextDTO) jdbcTemplate.queryForObject(
                "SELECT * FROM FC.TEXTS WHERE KEY=?",
                new Object[]{key},
                new TextDTOMapper());
    }

    @Override
    @Cacheable("textDtoMap")
    public Map<Text, TextDTO> getTextDtoMap() {
        Map<Text, TextDTO> textDTOMap = Maps.newHashMap();
        List<TextDTO> textDTOS = getAllTextDTOs();
        textDTOS.forEach(textDTO -> {
            textDTOMap.put(textDTO.getKey(), textDTO);
        });
        return textDTOMap;
    }
}