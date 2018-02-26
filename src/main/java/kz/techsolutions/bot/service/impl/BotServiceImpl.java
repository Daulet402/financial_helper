package kz.techsolutions.bot.service.impl;

import kz.techsolutions.bot.api.*;
import kz.techsolutions.bot.api.dto.*;
import kz.techsolutions.bot.api.exception.BotAppException;
import kz.techsolutions.bot.helper.CategoryHelper;
import kz.techsolutions.bot.helper.CurrencyHelper;
import kz.techsolutions.bot.helper.LangHelper;
import kz.techsolutions.bot.helper.MenuHelper;
import kz.techsolutions.bot.service.BotConstants;
import kz.techsolutions.bot.utils.BotCollectionUtils;
import kz.techsolutions.bot.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    @Autowired
    private MenuHelper menuHelper;

    @Override
    public SendMessage processAndGetSendMessage(Update update) throws BotAppException {
        Long chatId = getChatId(update);
        String username = "";
        try {
            if (Objects.nonNull(update.getMessage())) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                username = Objects.nonNull(update.getMessage().getFrom()) ?
                        update.getMessage().getFrom().getUserName() : null;
                String text = StringUtils.defaultIfEmpty(update.getMessage().getText(), "");
                log.info(String.format("text: %s from: %s", text, username));
                PersonDTO personDTO = financialControlDaoService.getPersonByUsername(username);
                if (Objects.isNull(personDTO)) {
                    personDTO = new PersonDTO();
                    personDTO.setUsername(username);
                    personDTO.setLanguage(Language.RUS);
                    financialControlDaoService.addPerson(personDTO);
                }
                Optional<CategoryDTO> categoryOptional = CategoryHelper.getCategoryByUserInputText(
                        categoryDaoService.getAllCategories(),
                        personDTO,
                        text);
                CommandType lastCommandKey = BotCollectionUtils.getKeyFromSingleMap(userSessionService.getLastCommand(username));
                CommandType firstCommandKey = BotCollectionUtils.getKeyFromSingleMap(userSessionService.getFirstCommand(username));

                if (Objects.equals(botConstants.getStartText(), text)) {
                    sendMessage.setReplyMarkup(menuHelper.mainMenu(personDTO));
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.MAIN_MENU_TEXT)));
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.BACK_TO_MENU_ITEM)), text)) {
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.BACK_TO_MENU_ITEM)));
                    sendMessage.setReplyMarkup(menuHelper.mainMenu(personDTO));
                    userSessionService.clearCommands(username);
                } else if (Objects.equals(firstCommandKey, CommandType.SETTINGS) && Objects.equals(lastCommandKey, CommandType.LANGUAGE)) {
                    Language language = LangHelper.getLangByUserInput(text);
                    if (Objects.nonNull(language)) {
                        personDTO.setLanguage(language);
                        financialControlDaoService.updatePerson(personDTO);
                        sendMessage.setReplyMarkup(menuHelper.mainMenu(personDTO));
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.CHANGES_SAVED_TEXT)));
                        userSessionService.clearCommands(username);
                    } else
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.INVALID_INPUT_ERROR_TEXT)));

                } else if (Objects.equals(firstCommandKey, CommandType.SETTINGS) && Objects.equals(lastCommandKey, CommandType.CURRENCY)) {
                    CurrencyDTO currencyDTO = CurrencyHelper.getCurrencyDtoByUserInput(currencyDaoService.getCurrencyDtoList(), text);
                    if (Objects.nonNull(currencyDTO)) {
                        personDTO.setCurrencyDTO(currencyDTO);
                        financialControlDaoService.updatePerson(personDTO);
                        sendMessage.setReplyMarkup(menuHelper.mainMenu(personDTO));
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.CHANGES_SAVED_TEXT)));
                        userSessionService.clearCommands(username);
                    } else
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.INVALID_INPUT_ERROR_TEXT)));

                } else if (Objects.equals(firstCommandKey, CommandType.ADD_RECORD) && Objects.equals(lastCommandKey, CommandType.SUBCATEGORY)) {
                    try {
                        Double amount = Double.parseDouble(StringUtils.replace(text, " ", ""));
                        if (Double.compare(amount, 0d) <= 0) {
                            sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.INVALID_AMOUNT_ERROR_TEXT)));
                        } else {
                            userSessionService.saveCommand(username, CommandType.AMOUNT, amount);
                            sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.AMOUNT_SPENT_TIME_TEXT)));
                        }
                    } catch (NumberFormatException e) {
                        log.error(e.getMessage());
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.INVALID_AMOUNT_ERROR_TEXT)));
                    }
                } else if (Objects.equals(firstCommandKey, CommandType.ADD_RECORD) && Objects.equals(lastCommandKey, CommandType.AMOUNT)) {
                    try {
                        LocalDateTime spentTime = LocalDateTime.of(LocalDate.parse(text, DateTimeFormatter.ofPattern(botConstants.getDateTimePattern())), LocalTime.MIN);
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.ADD_COMMENT_TEXT)));
                        ReplyKeyboardMarkup menu = menuHelper.commentMenu(personDTO);
                        menu.setOneTimeKeyboard(Boolean.TRUE);
                        sendMessage.setReplyMarkup(menu);
                        userSessionService.saveCommand(username, CommandType.EVENT_TIME, spentTime);
                    } catch (DateTimeParseException e) {
                        log.error(e);
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.INVALID_TIME_ERROR_TEXT)));
                    }
                } else if (Objects.equals(firstCommandKey, CommandType.ADD_RECORD) && Objects.equals(lastCommandKey, CommandType.EVENT_TIME)) {
                    if (StringUtils.equalsIgnoreCase(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.YES_TEXT)), text)) {
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.TYPE_COMMENT_TEXT)));
                        userSessionService.saveCommand(username, CommandType.ADD_COMMENT, "yes");
                    } else if (StringUtils.equalsIgnoreCase(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.NO_TEXT)), text)) {
                        saveAndSetMessage(username, null, personDTO, sendMessage);
                        userSessionService.clearCommands(username);
                    }
                } else if (Objects.equals(firstCommandKey, CommandType.ADD_RECORD) && Objects.equals(lastCommandKey, CommandType.ADD_COMMENT)) {
                    saveAndSetMessage(username, text, personDTO, sendMessage);
                    userSessionService.clearCommands(username);
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.ADD_RECORD_MENU_ITEM)), text)) {
                    ReplyKeyboardMarkup categoryMenu = new ReplyKeyboardMarkup();
                    List<KeyboardRow> keyboardRowList = new ArrayList<>();
                    for (CategoryDTO categoryDTO : categoryDaoService.getAllCategories()) {
                        KeyboardRow keyboardRow = new KeyboardRow();
                        keyboardRow.add(LangHelper.getCategoryTextByLang(personDTO.getLanguage(), categoryDTO));
                        keyboardRowList.add(keyboardRow);
                    }
                    keyboardRowList.add(menuHelper.setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.BACK_TO_MENU_ITEM))));
                    categoryMenu.setKeyboard(keyboardRowList);

                    sendMessage.setReplyMarkup(categoryMenu);
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.CHOOSE_CATEGORY_TEXT)));
                    userSessionService.saveCommand(username, CommandType.ADD_RECORD, "add record");
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.SETTINGS_MENU_ITEM)), text)) {
                    sendMessage.setReplyMarkup(menuHelper.settingsMenu(personDTO));
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.SETTINGS_MENU_ITEM)));
                    userSessionService.saveCommand(username, CommandType.SETTINGS, "settings");
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.LANGUAGE_MENU_ITEM)), text)) {
                    sendMessage.setReplyMarkup(menuHelper.langMenu(personDTO));
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.CHOOSE_LANGUAGE_TEXT)));
                    userSessionService.saveCommand(username, CommandType.LANGUAGE, "lang");
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.CURRENCY_MENU_ITEM)), text)) {
                    sendMessage.setReplyMarkup(menuHelper.currenciesMenu(personDTO));
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.CHOOSE_CURRENCY_TEXT)));
                    userSessionService.saveCommand(username, CommandType.CURRENCY, "currency");
                } else if (Objects.equals(firstCommandKey, CommandType.ADD_RECORD) && Objects.equals(lastCommandKey, CommandType.ADD_RECORD) /*&& categoryOptional.isPresent()*/) {
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.CHOOSE_SUBCATEGORY_TEXT)));
                    sendMessage.setReplyMarkup(menuHelper.subcategoryListMenu(categoryOptional.get().getSubcategoryDtoList(), personDTO));
                    UserSessionDTO userSessionDto = userSessionService.getUserSession(username);
                    userSessionDto.setCategoryDto(categoryOptional.get());
                    userSessionDto.getCommands().put(CommandType.CATEGORY, text);
                } else if (Objects.equals(firstCommandKey, CommandType.ADD_RECORD) && Objects.equals(lastCommandKey, CommandType.CATEGORY)) {
                    UserSessionDTO userSessionDTO = userSessionService.getUserSession(username);
                    if (Objects.nonNull(userSessionDTO) && Objects.nonNull(userSessionDTO.getCategoryDto())) {
                        Optional<SubcategoryDTO> subCategoryOptional = CategoryHelper.getSubcategoryByUserInputText(
                                userSessionDTO.getCategoryDto().getSubcategoryDtoList(),
                                personDTO,
                                text);

                        userSessionService.saveCommand(username, CommandType.SUBCATEGORY, subCategoryOptional.get().getSubcategory());
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.SPENT_AMOUNT_TEXT)));
                    } else {
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.COMMANDS_NOT_SAVED_TEXT)));
                        sendMessage.setReplyMarkup(menuHelper.mainMenu(personDTO));
                        userSessionService.clearCommands(username);
                    }
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.RECORDS_MENU_ITEM)), text)) {
                    sendMessage.setReplyMarkup(menuHelper.recordsMenu(personDTO));
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.RECORDS_MENU_ITEM)));
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.CATEGORY_RECORDS_MENU_ITEM)), text)) {
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.DATE_RANGE_FOR_RECORDS)));
                    userSessionService.saveCommand(username, CommandType.CATEGORY_RECORDS, text);
                } else if (Objects.equals(firstCommandKey, CommandType.CATEGORY_RECORDS)) {
                    String[] timeRange = text.split("-");
                    if (timeRange.length != 2) {
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.INVALID_TIME_ERROR_TEXT)));
                    } else {
                        LocalDateTime startTime = DateTimeUtils.parseLocalDateTime(timeRange[0], botConstants.getDateTimePattern());
                        LocalDateTime endTime = DateTimeUtils.parseLocalDateTime(timeRange[1], botConstants.getDateTimePattern());
                        List<FinancialControlDTO> financialControlDtoList = financialControlDaoService.getFcDTOListInDateRangeByСategory(personDTO.getId(), startTime, endTime);
                        if (CollectionUtils.isEmpty(financialControlDtoList)) {
                            sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.NO_RECORDS_FOUND)));
                        } else {
                            StringBuilder builder = new StringBuilder();
                            PersonDTO p = personDTO;
                            String currencySign = Objects.nonNull(p.getCurrencyDTO()) ? p.getCurrencyDTO().getSign() : null;
                            Double[] total = new Double[]{0d};
                            financialControlDtoList.forEach(financialControlDTO -> {
                                builder.append(
                                        String.format(
                                                "%s: %s%s",
                                                LangHelper.getCategoryTextByLang(p.getLanguage(), financialControlDTO.getCategoryDTO()),
                                                financialControlDTO.getAmount(),
                                                currencySign
                                        )
                                );
                                builder.append("\n");
                                total[0] += financialControlDTO.getAmount();
                            });
                            builder.append(String.format("%s: %s%s",
                                    LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.TOTAL_TEXT)),
                                    total[0],
                                    currencySign));
                            sendMessage.setText(builder.toString());
                        }
                        sendMessage.setReplyMarkup(menuHelper.mainMenu(personDTO));
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
                        LocalDateTime startTime = DateTimeUtils.parseLocalDateTime(timeRange[0], botConstants.getDateTimePattern());
                        LocalDateTime endTime = DateTimeUtils.parseLocalDateTime(timeRange[1], botConstants.getDateTimePattern());
                        List<FinancialControlDTO> financialControlDtoList = financialControlDaoService.getFcDTOListInDateRangeBySubcategory(personDTO.getId(), startTime, endTime);
                        if (CollectionUtils.isEmpty(financialControlDtoList)) {
                            sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.NO_RECORDS_FOUND)));
                        } else {
                            StringBuilder builder = new StringBuilder();
                            PersonDTO p = personDTO;
                            Double[] total = new Double[]{0d};
                            String currencySign = Objects.nonNull(p.getCurrencyDTO()) ? p.getCurrencyDTO().getSign() : null;
                            financialControlDtoList.forEach(financialControlDTO -> {
                                builder.append(
                                        String.format(
                                                "%s (%s): %s%s",
                                                LangHelper.getSubcategoryTextByLang(p.getLanguage(), financialControlDTO.getSubcategoryDTO()),
                                                LangHelper.getCategoryTextByLang(p.getLanguage(), Objects.nonNull(financialControlDTO.getSubcategoryDTO()) ? financialControlDTO.getSubcategoryDTO().getCategoryDTO() : null),
                                                financialControlDTO.getAmount(),
                                                currencySign
                                        )
                                );
                                builder.append("\n");
                                total[0] += financialControlDTO.getAmount();
                            });
                            builder.append(String.format("%s: %s%s",
                                    LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.TOTAL_TEXT)),
                                    total[0],
                                    currencySign));
                            sendMessage.setText(builder.toString());
                        }
                        sendMessage.setReplyMarkup(menuHelper.mainMenu(personDTO));
                        userSessionService.clearCommands(username);
                    }
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.DETAILED_RECORDS_MENU_ITEM)), text)) {
                    sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.DATE_RANGE_FOR_RECORDS)));
                    userSessionService.saveCommand(username, CommandType.DETAILED_RECORDS, text);
                } else if (Objects.equals(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.RECORDS_FOR_TODAY_TEXT)), text)) {
                    LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
                    LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
                    setDetailedRecordsToMessage(personDTO, sendMessage, startTime, endTime);
                    sendMessage.setReplyMarkup(menuHelper.mainMenu(personDTO));
                    userSessionService.clearCommands(username);
                } else if (Objects.equals(firstCommandKey, CommandType.DETAILED_RECORDS)) {
                    String[] timeRange = text.split("-");
                    if (timeRange.length != 2) {
                        sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.INVALID_TIME_ERROR_TEXT)));
                    } else {
                        LocalDateTime startTime = DateTimeUtils.parseLocalDateTime(timeRange[0], botConstants.getDateTimePattern());
                        LocalDateTime endTime = DateTimeUtils.parseLocalDateTime(timeRange[1], botConstants.getDateTimePattern());
                        setDetailedRecordsToMessage(personDTO, sendMessage, startTime, endTime);
                        sendMessage.setReplyMarkup(menuHelper.mainMenu(personDTO));
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

    @Override
    public Long getChatId(Update update) throws BotAppException {
        return Objects.nonNull(update.getMessage()) ? update.getMessage().getChatId() : null;
    }

    private void saveAndSetMessage(String username, String text, PersonDTO personDTO, SendMessage sendMessage) {
        LocalDateTime spentTime = (LocalDateTime) userSessionService.getValueByKey(username, CommandType.EVENT_TIME);
        Subcategory subcategory = (Subcategory) userSessionService.getValueByKey(username, CommandType.SUBCATEGORY);
        Double amount = (Double) userSessionService.getValueByKey(username, CommandType.AMOUNT);
        if (Objects.nonNull(subcategory) && Objects.nonNull(amount)) {
            FinancialControlDTO financialControlDTO = new FinancialControlDTO();
            financialControlDTO.setSubcategoryDTO(SubcategoryDTO.builder().subcategory(subcategory).build());
            financialControlDTO.setAmount(amount);
            financialControlDTO.setEventTime(spentTime);
            financialControlDTO.setPersonDTO(personDTO);
            financialControlDTO.setComment(text);
            financialControlDaoService.addFcInfo(financialControlDTO);
            sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.RECORD_SAVED_TEXT)));
            sendMessage.setReplyMarkup(menuHelper.mainMenu(personDTO));
        } else {
            sendMessage.setText(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.COMMANDS_NOT_SAVED_TEXT)));
            sendMessage.setReplyMarkup(menuHelper.mainMenu(personDTO));
        }
    }

    private void setDetailedRecordsToMessage(PersonDTO p, SendMessage sendMessage, LocalDateTime startTime, LocalDateTime endTime) {
        List<FinancialControlDTO> financialControlDtoList = financialControlDaoService.getFcDTOListInDateRange(p.getId(), startTime, endTime);
        if (CollectionUtils.isEmpty(financialControlDtoList)) {
            sendMessage.setText(LangHelper.getTextByLang(p.getLanguage(), textService.getTextDtoMap().get(Text.NO_RECORDS_FOUND)));
        } else {
            StringBuilder builder = new StringBuilder();
            Double[] total = new Double[]{0d};
            String currencySign = Objects.nonNull(p.getCurrencyDTO()) ? p.getCurrencyDTO().getSign() : null;
            financialControlDtoList.forEach(financialControlDTO -> {
                builder.append(
                        String.format(
                                "%s (%s): %s%s %s %s",
                                LangHelper.getSubcategoryTextByLang(p.getLanguage(), financialControlDTO.getSubcategoryDTO()),
                                LangHelper.getCategoryTextByLang(p.getLanguage(), Objects.nonNull(financialControlDTO.getSubcategoryDTO()) ? financialControlDTO.getSubcategoryDTO().getCategoryDTO() : null),
                                financialControlDTO.getAmount(),
                                currencySign,
                                DateTimeFormatter.ofPattern(botConstants.getDateTimePattern()).format(financialControlDTO.getEventTime()),
                                StringUtils.defaultIfEmpty(financialControlDTO.getComment(), "")
                        )
                );
                builder.append("\n");
                total[0] += financialControlDTO.getAmount();
            });
            builder.append(String.format("%s: %s%s",
                    LangHelper.getTextByLang(p.getLanguage(), textService.getTextDtoMap().get(Text.TOTAL_TEXT)),
                    total[0],
                    currencySign));
            sendMessage.setText(builder.toString());
        }
    }
}