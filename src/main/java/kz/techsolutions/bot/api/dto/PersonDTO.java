package kz.techsolutions.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PersonDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private Language language;
    private CurrencyDTO currencyDTO;
}