package kz.techsolutions.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedHashMap;

@AllArgsConstructor
@Data
public class UserSessionDTO {
    //private String username;
    //private Double amount;
    //private LinkedList<CommandDTO> commands;
    private LinkedHashMap<CommandType, Object> commands;
    //private CommandDTO firtsCommand;
    // private CommandDTO command;
    // private CommandDTO lastCommand;

    public UserSessionDTO() {
        commands = new LinkedHashMap<>();
    }
}