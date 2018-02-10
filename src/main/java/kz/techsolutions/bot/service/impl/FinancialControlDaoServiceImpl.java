package kz.techsolutions.bot.service.impl;

import kz.techsolutions.bot.api.CategoryDaoService;
import kz.techsolutions.bot.api.CurrencyDaoService;
import kz.techsolutions.bot.api.FinancialControlDaoService;
import kz.techsolutions.bot.api.dto.FinancialControlDTO;
import kz.techsolutions.bot.api.dto.Language;
import kz.techsolutions.bot.api.dto.PersonDTO;
import kz.techsolutions.bot.api.dto.Subcategory;
import kz.techsolutions.bot.api.mapper.FinancialControlCategoryDTOMapper;
import kz.techsolutions.bot.api.mapper.FinancialControlDTOMapper;
import kz.techsolutions.bot.api.mapper.FinancialControlSubcategoryDTOMapper;
import kz.techsolutions.bot.api.mapper.PersonDTOMapper;
import kz.techsolutions.bot.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@Primary
public class FinancialControlDaoServiceImpl implements FinancialControlDaoService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CategoryDaoService categoryDaoService;

    @Autowired
    private CurrencyDaoService currencyDaoService;

    private static final String ADD_PERSON_SQL = "INSERT INTO FC.PERSONS(NAME,SURNAME,USERNAME, LANGID) VALUES(?,?,?,?)";
    private static final String UPDATE_PERSON_SQL = "UPDATE FC.PERSONS SET NAME=?, SURNAME=?, LANGID=?, CURRENCY_ID=? WHERE ID=?";
    private static final String ADD_FC_SQL = "INSERT INTO FC.FINANCIAL_CONTROL(SUBCATEGORYID, EVENTTIME, AMOUNT, INSERTEDTIME, PERSONID) VALUES(?,?,?,TIMESTAMP 'now',?)";
    private static final String GET_PERSON_BY_USERNAME_SQL = "SELECT * FROM FC.PERSONS WHERE USERNAME=?";
    private static final String GET_DETAILED_DATA_SQL = "SELECT fc.id as fc_id,fc.subcategoryid,fc.eventtime,fc.amount,fc.insertedtime,p.* " +
            "FROM FC.FINANCIAL_CONTROL fc, FC.PERSONS p " +
            "WHERE fc.personid=p.id AND p.id=? AND eventtime BETWEEN ? AND ?";

    private static final String GET_DATA_BY_SUBCATEGORY_SQL = "SELECT subcategoryid, sum(amount), personId " +
            "FROM FC.FINANCIAL_CONTROL fc " +
            "WHERE personId = ? " +
            "AND eventtime BETWEEN ? AND ? " +
            "GROUP BY subcategoryid, personId";

    private static final String GET_DATA_BY_CATEGORY_SQL = "SELECT personId, c.id as categoryId, sum (amount) " +
            "FROM FC.FINANCIAL_CONTROL fc, FC.SUBCATEGORY s, FC.CATEGORY c " +
            "WHERE fc.personId = ? " +
            "AND s.id = fc.subcategoryId " +
            "AND c.id = s.categoryId " +
            "AND eventtime BETWEEN ? AND ? " +
            "GROUP BY c.id, personId";

    @Override
    public void addFcInfo(FinancialControlDTO financialControlDTO) {
        jdbcTemplate.update(ADD_FC_SQL, new Object[]{
                Subcategory.getId(Objects.nonNull(financialControlDTO.getSubcategoryDTO()) ? financialControlDTO.getSubcategoryDTO().getSubcategory() : null),
                DateTimeUtils.toTimestamp(financialControlDTO.getEventTime()),
                financialControlDTO.getAmount(),
                Objects.nonNull(financialControlDTO.getPersonDTO()) ? financialControlDTO.getPersonDTO().getId() : null
        });
    }

    @Override
    public void addPerson(PersonDTO personDTO) {
        jdbcTemplate.update(ADD_PERSON_SQL, new Object[]{
                personDTO.getFirstName(),
                personDTO.getLastName(),
                personDTO.getUsername(),
                Language.getId(personDTO.getLanguage())
        });
    }

    @Override
    public void updatePerson(PersonDTO personDTO) {
        jdbcTemplate.update(UPDATE_PERSON_SQL, new Object[]{
                personDTO.getFirstName(),
                personDTO.getLastName(),
                Language.getId(personDTO.getLanguage()),
                personDTO.getCurrencyDTO().getId(),
                personDTO.getId()
        });
    }

    @Override
    public void updateAccountInfo(FinancialControlDTO financialControlDTO) {
        throw new UnsupportedOperationException("This method is unnecessary");
    }

    @Override
    public PersonDTO getPersonByUsername(String username) {
        try {
            return (PersonDTO) jdbcTemplate.queryForObject(
                    GET_PERSON_BY_USERNAME_SQL,
                    new Object[]{username},
                    new PersonDTOMapper(currencyDaoService.getCurrencyDtoList())
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<FinancialControlDTO> getFcDTOListInDateRange(Long personId, LocalDateTime startTime, LocalDateTime endTime) {
        return getFcDtoListInternal(personId, startTime, endTime, GET_DETAILED_DATA_SQL, new FinancialControlDTOMapper(new PersonDTOMapper(currencyDaoService.getCurrencyDtoList()), categoryDaoService.getAllSubcategories()));
    }

    @Override
    public List<FinancialControlDTO> getFcDTOListInDateRangeBySubcategory(Long personId, LocalDateTime startTime, LocalDateTime endTime) {
        return getFcDtoListInternal(personId, startTime, endTime, GET_DATA_BY_SUBCATEGORY_SQL, new FinancialControlSubcategoryDTOMapper(categoryDaoService.getAllSubcategories()));
    }

    @Override
    public List<FinancialControlDTO> getFcDTOListInDateRangeByСategory(Long personId, LocalDateTime startTime, LocalDateTime endTime) {
        return getFcDtoListInternal(personId, startTime, endTime, GET_DATA_BY_CATEGORY_SQL, new FinancialControlCategoryDTOMapper(categoryDaoService.getAllCategories()));
    }

    private List<FinancialControlDTO> getFcDtoListInternal(Long personId, LocalDateTime startTime, LocalDateTime endTime, String query, RowMapper rowMapper) {
        return jdbcTemplate.query(
                query,
                new Object[]{
                        personId,
                        DateTimeUtils.toTimestamp(startTime),
                        DateTimeUtils.toTimestamp(endTime)
                },
                rowMapper
        );
    }
}