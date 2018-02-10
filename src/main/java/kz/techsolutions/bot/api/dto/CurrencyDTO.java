package kz.techsolutions.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CurrencyDTO {
    private Long id;
    private CurrencyCode code;
    private String nameRu;
    private String nameEn;
    private String nameKk;
    private String sign;

    public enum CurrencyCode {
        KZT,
        RUB,
        USD,
        EUR;

        public static CurrencyCode instance(String name) {
            for (CurrencyCode code : CurrencyCode.values())
                if (Objects.equals(code.name(), name))
                    return code;

            return null;
        }
    }
}