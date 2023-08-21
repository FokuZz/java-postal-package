package ru.skyeng.javapostalpackage.Mail.service;

import ru.skyeng.javapostalpackage.Mail.dto.MailDto;
import ru.skyeng.javapostalpackage.PostOffice.dto.postalHistory.PostalHistoryDtoOut;

import java.util.List;

public interface MailService {
    List<MailDto> getAll();

    MailDto create(MailDto mailDto, long userId);

    void deleteById(long mailId, long userId);

    MailDto getMailById(long mailId, long userId);


    List<PostalHistoryDtoOut> getHistory(long mailId, long userId);
}