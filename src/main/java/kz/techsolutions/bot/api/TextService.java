package kz.techsolutions.bot.api;

import kz.techsolutions.bot.api.dto.Text;
import kz.techsolutions.bot.api.dto.TextDTO;

import java.util.List;
import java.util.Map;

public interface TextService {

    List<TextDTO> getAllTextDTOs();

    TextDTO getTextDTOByKey(String key);

    Map<Text, TextDTO> getTextDtoMap();
}