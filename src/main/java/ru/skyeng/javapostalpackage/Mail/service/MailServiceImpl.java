package ru.skyeng.javapostalpackage.Mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skyeng.javapostalpackage.Exception.model.NotFoundException;
import ru.skyeng.javapostalpackage.Mail.dto.MailDto;
import ru.skyeng.javapostalpackage.Mail.dto.MailMapper;
import ru.skyeng.javapostalpackage.Mail.model.Mail;
import ru.skyeng.javapostalpackage.Mail.repository.MailRepository;
import ru.skyeng.javapostalpackage.PostOffice.dto.postalHistory.PostalHistoryDtoOut;
import ru.skyeng.javapostalpackage.PostOffice.dto.postalHistory.PostalHistoryMapper;
import ru.skyeng.javapostalpackage.PostOffice.model.PostOffice;
import ru.skyeng.javapostalpackage.PostOffice.repository.PostOfficeRepository;
import ru.skyeng.javapostalpackage.PostOffice.repository.PostalHistoryRepository;
import ru.skyeng.javapostalpackage.User.model.User;
import ru.skyeng.javapostalpackage.User.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MailServiceImpl implements MailService {

    private final MailRepository repository;

    private final UserRepository userRepository;

    private final PostalHistoryRepository historyRepository;

    private final PostOfficeRepository officeRepository;

    @Override
    public List<MailDto> getAll() {
        log.info("Mail Get All");
        return MailMapper.toMailDto(repository.findAll());
    }

    @Override
    @Transactional
    public MailDto create(MailDto mailDto, long userId) {
        log.info("Mail Post with userId = {}, Mail = {}", userId, mailDto);
        User owner = checkUser(userId);
        PostOffice office;
        if (mailDto.getOfficeId() != null) {
            office = officeRepository.findById(mailDto.getOfficeId()).orElseThrow(
                    () -> new NotFoundException(
                            String.format("Office по officeId = %s не найден", mailDto.getOfficeId())
                    )
            ); // Если пользователь не будет выбирать офис, система выберет за него
        } else { // Сделал примитивно, но можно добавить логику по поиску ближнего отделения
            List<PostOffice> offices = officeRepository.findAll();
            if (offices.isEmpty()) throw new NotFoundException("В базе данных нет ни одного PostOffice");
            office = offices.get(0);
        }
        return MailMapper.toMailDto(
                repository.save(
                        MailMapper.toMail(mailDto, owner, office)
                )
        );
    }

    @Override
    @Transactional
    public void deleteById(long mailId, long userId) {
        log.info("Mail Delete with mailId = {}, userId = {}", mailId, userId);
        repository.deleteByIdAndOwnerId(mailId, userId);
    }

    @Override
    public MailDto getMailById(long mailId, long userId) {
        log.info("Mail Get by mailId = {}, userId = {}", mailId, userId);
        return MailMapper.toMailDto(
                repository.findByIdAndOwnerId(mailId, userId).orElseThrow(
                        () -> new NotFoundException(String.format("Mail по id = %s , ownerId = %s не найден", mailId, userId))
                )
        );
    }

    @Override
    public List<PostalHistoryDtoOut> getHistory(long mailId, long userId) {
        log.info("Mail GetHistory with mailId = {}, userId = {}", mailId, userId);
        Mail oldMail = repository.findByIdWithAll(mailId).orElseThrow(
                () -> new NotFoundException(String.format("Mail по id = %s не найден", mailId))
        );
        if (oldMail.getOwner().getId() != userId) {
            throw new IllegalArgumentException(
                    String.format("Ошибка, userId = %s не является получателем", userId)
            );
        }

        return PostalHistoryMapper.toHistoryDtoOut(
                historyRepository.findAllByMailIdOrderByCreated(mailId)
        );
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User по id = %s не найден", userId)));
    }
}
