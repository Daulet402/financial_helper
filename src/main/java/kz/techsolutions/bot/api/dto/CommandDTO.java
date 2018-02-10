package kz.techsolutions.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommandDTO {
    private String command;
    private CommandType commandType;
}