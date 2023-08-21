package ru.skyeng.javapostalpackage.PostOffice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skyeng.javapostalpackage.Exception.model.NotFoundException;
import ru.skyeng.javapostalpackage.Mail.model.Mail;
import ru.skyeng.javapostalpackage.Mail.model.TypeMail;
import ru.skyeng.javapostalpackage.Mail.repository.MailRepository;
import ru.skyeng.javapostalpackage.PostOffice.dto.postOffice.PostOfficeDto;
import ru.skyeng.javapostalpackage.PostOffice.dto.postOffice.PostOfficeMapper;
import ru.skyeng.javapostalpackage.PostOffice.dto.postalHistory.PostalHistoryDtoOut;
import ru.skyeng.javapostalpackage.PostOffice.model.PostOffice;
import ru.skyeng.javapostalpackage.PostOffice.model.PostalHistory;
import ru.skyeng.javapostalpackage.PostOffice.repository.PostOfficeRepository;
import ru.skyeng.javapostalpackage.PostOffice.repository.PostalHistoryRepository;
import ru.skyeng.javapostalpackage.User.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostOfficeServiceImplTest {

    @InjectMocks
    PostOfficeServiceImpl service;

    @Mock
    PostOfficeRepository repository;

    @Mock
    MailRepository mailRepository;

    @Mock
    PostalHistoryRepository postalHistoryRepository;

    PostOffice.PostOfficeBuilder builder;

    PostOffice postOfficeWithIdOne;

    PostOffice getPostOfficeWithNoId;

    PostalHistory postalHistoryWithIdOne;

    Mail mail;

    @BeforeEach
    void setup() {
        builder = PostOffice.builder()
                .postalIndex("404221")
                .name("name")
                .address("address");
        postOfficeWithIdOne = builder.id(1L).build();
        getPostOfficeWithNoId = builder.build();
        User owner = User.builder()
                .id(1L)
                .email("email@gmail.com")
                .phoneNumber("+70005566771")
                .name("name")
                .lastName("lastName")
                .build();
        mail = Mail.builder()
                .id(1L)
                .typeMail(TypeMail.PACKAGE)
                .owner(owner)
                .office(postOfficeWithIdOne)
                .mailIndex("404222")
                .address("address")
                .build();
        postalHistoryWithIdOne = PostalHistory.builder()
                .id(1L)
                .mail(mail)
                .info("info")
                .created(LocalDateTime.now())
                .build();


    }

    @Test
    void testGetAllEmpty() {
        when(repository.findAll()).thenReturn(new ArrayList<>());
        List<PostOfficeDto> officeDtos = service.getAll();
        assertEquals(0, officeDtos.size());
    }

    @Test
    void testGetAllStandard() {
        when(repository.findAll()).thenReturn(List.of(postOfficeWithIdOne));
        List<PostOfficeDto> officeDtos = service.getAll();
        assertEquals(1, officeDtos.size());
    }

    @Test
    void testGetOfficeByIdWrongId() {
        String error = "PostOffice по id = 3 не найден";
        when(repository.findById(3L)).thenThrow(new NotFoundException(error));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getOfficeById(3));
        assertEquals(error, exception.getMessage());
    }

    @Test
    void testGetOfficeByIdStandard() {
        when(repository.findById(1L)).thenReturn(Optional.of(postOfficeWithIdOne));
        PostOfficeDto office = service.getOfficeById(1);
        assertEquals(1L, office.getId());
        assertEquals("name", office.getName());
    }

    @Test
    void testCreateStandard() {
        when(repository.save(getPostOfficeWithNoId)).thenReturn(postOfficeWithIdOne);
        PostOfficeDto office = service.create(
                PostOfficeMapper.toOfficeDto(getPostOfficeWithNoId)
        );
        assertEquals(1L, office.getId());
        assertEquals("name", office.getName());
    }

    @Test
    void testDeleteByIdStandard() {
        service.deleteById(1);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateOfficeFailWrongId() {
        String error = "PostOffice по id = 3 не найден";
        when(repository.findById(3L)).thenThrow(new NotFoundException(error));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.updateOffice(3, PostOfficeMapper.toOfficeDto(postOfficeWithIdOne)));
        assertEquals(error, exception.getMessage());
    }

    @Test
    void testUpdateOfficeStandard() {
        when(repository.findById(1L)).thenReturn(Optional.of(postOfficeWithIdOne));
        when(repository.save(postOfficeWithIdOne)).thenReturn(postOfficeWithIdOne);
        PostOfficeDto office = service.updateOffice(1L,
                PostOfficeMapper.toOfficeDto(postOfficeWithIdOne)
        );
        assertEquals(1L, office.getId());
        assertEquals("name", office.getName());
    }

    @Test
    void testRegisterMailInfoWrongMailId() {
        String error = "Mail по id = 5 не найден";
        when(mailRepository.findById(5L)).thenThrow(new NotFoundException(error));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.registerMailInfo(5));
        assertEquals(error, exception.getMessage());
    }

    @Test
    void testRegisterMailInfoStandard() {
        when(mailRepository.findById(1L)).thenReturn(Optional.of(mail));
        when(postalHistoryRepository.save(any())).thenReturn(postalHistoryWithIdOne);
        PostalHistoryDtoOut history = service.registerMailInfo(1L);
        assertEquals(1L, history.getId());
        assertEquals("info", history.getInfo());
    }


    @Test
    void testIntermediatePostOfficeInfoWrongMailId() {
        String error = "Mail по id = 5 не найден";
        when(mailRepository.findById(5L)).thenThrow(new NotFoundException(error));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.intermediatePostOfficeInfo(5));
        assertEquals(error, exception.getMessage());
    }

    @Test
    void testIntermediatePostOfficeInfoStandard() {
        when(mailRepository.findById(1L)).thenReturn(Optional.of(mail));
        when(postalHistoryRepository.findByMailId(1L)).thenReturn(
                List.of(postalHistoryWithIdOne)
        );
        when(postalHistoryRepository.save(any())).thenReturn(postalHistoryWithIdOne);

        PostalHistoryDtoOut history = service.intermediatePostOfficeInfo(1L);
        assertEquals(1L, history.getId());
        assertEquals("info", history.getInfo());
    }

    @Test
    void testIntermediatePostOfficeLeaveInfoWrongMailId() {
        String error = "Mail по id = 5 не найден";
        when(mailRepository.findById(5L)).thenThrow(new NotFoundException(error));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.intermediatePostOfficeLeaveInfo(5));
        assertEquals(error, exception.getMessage());
    }

    @Test
    void testIntermediatePostOfficeLeaveInfoStandard() {
        when(mailRepository.findById(1L)).thenReturn(Optional.of(mail));
        when(postalHistoryRepository.save(any())).thenReturn(postalHistoryWithIdOne);
        when(postalHistoryRepository.findByMailId(1L)).thenReturn(
                List.of(postalHistoryWithIdOne, postalHistoryWithIdOne)
        );
        PostalHistoryDtoOut history = service.intermediatePostOfficeLeaveInfo(1L);
        assertEquals(1L, history.getId());
        assertEquals("info", history.getInfo());
    }

    @Test
    void testArrivalInPostOfficeWrongMailId() {
        String error = "Mail по id = 5 не найден";
        when(mailRepository.findById(5L)).thenThrow(new NotFoundException(error));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.arrivalInPostOffice(5, 1));
        assertEquals(error, exception.getMessage());
    }

    @Test
    void testArrivalInPostOfficeWrongOfficeId() {
        String error = "PostOffice по id = 5 не найден";
        when(mailRepository.findById(1L)).thenReturn(Optional.of(mail));
        when(repository.findById(5L)).thenThrow(new NotFoundException(error));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.arrivalInPostOffice(1, 5));
        assertEquals(error, exception.getMessage());
    }

    @Test
    void testArrivalInPostOfficeStandard() {
        when(mailRepository.findById(1L)).thenReturn(Optional.of(mail));
        when(postalHistoryRepository.findByMailId(1L)).thenReturn(
                List.of(postalHistoryWithIdOne, postalHistoryWithIdOne, postalHistoryWithIdOne)
        );
        when(repository.findById(1L)).thenReturn(Optional.of(postOfficeWithIdOne));
        when(postalHistoryRepository.save(any())).thenReturn(postalHistoryWithIdOne);
        PostalHistoryDtoOut history = service.arrivalInPostOffice(1L, 1L);
        assertEquals(1L, history.getId());
        assertEquals("info", history.getInfo());
    }

    @Test
    void testReceivingInfoWrongMailId() {
        String error = "Mail по id = 5 не найден";
        when(mailRepository.findById(5L)).thenThrow(new NotFoundException(error));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.receivingInfo(5));
        assertEquals(error, exception.getMessage());
    }

    @Test
    void testReceivingInfoStandard() {
        when(mailRepository.findById(1L)).thenReturn(Optional.of(mail));
        when(postalHistoryRepository.findByMailId(1L)).thenReturn(List.of(
                postalHistoryWithIdOne, postalHistoryWithIdOne,
                postalHistoryWithIdOne, postalHistoryWithIdOne));
        when(postalHistoryRepository.save(any())).thenReturn(postalHistoryWithIdOne);
        PostalHistoryDtoOut history = service.receivingInfo(1L);
        assertEquals(1L, history.getId());
        assertEquals("info", history.getInfo());
    }
}