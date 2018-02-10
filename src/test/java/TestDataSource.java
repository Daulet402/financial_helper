import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.PieChart;
import com.googlecode.charts4j.Slice;
import kz.techsolutions.bot.api.*;
import kz.techsolutions.bot.api.dto.*;
import kz.techsolutions.bot.app.BotApplication;
import kz.techsolutions.bot.helper.CategoryHelper;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.googlecode.charts4j.Color.BLACK;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BotApplication.class)
//@ConfigurationProperties("application.properties")
@PropertySource("classpath:app.datasource.properties")
public class TestDataSource {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TextService textService;

    @Autowired
    private FinancialControlDaoService financialControlDaoService;

    @Autowired
    private CategoryDaoService categoryDaoService;

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private Logger log;

    @Autowired
    private CurrencyDaoService currencyDaoService;

    @Test
    public void testDatasource() {
        System.out.println(dataSource);
    }

    @Test
    public void testAddPersonMethod() throws Exception {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setFirstName("Daulet");
        personDTO.setLastName("Nurgali");
        personDTO.setUsername("Daulet.Nurgali");
        personDTO.setLanguage(Language.RUS);

        //  financialControlDaoService.addPerson(personDTO);
    }

    @Test
    public void testUpdatePersonMethod() throws Exception {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setFirstName("Daulet2");
        personDTO.setLastName("Nurgali2");
        personDTO.setUsername("Chere");
        personDTO.setLanguage(Language.ENG);

        // financialControlDaoService.updatePerson(personDTO);
    }


    @Test
    public void testAddFcInfo() throws Exception {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setId(5l);

        FinancialControlDTO financialControlDTO = new FinancialControlDTO();
        financialControlDTO.setAmount(1550.55);
        financialControlDTO.setEventTime(LocalDateTime.now().minusDays(25).plusHours(45));
        //  financialControlDTO.setSubcategory(Subcategory.BASKETBALL);
        financialControlDTO.setPersonDTO(personDTO);

        // financialControlDaoService.addFcInfo(financialControlDTO);
    }

    @Test
    public void testGetAllCategories() {
        List<CategoryDTO> categoryDTOList = categoryDaoService.getAllCategories();
        assertTrue(!CollectionUtils.isEmpty(categoryDTOList));
    }

    @Test
    public void testAddSubcategoryDTO() {
        SubcategoryDTO subcategoryDTO = new SubcategoryDTO();
        subcategoryDTO.setSubcategory(Subcategory.BASKETBALL);
        subcategoryDTO.setCategory(Category.HOUSE);
        subcategoryDTO.setNameRu("это тестовый вызов");
        //categoryDaoService.addSubcategoryDTO(subcategoryDTO);
    }

    @Test
    public void testGetAllSubcategories() {
        List<SubcategoryDTO> subcategoryDTOList = categoryDaoService.getAllSubcategories();
        assertTrue(!CollectionUtils.isEmpty(subcategoryDTOList));

        StringBuilder builder = new StringBuilder("");
        for (SubcategoryDTO subcategoryDTO : subcategoryDTOList) {
            builder.append(subcategoryDTO.getSubcategory() + "(" + subcategoryDTO.getId() + "l" + ")" + ",");
            builder.append("\n");
        }

        System.out.println("Enums:");
        System.out.println();
        System.out.println(builder.toString());
    }

    @Test
    public void testGetPersonByUsername() {
        PersonDTO personDTO = financialControlDaoService.getPersonByUsername("asd");
        assertNotNull(personDTO);
        assertEquals("Daulet2", personDTO.getFirstName());
        assertEquals("Nurgali2", personDTO.getLastName());
    }

    @Test
    public void testRemoveCommandMethod() {
        String username = "daulet";
        userSessionService.saveCommand(username, CommandType.ADD_RECORD, "add rec");
        userSessionService.getCommandByIndex(username, 500);
        userSessionService.saveCommand(username, CommandType.CATEGORY, "CAR");
        userSessionService.saveCommand(username, CommandType.SUBCATEGORY, "REPAIR");
        userSessionService.saveCommand(username, CommandType.AMOUNT, 2000d);
        userSessionService.saveCommand(username, CommandType.EVENT_TIME, LocalDate.now().minusDays(2));
        userSessionService.saveCommand(username, CommandType.SUBCATEGORY, "FUEL");
        userSessionService.saveCommand(username, CommandType.AMOUNT, 1500d);
        System.out.println();
        userSessionService.deleteCommand(username, CommandType.SUBCATEGORY, "FUEL");
        System.out.println();
    }

    @Test
    public void testTextService() {
        assertNotNull(textService.getAllTextDTOs());
        assertNotNull(textService.getTextDtoMap().get(Text.ADD_RECORD_MENU_ITEM));
        assertNotNull(textService.getTextDTOByKey(Text.ADD_RECORD_MENU_ITEM.name()));
    }

    @Test
    public void testCacheable() {
        assertNotNull(categoryDaoService.getAllSubcategories());
        assertNotNull(categoryDaoService.getAllSubcategories());
    }

    @Test
    public void testGetFcInDateRange() {
        List<FinancialControlDTO> financialControlDTOList = financialControlDaoService.getFcDTOListInDateRange(
                6l,
                LocalDateTime.of(2017, 12, 01, 0, 0),
                LocalDateTime.of(2017, 12, 30, 0, 0)
        );

        assertTrue(!CollectionUtils.isEmpty(financialControlDTOList));
    }

    @Test
    public void testPieChart() {
        LocalDateTime start = LocalDateTime.of(LocalDate.of(2017, 12, 26), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.now().plusYears(2);

        List<FinancialControlDTO> list = financialControlDaoService.getFcDTOListInDateRangeByСategory(6l, start, end);
        List<Slice> sliceList = new ArrayList<>();
        List<SubcategoryDTO> subcategoryList = categoryDaoService.getAllSubcategories();

        subcategoryList.forEach(subcategoryDTO -> {
            switch (subcategoryDTO.getSubcategory()) {
                case REST_OTHER:
                    subcategoryDTO.setColor("0f3977");
                    break;
                case RENT:
                    subcategoryDTO.setColor("d32387");
                    break;
                case FUEL:
                    subcategoryDTO.setColor("56434e");
                    break;
                case RESTORATION:
                    subcategoryDTO.setColor("4c1a1a");
                    break;
                case PLANE:
                    subcategoryDTO.setColor("74e23d");
                    break;
            }
        });


        for (int i = 0; i < list.size(); i++) {
            SubcategoryDTO subcategoryDTO = getSubcategoryDTOByName(subcategoryList, list.get(i).getSubcategoryDTO().getSubcategory());
            sliceList.add(Slice.newSlice(
                    list.get(i).getAmount().intValue(),
                    Color.newColor(subcategoryDTO.getColor()),
                    String.valueOf(subcategoryDTO.getNameRu()),
                    String.valueOf(list.get(i).getAmount()))
            );


            PieChart chart = GCharts.newPieChart(sliceList);
            chart.setTitle("Finance report", BLACK, 16);
            chart.setSize(500, 200);
            chart.setThreeD(true);
            log.info(chart.toURLString());
        }
    }

    @Test
    public void testCurrencies() {
        currencyDaoService.getCurrencyDtoList();
        List<CurrencyDTO> currencyDtoList = currencyDaoService.getCurrencyDtoList();
        log.info("ok");
    }

    @Test
    public void test() {
        CategoryDTO categoryDTO = CategoryHelper.findCategoryDtoById(categoryDaoService.getAllCategories(), 2l);

    }

    private SubcategoryDTO getSubcategoryDTOByName(List<SubcategoryDTO> subcategoryList, Subcategory subcategory) {
        if (Objects.nonNull(subcategoryList) && Objects.nonNull(subcategory))
            return subcategoryList
                    .stream()
                    .filter(subcategoryDTO -> Objects.equals(subcategoryDTO.getSubcategory(), subcategory))
                    .findFirst().orElse(null);

        return null;
    }
}