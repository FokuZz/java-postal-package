package ru.skyeng.javapostalpackage.PostOffice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skyeng.javapostalpackage.Exception.model.NotFoundException;
import ru.skyeng.javapostalpackage.Mail.model.Mail;
import ru.skyeng.javapostalpackage.Mail.model.TypeMail;
import ru.skyeng.javapostalpackage.Mail.repository.MailRepository;
import ru.skyeng.javapostalpackage.PostOffice.dto.postOffice.PostOfficeDto;
import ru.skyeng.javapostalpackage.PostOffice.dto.postOffice.PostOfficeMapper;
import ru.skyeng.javapostalpackage.PostOffice.dto.postalHistory.PostalHistoryDtoOut;
import ru.skyeng.javapostalpackage.PostOffice.dto.postalHistory.PostalHistoryMapper;
import ru.skyeng.javapostalpackage.PostOffice.model.PostOffice;
import ru.skyeng.javapostalpackage.PostOffice.model.PostalHistory;
import ru.skyeng.javapostalpackage.PostOffice.repository.PostOfficeRepository;
import ru.skyeng.javapostalpackage.PostOffice.repository.PostalHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PostOfficeServiceImpl implements PostOfficeService {

    private final PostOfficeRepository repository;

    private final PostalHistoryRepository postalHistoryRepository;

    private final MailRepository mailRepository;

    @Override
    public List<PostOfficeDto> getAll() {
        log.info("PostOffice Get All");
        return PostOfficeMapper.toOfficeDto(repository.findAll());
    }

    @Override
    public PostOfficeDto getOfficeById(long officeId) {
        log.info("PostOffice Get by id = {} ", officeId);
        return PostOfficeMapper.toOfficeDto(getOffice(officeId));
    }

    @Override
    @Transactional
    public PostOfficeDto create(PostOfficeDto officeDto) {
        log.info("PostOffice Post postOffice = {}", officeDto);
        return PostOfficeMapper.toOfficeDto(
                repository.save(
                        PostOfficeMapper.toOffice(officeDto)
                )
        );
    }

    @Override
    public void deleteById(long officeId) {
        log.info("PostOffice Delete by id = {} ", officeId);
        repository.deleteById(officeId);
    }

    @Override
    @Transactional
    public PostOfficeDto updateOffice(long officeId, PostOfficeDto officeDto) {
        log.info("PostOffice Patch by officeId = {}, postOffice = {}", officeId, officeDto);
        PostOffice oldOffice = getOffice(officeId);
        if (officeDto.getPostalIndex() != null) oldOffice.setPostalIndex(officeDto.getPostalIndex());
        if (officeDto.getName() != null
                && !officeDto.getName().isBlank()) oldOffice.setName(officeDto.getName());
        if (officeDto.getAddress() != null
                && !officeDto.getAddress().isBlank()) oldOffice.setAddress(officeDto.getAddress());

        return PostOfficeMapper.toOfficeDto(repository.save(oldOffice));
    }

    @Override
    @Transactional
    public PostalHistoryDtoOut registerMailInfo(long mailId) {
        log.info("PostalHistory registerMailInfo with mailId = {}", mailId);
        PostalHistoryDtoOut history = new PostalHistoryDtoOut();
        Mail mail = mailRepository.findById(mailId).orElseThrow(
                () -> new NotFoundException(String.format("Mail по id = %s не найден", mailId))
        );
        history.setInfo(registerTypeTranslator(mail.getTypeMail()));
        history.setMailId(mailId);
        history.setCreated(LocalDateTime.now());
        String info = history.getInfo();
        if (postalHistoryRepository.findByMailIdAndInfo(mailId, info).isPresent()) {
            throw new IllegalArgumentException("Посылка уже была зарегистрирована");
        }
        PostalHistory historyOut = postalHistoryRepository.save(PostalHistoryMapper.toHistory(history, mail));
        return PostalHistoryMapper.toHistoryDtoOut(historyOut);
    }

    @Override
    @Transactional
    public PostalHistoryDtoOut intermediatePostOfficeInfo(long mailId) {
        log.info("PostalHistory intermediatePostOfficeInfo with mailId = {}", mailId);
        Mail mail = mailRepository.findById(mailId).orElseThrow(
                () -> new NotFoundException(String.format("Mail по id = %s не найден", mailId))
        );
        if (postalHistoryRepository.findByMailId(mailId).size() != 1) {
            throw new IllegalArgumentException(
                    "Заявлять о прибытии в промежуточное отделение можно только после регистрации"
            );
        }
        String info = "Отправление пришло в промежуточное отделение";
        if (postalHistoryRepository.findByMailIdAndInfo(mailId, info).isPresent()) {
            throw new IllegalArgumentException("Прибытие уже было зарегистрировано");
        }
        PostalHistoryDtoOut history = new PostalHistoryDtoOut();

        history.setInfo(info);
        history.setMailId(mailId);
        history.setCreated(LocalDateTime.now());
        PostalHistory historyOut = postalHistoryRepository.save(PostalHistoryMapper.toHistory(history, mail));
        return PostalHistoryMapper.toHistoryDtoOut(historyOut);
    }

    @Override
    @Transactional
    public PostalHistoryDtoOut intermediatePostOfficeLeaveInfo(long mailId) {
        log.info("PostalHistory intermediatePostOfficeLeaveInfo with mailId = {}", mailId);
        Mail mail = mailRepository.findById(mailId).orElseThrow(
                () -> new NotFoundException(String.format("Mail по id = %s не найден", mailId))
        );
        if (postalHistoryRepository.findByMailId(mailId).isEmpty()) {
            throw new IllegalArgumentException(
                    "Заявлять о отбытии из промежуточное отделения можно только после прибытия"
            );
        }
        String info = "Отправление покинуло промежуточное отделение";
        if (postalHistoryRepository.findByMailId(mailId).size() != 2) {
            throw new IllegalArgumentException("Убытие уже было зарегистрировано");
        }
        PostalHistoryDtoOut history = new PostalHistoryDtoOut();

        history.setInfo(info);
        history.setMailId(mailId);
        history.setCreated(LocalDateTime.now());
        PostalHistory historyOut = postalHistoryRepository.save(PostalHistoryMapper.toHistory(history, mail));
        return PostalHistoryMapper.toHistoryDtoOut(historyOut);
    }

    @Override
    public PostalHistoryDtoOut arrivalInPostOffice(long mailId, long officeId) {
        log.info("PostalHistory arrivalInPostOffice with mailId = {}", mailId);
        PostalHistoryDtoOut history = new PostalHistoryDtoOut();
        Mail mail = mailRepository.findById(mailId).orElseThrow(
                () -> new NotFoundException(String.format("Mail по id = %s не найден", mailId))
        );
        PostOffice postOffice = repository.findById(officeId).orElseThrow(
                () -> new NotFoundException(String.format("PostOffice по id = %s не найден", officeId))
        );
        if (postalHistoryRepository.findByMailId(mailId).size() != 3) {
            throw new IllegalArgumentException(
                    "Заявлять о прибытии в отдел почты можно только после отбытия из промежуточного отделения"
            );
        }
        String info = String.format("Отправление прибыло в почтовое отделение по адресу (%s)", postOffice.getAddress());
        if (postalHistoryRepository.findByMailIdAndInfo(mailId, info).isPresent()) {
            throw new IllegalArgumentException("Прибытие уже было зарегистрировано");
        }
        history.setInfo(info);
        history.setMailId(mailId);
        history.setCreated(LocalDateTime.now());
        PostalHistory historyOut = postalHistoryRepository.save(PostalHistoryMapper.toHistory(history, mail));
        return PostalHistoryMapper.toHistoryDtoOut(historyOut);
    }

    @Override
    @Transactional
    public PostalHistoryDtoOut receivingInfo(long mailId) {
        log.info("PostalHistory receivingInfo with mailId = {}", mailId);
        Mail mail = mailRepository.findById(mailId).orElseThrow(
                () -> new NotFoundException(String.format("Mail по id = %s не найден", mailId))
        );
        if (postalHistoryRepository.findByMailId(mailId).size() != 4) {
            throw new IllegalArgumentException(
                    "Вручение может быть после прибытия в отдел почты"
            );
        }
        String info = "Отправление было вручено получателю";
        if (postalHistoryRepository.findByMailIdAndInfo(mailId, info).isPresent()) {
            throw new IllegalArgumentException("Вручение уже было зарегистрировано");
        }
        PostalHistoryDtoOut history = new PostalHistoryDtoOut();
        history.setInfo(info);
        history.setMailId(mailId);
        history.setCreated(LocalDateTime.now());
        PostalHistory historyOut = postalHistoryRepository.save(PostalHistoryMapper.toHistory(history, mail));
        return PostalHistoryMapper.toHistoryDtoOut(historyOut);
    }

    private PostOffice getOffice(long officeId) {
        return repository.findById(officeId).orElseThrow(
                () -> new NotFoundException(String.format("PostOffice по id = %s не найден", officeId))
        );
    }

    private String registerTypeTranslator(TypeMail type) {
        switch (type) {
            case LETTER -> {
                return "Письмо было зарегистрировано";
            }
            case PARCEL -> {
                return "Бандероль был зарегистрирован";
            }
            case PACKAGE -> {
                return "Посылка была зарегистрирована";
            }
            case POSTCARD -> {
                return "Открытка была зарегистрирована";
            }
            default -> {
                log.error("ОШИБКА: Несуществующий TypeMail");
                throw new IllegalArgumentException("Несуществующий TypeMail");
            }
        }
    }
}
