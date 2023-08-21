package ru.skyeng.javapostalpackage.PostOffice.service;

import ru.skyeng.javapostalpackage.PostOffice.dto.postOffice.PostOfficeDto;
import ru.skyeng.javapostalpackage.PostOffice.dto.postalHistory.PostalHistoryDtoOut;

import java.util.List;


public interface PostOfficeService {
    List<PostOfficeDto> getAll();

    PostOfficeDto getOfficeById(long officeId);

    PostOfficeDto create(PostOfficeDto officeDto);

    void deleteById(long officeId);

    PostOfficeDto updateOffice(long officeId, PostOfficeDto officeDto);

    PostalHistoryDtoOut registerMailInfo(long mailId);

    PostalHistoryDtoOut intermediatePostOfficeInfo(long mailId);

    PostalHistoryDtoOut intermediatePostOfficeLeaveInfo(long mailId);

    PostalHistoryDtoOut arrivalInPostOffice(long mailId, long officeId);

    PostalHistoryDtoOut receivingInfo(long mailId);
}

