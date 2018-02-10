package kz.techsolutions.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TextDTO {
    private Text key;
    private String textRu;
    private String textEn;
    private String textKk;
}

