package kz.techsolutions.bot.service.impl;

import kz.techsolutions.bot.api.UserSessionService;
import kz.techsolutions.bot.api.dto.CommandType;
import kz.techsolutions.bot.api.dto.UserSessionDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class UserSessionServiceImpl implements UserSessionService {

    private Map<String, UserSessionDTO> userSessionMap;

    @PostConstruct
    public void init() {
        userSessionMap = new HashMap<>();
    }

    @Override
    public LinkedHashMap<CommandType, Object> getFirstCommand(String username) {
        return getCommandByIndex(username, 0);
    }

    @Override
    public LinkedHashMap<CommandType, Object> getCommandByIndex(String username, int index) {
        UserSessionDTO userSession = getUserSessionInternal(username);
        try {
            if (!CollectionUtils.isEmpty(userSession.getCommands())) {
                CommandType key = (CommandType) userSession.getCommands().keySet().toArray()[index];
                return new LinkedHashMap<>(Collections.singletonMap(key, userSession.getCommands().get(key)));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return null;
    }

    @Override
    public LinkedHashMap<CommandType, Object> getLastCommand(String username) {
        UserSessionDTO userSession = getUserSessionInternal(username);
        if (CollectionUtils.isEmpty(userSession.getCommands())) {
            return null;
        }
        return getCommandByIndex(username, userSession.getCommands().size() - 1);
    }

    @Override
    public LinkedHashMap<CommandType, Object> getAllCommand(String username) {
        return getUserSessionInternal(username).getCommands();
    }

    @Override
    public Object getValueByKey(String username, CommandType key) {
        UserSessionDTO userSession = getUserSessionInternal(username);
        return !CollectionUtils.isEmpty(userSession.getCommands()) ? userSession.getCommands().get(key) : null;
    }

    @Override
    public void clearCommands(String username) {
        UserSessionDTO userSession = getUserSessionInternal(username);
        userSession.getCommands().clear();
        userSessionMap.put(username, userSession);
    }

    @Override
    public UserSessionDTO getUserSession(String username) {
        return getUserSessionInternal(username);
    }

    @Override
    public void saveCommand(String username, CommandType commandType, Object command) {
        UserSessionDTO userSession = getUserSessionInternal(username);
        userSession.getCommands().put(commandType, command);
        userSessionMap.put(username, userSession);
    }

    @Override
    public void deleteCommand(String username, CommandType commandType, Object command) {
        UserSessionDTO userSession = getUserSessionInternal(username);
        userSession.getCommands().entrySet().removeIf(c -> Objects.equals(c.getKey(), commandType)
                && Objects.equals(c.getValue(), command));
        userSessionMap.put(username, userSession);
    }

    @Override
    public void replaceCommand(String username, CommandType commandType, Object newСommand) {
        Object currentValue = getValueByKey(username, commandType);
        deleteCommand(username, commandType, currentValue);
        saveCommand(username, commandType, newСommand);
    }

    private UserSessionDTO getUserSessionInternal(String username) {
        UserSessionDTO userSession = userSessionMap.get(username);
        return Objects.nonNull(userSession) ? userSession : new UserSessionDTO();
    }
}