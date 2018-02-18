package kz.techsolutions.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedHashMap;

@AllArgsConstructor
@Data
public class UserSessionDTO {
    private CategoryDTO categoryDto;
    private LinkedHashMap<CommandType, Object> commands;

    public UserSessionDTO() {
        commands = new LinkedHashMap<>();
    }
}