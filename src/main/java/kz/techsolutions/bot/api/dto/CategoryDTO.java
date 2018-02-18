package kz.techsolutions.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDTO {
    private Long id;
    private String nameRu;
    private String nameEn;
    private String nameKk;
    private Category category;
    private List<SubcategoryDTO> subcategoryDtoList;
}