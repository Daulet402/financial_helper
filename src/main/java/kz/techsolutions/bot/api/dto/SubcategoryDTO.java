package kz.techsolutions.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SubcategoryDTO {
    private Long id;
    private Subcategory subcategory;
    private Category category;
    private String nameRu;
    private String nameEn;
    private String nameKk;
    private String color;
}