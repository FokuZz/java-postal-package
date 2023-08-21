package ru.skyeng.javapostalpackage.PostOffice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skyeng.javapostalpackage.PostOffice.dto.postOffice.PostOfficeDto;
import ru.skyeng.javapostalpackage.PostOffice.dto.postalHistory.PostalHistoryDtoOut;
import ru.skyeng.javapostalpackage.PostOffice.service.PostOfficeService;
import ru.skyeng.javapostalpackage.validation.ValidationGroups;

import java.util.List;

@RestController
@RequestMapping(path = "/post_office")
@RequiredArgsConstructor
@Validated
public class PostOfficeController {
    private final PostOfficeService service;

    @GetMapping
    public List<PostOfficeDto> getAll() {
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(ValidationGroups.Create.class)
    public PostOfficeDto createOffice(@RequestBody @Valid PostOfficeDto officeDto) {
        return service.create(officeDto);
    }

    @DeleteMapping("/{officeId}")
    public void deleteOffice(@PathVariable(name = "officeId")
                             @Positive(message = "Id не может быть отрицательным") Long officeId) {
        service.deleteById(officeId);
    }

    @PatchMapping("/{officeId}")
    public PostOfficeDto updateOffice(@PathVariable(name = "officeId")
                                      @Positive(message = "Id не может быть отрицательным") Long officeId,
                                      @RequestBody @Valid PostOfficeDto postOfficeDto) {
        return service.updateOffice(officeId, postOfficeDto);
    }

    //Методы связанные с управлением отправления

    @PostMapping("/mail/{mailId}/register") // Регистрация отравления
    public PostalHistoryDtoOut registerMailInfo(@PathVariable(name = "mailId")
                                                @Positive(message = "Id не может быть отрицательным") Long mailId) {
        return service.registerMailInfo(mailId);
    }

    @PostMapping("/mail/{mailId}/intermediate_arrival") // Прибытие в промежуточное отделение
    public PostalHistoryDtoOut intermediatePostOffice(@PathVariable(name = "mailId")
                                                      @Positive(message = "Id не может быть отрицательным") Long mailId) {
        return service.intermediatePostOfficeInfo(mailId);
    }

    @PostMapping("/mail/{mailId}/intermediate_leave") // Убытие из промежуточного отделения
    public PostalHistoryDtoOut intermediatePostOfficeLeave(@PathVariable(name = "mailId")
                                                           @Positive(message = "Id не может быть отрицательным") Long mailId) {
        return service.intermediatePostOfficeLeaveInfo(mailId);
    }

    @PostMapping("/mail/{mailId}/arrival/{officeId}") // Прибытие в почтовый отдел
    public PostalHistoryDtoOut arrivalInPostOffice(@PathVariable(name = "mailId")
                                                   @Positive(message = "Id не может быть отрицательным") Long mailId,
                                                   @PathVariable(name = "officeId")
                                                   @Positive(message = "Id не может быть отрицательным") Long officeId) {
        return service.arrivalInPostOffice(mailId, officeId);
    }

    @PostMapping("/mail/{mailId}/receiving") // Получение отправления
    public PostalHistoryDtoOut receivingInfo(@PathVariable(name = "mailId")
                                             @Positive(message = "Id не может быть отрицательным") Long mailId) {
        return service.receivingInfo(mailId);
    }
}
