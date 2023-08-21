package ru.skyeng.javapostalpackage.Mail;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skyeng.javapostalpackage.Mail.dto.MailDto;
import ru.skyeng.javapostalpackage.Mail.model.TypeMail;
import ru.skyeng.javapostalpackage.Mail.service.MailService;
import ru.skyeng.javapostalpackage.PostOffice.dto.postalHistory.PostalHistoryDtoOut;
import ru.skyeng.javapostalpackage.validation.ValidationGroups;

import java.util.List;

@RestController
@RequestMapping(path = "/user/mail")
@RequiredArgsConstructor
@Validated
public class MailController {
    private final MailService service;

    @GetMapping
    public List<MailDto> getAll() {
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(ValidationGroups.Create.class)
    public MailDto create(@RequestBody @Valid MailDto mailDto,
                          @RequestHeader("X-Package-User-Id")
                          @Positive(message = "Id не может быть отрицательным") long userId) {
        if (TypeMail.from(mailDto.getTypeMail().toString()).isEmpty()) {
            throw new IllegalArgumentException("Такого TypeMail не существует!");
        }
        return service.create(mailDto, userId);
    }

    @DeleteMapping("/{mailId}")
    public void delete(@PathVariable(name = "mailId")
                       @Positive(message = "Id не может быть отрицательным") long mailId,
                       @RequestHeader("X-Package-User-Id")
                       @Positive(message = "Id не может быть отрицательным") long userId) {
        service.deleteById(mailId, userId);
    }

    @GetMapping("/{mailId}")
    public MailDto getById(@PathVariable(name = "mailId")
                           @Positive(message = "Id не может быть отрицательным") long mailId,
                           @RequestHeader("X-Package-User-Id")
                           @Positive(message = "Id не может быть отрицательным") long userId) {
        return service.getMailById(mailId, userId);
    }

    @GetMapping("/{mailId}/history")
    public List<PostalHistoryDtoOut> getHistory(@PathVariable(name = "mailId")
                                                @Positive(message = "Id не может быть отрицательным") long mailId,
                                                @RequestHeader("X-Package-User-Id")
                                                @Positive(message = "Id не может быть отрицательным") long userId) {
        return service.getHistory(mailId, userId);
    }
}
