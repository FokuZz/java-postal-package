package ru.skyeng.javapostalpackage.Mail;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.skyeng.javapostalpackage.Exception.model.NotFoundException;
import ru.skyeng.javapostalpackage.Mail.dto.MailDto;
import ru.skyeng.javapostalpackage.Mail.model.TypeMail;
import ru.skyeng.javapostalpackage.Mail.service.MailServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MailController.class)
class MailControllerTest {

    final static String URL = "/user/mail";

    @MockBean
    MailServiceImpl service;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    MailDto.MailDtoBuilder builder;

    MailDto mailWithIdOne;

    MailDto mailWithNoId;

    @BeforeEach
    void setup() {
        builder = MailDto.builder()
                .typeMail(TypeMail.PACKAGE)
                .officeId(1L)
                .mailIndex("424222")
                .address("address");
        mailWithIdOne = builder.id(1L).build();
        mailWithNoId = builder.build();
    }

    @Test
    void testGetAllEmpty() throws Exception {
        when(service.getAll()).thenReturn(new ArrayList<>());
        mvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }

    @Test
    void testGetAllStandard() throws Exception {
        when(service.getAll()).thenReturn(List.of(mailWithIdOne));
        mvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].address", is("address")));
    }

    @Test
    void testCreateWrongUserId() throws Exception {
        String error = "User по id = 2 не найден";
        when(service.create(mailWithNoId, 2L)).thenThrow(new NotFoundException(error));
        String json = mapper.writeValueAsString(mailWithNoId);
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("X-Package-User-Id", 2))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(error)));
    }

    @Test
    void testCreateEmptyUserId() throws Exception {
        String error = "Отсутствует необходимое значение в header";
        String json = mapper.writeValueAsString(mailWithNoId);
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(error)));
    }

    @Test
    void testCreateNegativeUserId() throws Exception {
        String error = "Id не может быть отрицательным";
        String json = mapper.writeValueAsString(mailWithNoId);
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("X-Package-User-Id", -2))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testCreateEmptyBody() throws Exception {
        String error = "Отсутствует тело запроса";
        mvc.perform(post(URL)
                        .header("X-Package-User-Id", 1))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testCreateStandard() throws Exception {
        when(service.create(mailWithNoId, 1L)).thenReturn(mailWithIdOne);
        String json = mapper.writeValueAsString(mailWithNoId);
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("X-Package-User-Id", 1))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("address", is("address")));
    }

    @Test
    void testDeleteEmptyUserId() throws Exception {
        String error = "Отсутствует необходимое значение в header";
        mvc.perform(delete(URL + "/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(error)));
    }

    @Test
    void testDeleteNegativeUserId() throws Exception {
        String error = "Id не может быть отрицательным";
        mvc.perform(delete(URL + "/1")
                        .header("X-Package-User-Id", -1))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testDeleteNegativeMailId() throws Exception {
        String error = "Id не может быть отрицательным";
        mvc.perform(delete(URL + "/-1")
                        .header("X-Package-User-Id", 1))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testDeleteEmptyMailId() throws Exception {
        mvc.perform(delete(URL + "/")
                        .header("X-Package-User-Id", 1))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteStandard() throws Exception {
        mvc.perform(delete(URL + "/1")
                        .header("X-Package-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());
        verify(service, times(1)).deleteById(1L, 1L);
    }

    @Test
    void testGetByIdEmptyUserId() throws Exception {
        String error = "Отсутствует необходимое значение в header";
        mvc.perform(get(URL + "/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(error)));
    }

    @Test
    void testGetByIdNegativeUserId() throws Exception {
        String error = "Id не может быть отрицательным";
        mvc.perform(get(URL + "/1")
                        .header("X-Package-User-Id", -1))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testGetByIdNegativeMailId() throws Exception {
        String error = "Id не может быть отрицательным";
        mvc.perform(get(URL + "/-1")
                        .header("X-Package-User-Id", 1))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testGetByIdEmptyMailId() throws Exception {
        mvc.perform(get(URL + "/")
                        .header("X-Package-User-Id", 1))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetByIdStandard() throws Exception {
        mvc.perform(get(URL + "/1")
                        .header("X-Package-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());
        verify(service, times(1)).getMailById(1L, 1L);
    }

    @Test
    void GetHistory() throws Exception {
    }

    @Test
    void testGetHistoryEmptyUserId() throws Exception {
        String error = "Отсутствует необходимое значение в header";
        mvc.perform(get(URL + "/1/history"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(error)));
    }

    @Test
    void testGetHistoryNegativeUserId() throws Exception {
        String error = "Id не может быть отрицательным";
        mvc.perform(get(URL + "/1/history")
                        .header("X-Package-User-Id", -1))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testGetHistoryNegativeMailId() throws Exception {
        String error = "Id не может быть отрицательным";
        mvc.perform(get(URL + "/-1/history")
                        .header("X-Package-User-Id", 1))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void testGetHistoryStandard() throws Exception {
        mvc.perform(get(URL + "/1/history")
                        .header("X-Package-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());
        verify(service, times(1)).getHistory(1L, 1L);
    }
}