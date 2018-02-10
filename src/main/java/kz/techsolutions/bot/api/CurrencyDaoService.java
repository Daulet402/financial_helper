package kz.techsolutions.bot.api;

import kz.techsolutions.bot.api.dto.CurrencyDTO;

import java.util.List;

public interface CurrencyDaoService {

    List<CurrencyDTO> getCurrencyDtoList();
}