package kz.techsolutions.bot.api;

import kz.techsolutions.bot.api.dto.CommandType;
import kz.techsolutions.bot.api.dto.UserSessionDTO;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;

public interface UserSessionService {

    LinkedHashMap<CommandType, Object> getFirstCommand(@NotNull String username);

    LinkedHashMap<CommandType, Object> getCommandByIndex(@NotNull String username, int index);

    LinkedHashMap<CommandType, Object> getLastCommand(@NotNull String username);

    LinkedHashMap<CommandType, Object> getAllCommand(@NotNull String username);

    Object getValueByKey(@NotNull String username, @NotNull CommandType key);

    void saveCommand(@NotNull String username, @NotNull CommandType commandType, @NotNull Object command);

    void clearCommands(@NotNull String username);

    UserSessionDTO getUserSession(@NotNull String username);

    void deleteCommand(@NotNull String username, @NotNull CommandType commandType, @NotNull Object command);

    void replaceCommand(@NotNull String username, @NotNull CommandType commandType, @NotNull Object newСommand);
}