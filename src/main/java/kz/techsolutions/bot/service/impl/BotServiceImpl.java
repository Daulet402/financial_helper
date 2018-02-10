package kz.techsolutions.bot.service.impl;

import kz.techsolutions.bot.api.*;
import kz.techsolutions.bot.api.dto.*;
import kz.techsolutions.bot.api.exception.BotAppException;
import kz.techsolutions.bot.service.BotConstants;
import kz.techsolutions.bot.helper.LangHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static kz.techsolutions.bot.helper.WordBotHelper.setOneButtonRow;

@Service
public class BotServiceImpl implements BotService {

    @Autowired
    private Logger log;

    @Autowired
    private TextService textService;

    @Autowired
    private BotConstants botConstants;

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private FinancialControlDaoService financialControlDaoService;

    @Autowired
    private CategoryDaoService categoryDaoService;

    @Autowired
    private CurrencyDaoService currencyDaoService;

    @Override
    public SendMessage processAndGetSendMessage(Update update) throws BotAppException {
        Long chatId = getChatId(update);
        String username = "";
        // TODO: 12/27/17 добавить возможность внесения коментарии
        // TODO: 2/10/18 Убрать меню, когда ожидается input текст
        try {
            if (Objects.nonNull(update.getMessage())) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                username = Objects.nonNull(update.getMessage().getFrom()) ?
                        update.getMessage().getFrom().getUserName() : null;
                String text = Objects.nonNull(update.getMessage().getText()) ? update.getMessage().getText() : "";
                log.info(String.format("text: %s from: %s", text, username));

                PersonDTO personDTO = financialControlDaoService.getPersonByUsername(username);
                if (Objects.isNull(personDTO)) {
                    personDTO = new PersonDTO();
                    personDTO.setUsername(username);
                    personDTO.setLanguage(Language.RUS);
                    financialControlDaoService.addPerson(personDTO);
                }

                Optional<CategoryDTO> categoryOptional = getCategoryByUserInputText(personDTO, text);
                Optional<SubcategoryDTO> subCategoryOptional = getSubcategoryByUserInputText(personDTO, text);
                LinkedHashMap<CommandType, Object> lastCommand = userSessionService.getLastCommand(username);
                LinkedHashMap<CommandType, Object> firstCommand = userSessionService.getFirstCommand(username);
                CommandType lastCommandKey = getKeyFromSingleMap(lastCommand);
                CommandType firstCommandKey = getKeyFromSingleMap(firstCommand);

                if (Objects.equals(botConstants.getStartText(), text)) {
                    sendMessage.setReplyMarkup(mainMenu(personDTO));
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.MAIN_MENU_TEXT)));
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.BACK_TO_MENU_ITEM)), text)) {
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.BACK_TO_MENU_ITEM)));
                    sendMessage.setReplyMarkup(mainMenu(personDTO));
                    userSessionService.clearCommands(username);
                } else if (Objects.equals(firstCommandKey, CommandType.SETTINGS) && Objects.equals(lastCommandKey, CommandType.LANGUAGE)) {
                    Language language = getLangByUserInput(text);
                    if (Objects.nonNull(language)) {
                        personDTO.setLanguage(language);
                        financialControlDaoService.updatePerson(personDTO);
                        sendMessage.setReplyMarkup(mainMenu(personDTO));
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.CHANGES_SAVED_TEXT)));
                        userSessionService.clearCommands(username);
                    } else
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.INVALID_INPUT_ERROR_TEXT)));

                } else if (Objects.equals(firstCommandKey, CommandType.SETTINGS) && Objects.equals(lastCommandKey, CommandType.CURRENCY)) {
                    CurrencyDTO currencyDTO = getCurrencyDtoByUserInput(text);
                    if (Objects.nonNull(currencyDTO)) {
                        personDTO.setCurrencyDTO(currencyDTO);
                        financialControlDaoService.updatePerson(personDTO);
                        sendMessage.setReplyMarkup(mainMenu(personDTO));
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.CHANGES_SAVED_TEXT)));
                        userSessionService.clearCommands(username);
                    } else
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.INVALID_INPUT_ERROR_TEXT)));

                } else if (Objects.nonNull(firstCommand)
                        && Objects.nonNull(lastCommand)
                        && Objects.equals(firstCommandKey, CommandType.ADD_RECORD)
                        && Objects.equals(lastCommandKey, CommandType.SUBCATEGORY)) {
                    try {
                        Double amount = Double.parseDouble(text);
                        userSessionService.saveCommand(username, CommandType.AMOUNT, amount);
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.AMOUNT_SPENT_TIME_TEXT)));
                    } catch (NumberFormatException e) {
                        log.error(e.getMessage());
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.INVALID_AMOUNT_ERROR_TEXT)));
                    }
                } else if (Objects.nonNull(firstCommand)
                        && Objects.nonNull(lastCommand)
                        && Objects.equals(firstCommandKey, CommandType.ADD_RECORD)
                        && Objects.equals(lastCommandKey, CommandType.AMOUNT)) {

                    try {
                        LocalDateTime spentTime = LocalDateTime.of(LocalDate.parse(text, DateTimeFormatter.ofPattern(botConstants.getDateTimePattern())), LocalTime.MIN);
                        userSessionService.saveCommand(username, CommandType.EVENT_TIME, spentTime);
                        Subcategory subcategory = (Subcategory) userSessionService.getValueByKey(username, CommandType.SUBCATEGORY);
                        Double amount = (Double) userSessionService.getValueByKey(username, CommandType.AMOUNT);

                        if (Objects.nonNull(subcategory) && Objects.nonNull(amount)) {
                            FinancialControlDTO financialControlDTO = new FinancialControlDTO();
                            financialControlDTO.setSubcategoryDTO(SubcategoryDTO.builder().subcategory(subcategory).build());
                            financialControlDTO.setAmount(amount);
                            financialControlDTO.setEventTime(spentTime);
                            financialControlDTO.setPersonDTO(personDTO);
                            financialControlDaoService.addFcInfo(financialControlDTO);
                            userSessionService.clearCommands(username);
                            sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.RECORD_SAVED_TEXT)));
                            sendMessage.setReplyMarkup(mainMenu(personDTO));
                        } else {
                            sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.COMMANDS_NOT_SAVED_TEXT)));
                            sendMessage.setReplyMarkup(mainMenu(personDTO));
                            userSessionService.clearCommands(username);
                        }
                    } catch (DateTimeParseException e) {
                        log.error(e);
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.INVALID_TIME_ERROR_TEXT)));
                    }
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.ADD_RECORD_MENU_ITEM)), text)) {
                    ReplyKeyboardMarkup categoryMenu = new ReplyKeyboardMarkup();
                    List<KeyboardRow> keyboardRowList = new ArrayList<>();
                    for (CategoryDTO categoryDTO : categoryDaoService.getAllCategories()) {
                        KeyboardRow keyboardRow = new KeyboardRow();
                        keyboardRow.add(LangHelper.getCategoryTextByLang(personDTO.getLanguage(), categoryDTO));
                        keyboardRowList.add(keyboardRow);
                    }
                    keyboardRowList.add(setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.BACK_TO_MENU_ITEM))));
                    categoryMenu.setKeyboard(keyboardRowList);

                    sendMessage.setReplyMarkup(categoryMenu);
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.CHOOSE_CATEGORY_TEXT)));
                    userSessionService.saveCommand(username, CommandType.ADD_RECORD, "add record");
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.SETTINGS_MENU_ITEM)), text)) {
                    sendMessage.setReplyMarkup(settingsMenu(personDTO));
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.SETTINGS_MENU_ITEM)));
                    userSessionService.saveCommand(username, CommandType.SETTINGS, "settings");
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.LANGUAGE_MENU_ITEM)), text)) {
                    sendMessage.setReplyMarkup(langMenu(personDTO));
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.CHOOSE_LANGUAGE_TEXT)));
                    userSessionService.saveCommand(username, CommandType.LANGUAGE, "lang");
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.CURRENCY_MENU_ITEM)), text)) {
                    sendMessage.setReplyMarkup(currenciesMenu(personDTO));
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.CHOOSE_CURRENCY_TEXT)));
                    userSessionService.saveCommand(username, CommandType.CURRENCY, "currency");
                } else if (categoryOptional.isPresent()) {
                    List<SubcategoryDTO> subcategoryDTOList = categoryDaoService.getAllSubcategories()
                            .stream()
                            .filter(subcategoryDTO -> Objects.equals(subcategoryDTO.getCategory(), categoryOptional.get().getCategory()))
                            .collect(Collectors.toList());

                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.CHOOSE_SUBCATEGORY_TEXT)));
                    sendMessage.setReplyMarkup(subcategoryListMenu(subcategoryDTOList, personDTO));
                    userSessionService.saveCommand(username, CommandType.CATEGORY, text);
                } else if (subCategoryOptional.isPresent()) {
                    userSessionService.saveCommand(username, CommandType.SUBCATEGORY, subCategoryOptional.get().getSubcategory());
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.SPENT_AMOUNT_TEXT)));
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.RECORDS_MENU_ITEM)), text)) {
                    sendMessage.setReplyMarkup(recordsMenu(personDTO));
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.RECORDS_MENU_ITEM)));
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.CATEGORY_RECORDS_MENU_ITEM)), text)) {
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.DATE_RANGE_FOR_RECORDS)));
                    userSessionService.saveCommand(username, CommandType.CATEGORY_RECORDS, text);
                } else if (Objects.equals(firstCommandKey, CommandType.CATEGORY_RECORDS)) {
                    String[] timeRange = text.split("-");
                    if (timeRange.length != 2) {
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.INVALID_TIME_ERROR_TEXT)));
                    } else {
                        LocalDateTime startTime = parseLocalDateTimeInternal(timeRange[0]);// LocalDateTime.of(LocalDate.parse(, DateTimeFormatter.ofPattern(botConstants.getDateTimePattern())), LocalTime.MIN);
                        LocalDateTime endTime = parseLocalDateTimeInternal(timeRange[1]);//LocalDateTime.of(LocalDate.parse(timeRange[1], DateTimeFormatter.ofPattern(botConstants.getDateTimePattern())), LocalTime.MIN);
                        List<FinancialControlDTO> financialControlDtoList = financialControlDaoService.getFcDTOListInDateRangeByСategory(personDTO.getId(), startTime, endTime);
                        if (CollectionUtils.isEmpty(financialControlDtoList)) {
                            sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.NO_RECORDS_FOUND)));
                        } else {
                            StringBuilder builder = new StringBuilder();
                            PersonDTO p = personDTO;
                            financialControlDtoList.forEach(financialControlDTO -> {
                                builder.append(
                                        String.format(
                                                "%s: %s %s",
                                                LangHelper.getCategoryTextByLang(p.getLanguage(), financialControlDTO.getCategoryDTO()),
                                                financialControlDTO.getAmount(),
                                                Objects.nonNull(p.getCurrencyDTO()) ? p.getCurrencyDTO().getSign() : null
                                        )
                                );
                                builder.append("\n");
                            });
                            sendMessage.setText(builder.toString());
                        }
                        sendMessage.setReplyMarkup(mainMenu(personDTO));
                        userSessionService.clearCommands(username);
                    }
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.SUBCATEGORY_RECORDS_MENU_ITEM)), text)) {
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.DATE_RANGE_FOR_RECORDS)));
                    userSessionService.saveCommand(username, CommandType.SUBCATEGORY_RECORDS, text);
                } else if (Objects.equals(firstCommandKey, CommandType.SUBCATEGORY_RECORDS)) {
                    String[] timeRange = text.split("-");
                    if (timeRange.length != 2) {
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.INVALID_TIME_ERROR_TEXT)));
                    } else {
                        LocalDateTime startTime = parseLocalDateTimeInternal(timeRange[0]);//LocalDateTime.of(LocalDate.parse(timeRange[0], DateTimeFormatter.ofPattern(botConstants.getDateTimePattern())), LocalTime.MIN);
                        LocalDateTime endTime = parseLocalDateTimeInternal(timeRange[1]);//LocalDateTime.of(LocalDate.parse(timeRange[1], DateTimeFormatter.ofPattern(botConstants.getDateTimePattern())), LocalTime.MIN);
                        List<FinancialControlDTO> financialControlDtoList = financialControlDaoService.getFcDTOListInDateRangeBySubcategory(personDTO.getId(), startTime, endTime);
                        if (CollectionUtils.isEmpty(financialControlDtoList)) {
                            sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.NO_RECORDS_FOUND)));
                        } else {
                            StringBuilder builder = new StringBuilder();
                            PersonDTO p = personDTO;
                            financialControlDtoList.forEach(financialControlDTO -> {
                                builder.append(
                                        String.format(
                                                "%s: %s %s",
                                                LangHelper.getSubcategoryTextByLang(p.getLanguage(), financialControlDTO.getSubcategoryDTO()),
                                                financialControlDTO.getAmount(),
                                                Objects.nonNull(p.getCurrencyDTO()) ? p.getCurrencyDTO().getSign() : null
                                        )
                                );
                                builder.append("\n");
                            });
                            sendMessage.setText(builder.toString());
                        }
                        sendMessage.setReplyMarkup(mainMenu(personDTO));
                        userSessionService.clearCommands(username);
                    }
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.DETAILED_RECORDS_MENU_ITEM)), text)) {
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.DATE_RANGE_FOR_RECORDS)));
                    userSessionService.saveCommand(username, CommandType.DETAILED_RECORDS, text);
                } else if (Objects.equals(firstCommandKey, CommandType.DETAILED_RECORDS)) {
                    String[] timeRange = text.split("-");
                    if (timeRange.length != 2) {
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.INVALID_TIME_ERROR_TEXT)));
                    } else {
                        LocalDateTime startTime = parseLocalDateTimeInternal(timeRange[0]);//LocalDateTime.of(LocalDate.parse(timeRange[0], DateTimeFormatter.ofPattern(botConstants.getDateTimePattern())), LocalTime.MIN);
                        LocalDateTime endTime = parseLocalDateTimeInternal(timeRange[1]);//LocalDateTime.of(LocalDate.parse(timeRange[1], DateTimeFormatter.ofPattern(botConstants.getDateTimePattern())), LocalTime.MIN);
                        List<FinancialControlDTO> financialControlDtoList = financialControlDaoService.getFcDTOListInDateRange(personDTO.getId(), startTime, endTime);
                        if (CollectionUtils.isEmpty(financialControlDtoList)) {
                            sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.NO_RECORDS_FOUND)));
                        } else {
                            StringBuilder builder = new StringBuilder();
                            PersonDTO p = personDTO;
                            financialControlDtoList.forEach(financialControlDTO -> {
                                builder.append(
                                        String.format(
                                                "%s: %s %s   %s",
                                                LangHelper.getSubcategoryTextByLang(p.getLanguage(), financialControlDTO.getSubcategoryDTO()),
                                                financialControlDTO.getAmount(),
                                                Objects.nonNull(p.getCurrencyDTO()) ? p.getCurrencyDTO().getSign() : null,
                                                DateTimeFormatter.ofPattern(botConstants.getDateTimePattern()).format(financialControlDTO.getEventTime())
                                        )
                                );
                                builder.append("\n");
                            });
                            sendMessage.setText(builder.toString());
                        }
                        sendMessage.setReplyMarkup(mainMenu(personDTO));
                        userSessionService.clearCommands(username);
                    }
                }
                return sendMessage;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            userSessionService.clearCommands(username);
            throw new BotAppException(e);
        }
        return null;
    }

    private Optional<CategoryDTO> getCategoryByUserInputText(PersonDTO personDTO, String text) {
        return categoryDaoService.getAllCategories()
                .stream()
                .filter(c -> Objects.equals(LangHelper.getCategoryTextByLang(personDTO.getLanguage(), c), text))
                .findFirst();
    }

    private Optional<SubcategoryDTO> getSubcategoryByUserInputText(PersonDTO personDTO, String text) {
        return categoryDaoService.getAllSubcategories()
                .stream()
                .filter(s -> Objects.equals(LangHelper.getSubcategoryTextByLang(personDTO.getLanguage(), s), text))
                .findFirst();
    }

    private ReplyKeyboardMarkup mainMenu(PersonDTO personDTO) {
        ReplyKeyboardMarkup menu = new ReplyKeyboardMarkup();
        menu.setKeyboard(Arrays.asList(
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDTOByKey(Text.ADD_RECORD_MENU_ITEM.name()))),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDTOByKey(Text.RECORDS_MENU_ITEM.name()))),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDTOByKey(Text.SETTINGS_MENU_ITEM.name())))
        ));
        return menu;
    }

    private ReplyKeyboardMarkup settingsMenu(PersonDTO personDTO) {
        ReplyKeyboardMarkup menu = new ReplyKeyboardMarkup();
        menu.setKeyboard(Arrays.asList(
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDTOByKey(Text.LANGUAGE_MENU_ITEM.name()))),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDTOByKey(Text.CURRENCY_MENU_ITEM.name()))),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.BACK_TO_MENU_ITEM)))
        ));
        return menu;
    }

    private ReplyKeyboardMarkup currenciesMenu(PersonDTO personDTO) {
        ReplyKeyboardMarkup menu = new ReplyKeyboardMarkup();
        List<CurrencyDTO> currencyDtoList = currencyDaoService.getCurrencyDtoList();
        List<KeyboardRow> keyboard = new ArrayList<>();
        currencyDtoList.forEach(currencyDTO -> {
            keyboard.add(setOneButtonRow(String.valueOf(currencyDTO.getCode())));
        });
        keyboard.add(setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.BACK_TO_MENU_ITEM))));

        menu.setKeyboard(keyboard);
        return menu;
    }

    private ReplyKeyboardMarkup recordsMenu(PersonDTO personDTO) {
        ReplyKeyboardMarkup menu = new ReplyKeyboardMarkup();
        menu.setKeyboard(Arrays.asList(
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDTOByKey(Text.CATEGORY_RECORDS_MENU_ITEM.name()))),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDTOByKey(Text.SUBCATEGORY_RECORDS_MENU_ITEM.name()))),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDTOByKey(Text.DETAILED_RECORDS_MENU_ITEM.name()))),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.BACK_TO_MENU_ITEM)))
        ));
        return menu;
    }

    private ReplyKeyboardMarkup langMenu(PersonDTO personDTO) {
        ReplyKeyboardMarkup menu = new ReplyKeyboardMarkup();
        menu.setKeyboard(Arrays.asList(
                setOneButtonRow(botConstants.getLangRu()),
                setOneButtonRow(botConstants.getLangEn()),
                setOneButtonRow(botConstants.getLangKk()),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.BACK_TO_MENU_ITEM)))
        ));
        return menu;
    }

    private ReplyKeyboardMarkup subcategoryListMenu(List<SubcategoryDTO> subcategoryDTOList, PersonDTO personDTO) {
        ReplyKeyboardMarkup subcategoryListMenu = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        subcategoryDTOList.forEach(subcategoryDTO -> {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(LangHelper.getSubcategoryTextByLang(personDTO.getLanguage(), subcategoryDTO));
            keyboardRows.add(keyboardRow);
        });
        keyboardRows.add(setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.BACK_TO_MENU_ITEM))));
        subcategoryListMenu.setKeyboard(keyboardRows);
        subcategoryListMenu.setResizeKeyboard(true);
        return subcategoryListMenu;
    }

    private Language getLangByUserInput(String chosenLang) {
        if (chosenLang.contains("Ru")) {
            return Language.RUS;
        } else if (chosenLang.contains("En")) {
            return Language.ENG;
        } else if (chosenLang.contains("Kk")) {
            return Language.KK;
        }
        return null;
    }

    private CurrencyDTO getCurrencyDtoByUserInput(String chosenCurrency) {
        return currencyDaoService.getCurrencyDtoList().stream().filter(currencyDTO -> Objects.equals(String.valueOf(currencyDTO.getCode()), chosenCurrency)).findFirst().orElse(null);
    }

    private CommandType getKeyFromSingleMap(LinkedHashMap<CommandType, Object> command) {
        return !CollectionUtils.isEmpty(command) ? command.keySet().stream().findFirst().get() : null;
    }

    private Object getValueFromSingleMap(LinkedHashMap<CommandType, Object> command) {
        return !CollectionUtils.isEmpty(command) ? command.values().stream().findFirst().get() : null;
    }

    private LocalDateTime parseLocalDateTimeInternal(String toParse) {
        if (StringUtils.isEmpty(toParse))
            return null;

        try {
            return LocalDateTime.of(LocalDate.parse(toParse.replaceAll(" ", ""), DateTimeFormatter.ofPattern(botConstants.getDateTimePattern())), LocalTime.MIN);
        } catch (DateTimeParseException e) {
            log.error(e);
        }
        return null;
    }

    @Override
    public Long getChatId(Update update) throws BotAppException {
        return Objects.nonNull(update.getMessage()) ? update.getMessage().getChatId() : null;
    }
}