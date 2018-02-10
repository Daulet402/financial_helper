package kz.techsolutions.bot.service.impl;

import kz.techsolutions.bot.api.CurrencyDaoService;
import kz.techsolutions.bot.api.dto.CurrencyDTO;
import kz.techsolutions.bot.mapper.CurrencyDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CurrencyDaoServiceImpl implements CurrencyDaoService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Cacheable("currencies")
    public List<CurrencyDTO> getCurrencyDtoList() {
        return jdbcTemplate.query("SELECT * FROM FC.CURRENCIES", new CurrencyDTOMapper());
    }
}