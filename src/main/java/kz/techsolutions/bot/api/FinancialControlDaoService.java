package kz.techsolutions.bot.api;

import kz.techsolutions.bot.api.dto.FinancialControlDTO;
import kz.techsolutions.bot.api.dto.PersonDTO;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public interface FinancialControlDaoService {

    void addFcInfo(@NotNull FinancialControlDTO financialControlDTO);

    void addPerson(@NotNull PersonDTO personDTO);

    void updatePerson(@NotNull PersonDTO personDTO);

    void updateAccountInfo(@NotNull FinancialControlDTO financialControlDTO);

    PersonDTO getPersonByUsername(@NotNull String username);

    List<FinancialControlDTO> getFcDTOListInDateRange(@NotNull Long personId,
                                                      @NotNull LocalDateTime startTime,
                                                      @NotNull LocalDateTime endTime);

    List<FinancialControlDTO> getFcDTOListInDateRangeBySubcategory(@NotNull Long personId,
                                                                   @NotNull LocalDateTime startTime,
                                                                   @NotNull LocalDateTime endTime);

    List<FinancialControlDTO> getFcDTOListInDateRangeByСategory(@NotNull Long personId,
                                                                @NotNull LocalDateTime startTime,
                                                                @NotNull LocalDateTime endTime);
}