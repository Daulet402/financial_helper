package kz.techsolutions.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FinancialControlDTO {
    private Long id;
    private SubcategoryDTO subcategoryDTO;
    private CategoryDTO categoryDTO;
    private LocalDateTime eventTime;
    private LocalDateTime generatedTime;
    private Double amount;
    private PersonDTO personDTO;
    private String comment;

    @Override
    public String toString() {
        return "FinancialControlDTO{" +
                "subcategoryDTO=" + (Objects.nonNull(subcategoryDTO) ? subcategoryDTO.getNameRu() : null) +
                ", categoryDTO=" + (Objects.nonNull(categoryDTO) ? categoryDTO.getNameRu() : null) +
                ", eventTime=" + eventTime +
                ", amount=" + amount +
                "}\n";
    }
}