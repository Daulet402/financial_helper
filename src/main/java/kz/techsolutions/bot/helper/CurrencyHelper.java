package kz.techsolutions.bot.helper;

import kz.techsolutions.bot.api.dto.CurrencyDTO;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

public class CurrencyHelper {

    public static CurrencyDTO findCurrencyDTOById(List<CurrencyDTO> currencyDtoList, Long id) {
        if (CollectionUtils.isEmpty(currencyDtoList) || Objects.isNull(id))
            return null;

        return currencyDtoList
                .stream()
                .filter(currencyDTO -> Objects.equals(currencyDTO.getId(), id))
                .findFirst()
                .orElse(null);
    }
}