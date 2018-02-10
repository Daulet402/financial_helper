package kz.techsolutions.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LanguageDTO {
    private Long id;
    private String nameRu;
    private Language langCode;
}