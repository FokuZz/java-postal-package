package ru.skyeng.javapostalpackage.Mail.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skyeng.javapostalpackage.Exception.model.NotFoundException;
import ru.skyeng.javapostalpackage.Mail.dto.MailDto;
import ru.skyeng.javapostalpackage.Mail.dto.MailMapper;
import ru.skyeng.javapostalpackage.Mail.model.Mail;
import ru.skyeng.javapostalpackage.Mail.model.TypeMail;
import ru.skyeng.javapostalpackage.Mail.repository.MailRepository;
import ru.skyeng.javapostalpackage.PostOffice.dto.postalHistory.PostalHistoryDtoOut;
import ru.skyeng.javapostalpackage.PostOffice.model.PostOffice;
import ru.skyeng.javapostalpackage.PostOffice.model.PostalHistory;
import ru.skyeng.javapostalpackage.PostOffice.repository.PostOfficeRepository;
import ru.skyeng.javapostalpackage.PostOffice.repository.PostalHistoryRepository;
import ru.skyeng.javapostalpackage.User.model.User;
import ru.skyeng.javapostalpackage.User.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceImplTest {

    @InjectMocks
    MailServiceImpl service;

    @Mock
    MailRepository repository;

    @Mock
    UserRepository userRepository;

    @Mock
    PostOfficeRepository officeRepository;

    @Mock
    PostalHistoryRepository historyRepository;

    Mail.MailBuilder builder;

    Mail mailWithIdOne;

    Mail mailWithNoId;

    User owner;

    PostOffice office;

    PostalHistory postalHistoryWithIdOne;

    @BeforeEach
    void setup() {
        owner = User.builder()
                .id(2L)
                .email("email@gmail.com")
                .phoneNumber("+70005566771")
                .name("name")
                .lastName("lastName")
                .build();

        office = PostOffice.builder()
                .id(3L)
                .postalIndex("404221")
                .name("name")
                .address("address")
                .build();

        builder = Mail.builder()
                .typeMail(TypeMail.PACKAGE)
                .owner(owner)
                .office(office)
                .mailIndex("424222")
                .address("address");

        mailWithIdOne = builder.id(1L).build();
        mailWithNoId = builder.build();

        postalHistoryWithIdOne = PostalHistory.builder()
                .id(1L)
                .mail(mailWithIdOne)
                .info("info")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetAllEmpty() {
        when(repository.findAll()).thenReturn(new ArrayList<>());
        List<MailDto> mail = service.getAll();
        assertEquals(0, mail.size());
    }

    @Test
    void testGetAllStandard() {
        when(repository.findAll()).thenReturn(List.of(mailWithIdOne));
        List<MailDto> mail = service.getAll();
        assertEquals(1, mail.size());
    }

    @Test
    void testCreateEmptyOfficeDao() {
        String error = "В базе данных нет ни одного PostOffice";
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(officeRepository.findAll()).thenReturn(new ArrayList<>());
        MailDto mailDto = MailMapper.toMailDto(mailWithNoId);
        mailDto.setOfficeId(null);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.create(mailDto, 2)
        );
        assertEquals(error, exception.getMessage());
    }

    @Test
    void testCreateWrongUserId() {
        String error = "User по id = 3 не найден";
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.create(MailMapper.toMailDto(mailWithNoId), 3)
        );
        assertEquals(error, exception.getMessage());
    }

    @Test
    void testCreateStandard() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(officeRepository.findAll()).thenReturn(List.of(office));
        when(repository.save(mailWithNoId)).thenReturn(mailWithIdOne);
        MailDto mailDto = MailMapper.toMailDto(mailWithNoId);
        mailDto.setOfficeId(null);
        MailDto mailDtoNew = service.create(mailDto, 2);
        assertEquals(1L, mailDtoNew.getId());
        assertEquals(3L, mailDtoNew.getOfficeId());
    }

    @Test
    void testDeleteByIdStandard() {
        service.deleteById(1L, 2L);
        verify(repository, times(1)).deleteByIdAndOwnerId(1L, 2L);
    }

    @Test
    void testGetMailByIdWrongId() {
        String error = "Mail по id = 2 , ownerId = 2 не найден";
        when(repository.findByIdAndOwnerId(2L, 2L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getMailById(2, 2)
        );
        assertEquals(error, exception.getMessage());
    }

    @Test
    void testGetMailByIdStandard() {
        when(repository.findByIdAndOwnerId(1L, 2L)).thenReturn(Optional.of(mailWithIdOne));
        MailDto mailDto = service.getMailById(1, 2);
        assertEquals(1, mailDto.getId());
    }

    @Test
    void testGetHistoryWrongMailId() {
        String error = "Mail по id = 2 не найден";
        when(repository.findByIdWithAll(2L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getHistory(2, 2)
        );
        assertEquals(error, exception.getMessage());
    }

    @Test
    void testGetHistoryWrongUserId() {
        String error = "Ошибка, userId = 1 не является получателем";
        when(repository.findByIdWithAll(1L)).thenReturn(Optional.of(mailWithIdOne));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.getHistory(1, 1)
        );
        assertEquals(error, exception.getMessage());
    }

    @Test
    void testGetHistoryNoHistory() {
        when(repository.findByIdWithAll(1L)).thenReturn(Optional.of(mailWithIdOne));
        when(historyRepository.findAllByMailIdOrderByCreated(1L)).thenReturn(new ArrayList<>());
        List<PostalHistoryDtoOut> history = service.getHistory(1, 2);

        assertEquals(0, history.size());
    }

    @Test
    void testGetHistoryStandard() {
        when(repository.findByIdWithAll(1L)).thenReturn(Optional.of(mailWithIdOne));
        when(historyRepository.findAllByMailIdOrderByCreated(1L)).thenReturn(List.of(postalHistoryWithIdOne));
        List<PostalHistoryDtoOut> history = service.getHistory(1, 2);

        assertEquals(1, history.size());

    }

}