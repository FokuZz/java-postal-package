package ru.skyeng.javapostalpackage.PostOffice;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.skyeng.javapostalpackage.PostOffice.dto.postOffice.PostOfficeDto;
import ru.skyeng.javapostalpackage.PostOffice.dto.postalHistory.PostalHistoryDtoOut;
import ru.skyeng.javapostalpackage.PostOffice.service.PostOfficeService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostOfficeController.class)
class PostOfficeControllerTest {

    final static String URL = "/post_office";
    @MockBean
    PostOfficeService service;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    PostOfficeDto.PostOfficeDtoBuilder builder;

    PostOfficeDto postOfficeWithIdOne;

    PostOfficeDto getPostOfficeWithNoId;

    PostalHistoryDtoOut.PostalHistoryDtoOutBuilder historyBuilder;

    @BeforeEach
    void setup() {
        builder = PostOfficeDto.builder()
                .postalIndex("404221")
                .name("name")
                .address("address");
        postOfficeWithIdOne = builder.id(1L).build();
        getPostOfficeWithNoId = builder.build();

        historyBuilder = PostalHistoryDtoOut.builder()
                .mailId(1L);
    }


    @Test
    void testGetAllEmpty() throws Exception {
        when(service.getAll()).thenReturn(new ArrayList<>());
        mvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetAllStandard() throws Exception {
        when(service.getAll()).thenReturn(List.of(postOfficeWithIdOne));
        mvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

    }

    @Test
    void testCreateOfficeEmptyBody() throws Exception {
        String error = "Отсутствует тело запроса";

        mvc.perform(post(URL))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(error)));
    }

    @Test
    void testCreateOfficeStandard() throws Exception {
        String json = mapper.writeValueAsString(getPostOfficeWithNoId);
        when(service.create(getPostOfficeWithNoId)).thenReturn(postOfficeWithIdOne);
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")));
    }

    @Test
    void testDeleteOfficeEmptyOfficeId() throws Exception {
        mvc.perform(delete(URL + "/"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteOfficeWrongOfficeId() throws Exception {
        doThrow(new EmptyResultDataAccessException(1)).when(service).deleteById(5);
        String error = "В базе данных нет записи с таким идентификатором";

        mvc.perform(delete(URL + "/5"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(error)));
        verify(service, times(1)).deleteById(5);
    }

    @Test
    void testDeleteOfficeNegativeOfficeId() throws Exception {
        String error = "Id не может быть отрицательным";

        mvc.perform(delete(URL + "/-5"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testDeleteOfficeStandard() throws Exception {
        mvc.perform(delete(URL + "/1"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(service, times(1)).deleteById(1);
    }

    @Test
    void testUpdateOfficeEmptyOfficeId() throws Exception {
        String json = mapper.writeValueAsString(postOfficeWithIdOne);
        mvc.perform(patch(URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateOfficeWrongOfficeId() throws Exception {
        doThrow(new EmptyResultDataAccessException(1)).when(service).updateOffice(5, postOfficeWithIdOne);
        String error = "В базе данных нет записи с таким идентификатором";

        String json = mapper.writeValueAsString(postOfficeWithIdOne);
        mvc.perform(patch(URL + "/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(error)));

        verify(service, times(1)).updateOffice(5, postOfficeWithIdOne);
    }

    @Test
    void testUpdateOfficeNegativeUserId() throws Exception {
        String error = "Id не может быть отрицательным";
        String json = mapper.writeValueAsString(postOfficeWithIdOne);

        mvc.perform(patch(URL + "/-5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testUpdateOfficeStandard() throws Exception {
        PostOfficeDto postOfficePath = builder.id(1L).name("artemus").build();
        String json = mapper.writeValueAsString(postOfficePath);

        when(service.updateOffice(1L, postOfficePath)).thenReturn(postOfficePath);

        mvc.perform(patch(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk());

        verify(service, times(1)).updateOffice(1, postOfficePath);
    }

    //Тесты методов связанных с управлением отправления


    @Test
    void testRegisterMailInfoNegativeMailId() throws Exception {
        String error = "Id не может быть отрицательным";

        mvc.perform(post(URL + "/mail/-1/register"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testRegisterMailInfoStandard() throws Exception {
        String info = "Посылка была зарегистрирована";

        PostalHistoryDtoOut postalHistoryDtoOut = historyBuilder.info(info)
                .mailId(1L)
                .created(LocalDateTime.now())
                .build();

        when(service.registerMailInfo(1L)).thenReturn(postalHistoryDtoOut);

        mvc.perform(post(URL + "/mail/1/register"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info", is(info)));

        verify(service, times(1)).registerMailInfo(1);
    }

    @Test
    void testIntermediatePostOfficeNegativeMailId() throws Exception {
        String error = "Id не может быть отрицательным";

        mvc.perform(post(URL + "/mail/-1/intermediate_arrival"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testIntermediatePostOfficeStandard() throws Exception {
        String info = "Отправление пришло в промежуточное отделение";
        PostalHistoryDtoOut postalHistoryDtoOut = historyBuilder.info(info)
                .mailId(1L)
                .created(LocalDateTime.now())
                .build();

        when(service.intermediatePostOfficeInfo(1L)).thenReturn(postalHistoryDtoOut);

        mvc.perform(post(URL + "/mail/1/intermediate_arrival"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info", is(info)));

        verify(service, times(1)).intermediatePostOfficeInfo(1);
    }

    @Test
    void testIntermediatePostOfficeLeaveNegativeMailId() throws Exception {
        String error = "Id не может быть отрицательным";

        mvc.perform(post(URL + "/mail/-1/intermediate_leave"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testIntermediatePostOfficeLeaveStandard() throws Exception {
        String info = "Отправление покинуло промежуточное отделение";
        PostalHistoryDtoOut postalHistoryDtoOut = historyBuilder.info(info)
                .mailId(1L)
                .created(LocalDateTime.now())
                .build();

        when(service.intermediatePostOfficeLeaveInfo(1L)).thenReturn(postalHistoryDtoOut);

        mvc.perform(post(URL + "/mail/1/intermediate_leave"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info", is(info)));

        verify(service, times(1)).intermediatePostOfficeLeaveInfo(1);
    }

    @Test
    void testArrivalInPostOfficeNegativeMailId() throws Exception {
        String error = "Id не может быть отрицательным";

        mvc.perform(post(URL + "/mail/-1/arrival/1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testArrivalInPostOfficeNegativeOfficeId() throws Exception {
        String error = "Id не может быть отрицательным";

        mvc.perform(post(URL + "/mail/1/arrival/-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testArrivalInPostOfficeStandard() throws Exception {
        String info = "Отправление прибыло в почтовое отделение по адресу (address)";
        PostalHistoryDtoOut postalHistoryDtoOut = historyBuilder.info(info)
                .mailId(1L)
                .created(LocalDateTime.now())
                .build();

        when(service.arrivalInPostOffice(1L, 1L)).thenReturn(postalHistoryDtoOut);

        mvc.perform(post(URL + "/mail/1/arrival/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info", is(info)));

        verify(service, times(1)).arrivalInPostOffice(1L, 1L);
    }

    @Test
    void testReceivingInfoNegativeMailId() throws Exception {
        String error = "Id не может быть отрицательным";

        mvc.perform(post(URL + "/mail/-1/receiving"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testReceivingInfoStandard() throws Exception {
        String info = "Отправление было вручено получателю";
        PostalHistoryDtoOut postalHistoryDtoOut = historyBuilder.info(info)
                .mailId(1L)
                .created(LocalDateTime.now())
                .build();

        when(service.receivingInfo(1L)).thenReturn(postalHistoryDtoOut);

        mvc.perform(post(URL + "/mail/1/receiving"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info", is(info)));

        verify(service, times(1)).receivingInfo(1);
    }

    // Тесты по валидации класса PostOfficeDto

    @Test
    void testValidationPostalIndexSize() throws Exception {
        String error = "Нельзя использовать меньше или больше 6 символов";
        PostOfficeDto postOfficeDto = builder.postalIndex("12345").build();
        String json = mapper.writeValueAsString(postOfficeDto);
        when(service.create(getPostOfficeWithNoId)).thenReturn(postOfficeWithIdOne);
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testValidationPostalIndexBlank() throws Exception {
        String error = "Index при создании не может быть пустым";
        PostOfficeDto postOfficeDto = builder.postalIndex(null).build();
        String json = mapper.writeValueAsString(postOfficeDto);
        when(service.create(getPostOfficeWithNoId)).thenReturn(postOfficeWithIdOne);
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testValidationNameBlank() throws Exception {
        String error = "Имя при создании не может быть пустым";
        PostOfficeDto postOfficeDto = builder.name("").build();
        String json = mapper.writeValueAsString(postOfficeDto);
        when(service.create(getPostOfficeWithNoId)).thenReturn(postOfficeWithIdOne);
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testValidationNameSize() throws Exception {
        String error = "Имя не может превышать 64 символов";
        PostOfficeDto postOfficeDto = builder.name("12345678901234567890123456789012345678901234567890" +
                "12345678901234567890").build();
        String json = mapper.writeValueAsString(postOfficeDto);
        when(service.create(getPostOfficeWithNoId)).thenReturn(postOfficeWithIdOne);
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testValidationAddressBlank() throws Exception {
        String error = "Адрес при создании не может быть пустым";
        PostOfficeDto postOfficeDto = builder.address("").build();
        String json = mapper.writeValueAsString(postOfficeDto);
        when(service.create(getPostOfficeWithNoId)).thenReturn(postOfficeWithIdOne);
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testValidationAddressSize() throws Exception {
        String error = "Адрес не может превышать 256 символов";
        PostOfficeDto postOfficeDto = builder.address(
                "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890").build();
        String json = mapper.writeValueAsString(postOfficeDto);
        when(service.create(getPostOfficeWithNoId)).thenReturn(postOfficeWithIdOne);
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

}