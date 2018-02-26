package kz.techsolutions.bot.helper;

import kz.techsolutions.bot.api.CurrencyDaoService;
import kz.techsolutions.bot.api.TextService;
import kz.techsolutions.bot.api.dto.CurrencyDTO;
import kz.techsolutions.bot.api.dto.PersonDTO;
import kz.techsolutions.bot.api.dto.SubcategoryDTO;
import kz.techsolutions.bot.api.dto.Text;
import kz.techsolutions.bot.service.BotConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class MenuHelper {

    @Autowired
    private TextService textService;

    @Autowired
    private BotConstants botConstants;

    @Autowired
    private CurrencyDaoService currencyDaoService;

    public ReplyKeyboardMarkup mainMenu(PersonDTO personDTO) {
        ReplyKeyboardMarkup menu = new ReplyKeyboardMarkup();
        menu.setKeyboard(Arrays.asList(
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDTOByKey(Text.ADD_RECORD_MENU_ITEM.name()))),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDTOByKey(Text.RECORDS_MENU_ITEM.name()))),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDTOByKey(Text.SETTINGS_MENU_ITEM.name())))
        ));
        return menu;
    }

    public ReplyKeyboardMarkup settingsMenu(PersonDTO personDTO) {
        ReplyKeyboardMarkup menu = new ReplyKeyboardMarkup();
        menu.setKeyboard(Arrays.asList(
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDTOByKey(Text.LANGUAGE_MENU_ITEM.name()))),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDTOByKey(Text.CURRENCY_MENU_ITEM.name()))),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.BACK_TO_MENU_ITEM)))
        ));
        return menu;
    }

    public ReplyKeyboardMarkup currenciesMenu(PersonDTO personDTO) {
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

    public ReplyKeyboardMarkup recordsMenu(PersonDTO personDTO) {
        ReplyKeyboardMarkup menu = new ReplyKeyboardMarkup();
        menu.setKeyboard(Arrays.asList(
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDTOByKey(Text.CATEGORY_RECORDS_MENU_ITEM.name()))),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDTOByKey(Text.SUBCATEGORY_RECORDS_MENU_ITEM.name()))),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDTOByKey(Text.DETAILED_RECORDS_MENU_ITEM.name()))),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDTOByKey(Text.RECORDS_FOR_TODAY_TEXT.name()))),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.BACK_TO_MENU_ITEM)))
        ));
        return menu;
    }

    public ReplyKeyboardMarkup langMenu(PersonDTO personDTO) {
        ReplyKeyboardMarkup menu = new ReplyKeyboardMarkup();
        menu.setKeyboard(Arrays.asList(
                setOneButtonRow(botConstants.getLangRu()),
                setOneButtonRow(botConstants.getLangEn()),
                setOneButtonRow(botConstants.getLangKk()),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.BACK_TO_MENU_ITEM)))
        ));
        return menu;
    }

    public ReplyKeyboardMarkup subcategoryListMenu(List<SubcategoryDTO> subcategoryDTOList, PersonDTO personDTO) {
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

    public ReplyKeyboardMarkup commentMenu(PersonDTO personDTO) {
        ReplyKeyboardMarkup menu = new ReplyKeyboardMarkup();
        menu.setKeyboard(Arrays.asList(
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.YES_TEXT))),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.NO_TEXT))),
                setOneButtonRow(LangHelper.getTextByLang(personDTO.getLanguage(), textService.getTextDtoMap().get(Text.BACK_TO_MENU_ITEM)))
        ));
        return menu;
    }

    public KeyboardRow setOneButtonRow(String text) {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(text);
        return keyboardRow;
    }
}