package ru.skyeng.javapostalpackage.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.skyeng.javapostalpackage.User.dto.UserDto;
import ru.skyeng.javapostalpackage.User.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserControllerTest {

    private final static String URL = "/users";
    @MockBean
    UserService service;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    UserDto.UserDtoBuilder builder;

    @BeforeEach
    void setup() { //
        builder = UserDto.builder()
                .email("email@gmail.com")
                .phoneNumber("+70005566771")
                .name("name")
                .lastName("lastName");
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
        UserDto userDto = builder.id(1L).build();
        when(service.getAll()).thenReturn(List.of(userDto));
        mvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("name")));

    }

    @Test
    void testPostStandard() throws Exception {
        UserDto userWithoutId = builder.build();
        UserDto userFromDao = builder.id(1L).build();
        String json = mapper.writeValueAsString(userWithoutId);

        when(service.create(userWithoutId)).thenReturn(userFromDao);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("name")));

    }

    @Test
    void testPostEmptyBody() throws Exception {
        String errorMessage = "Отсутствует тело запроса";
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(errorMessage)));
    }

    @Test
    void testPostFailValidation() throws Exception {
        UserDto userWithoutId = builder.email("emailWithoutEMailDotCom").build();
        String json = mapper.writeValueAsString(userWithoutId);

        String errorMessage = "Email не подходит под формат";
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(errorMessage)));
    }

    @Test
    void testDeleteEmptyUserId() throws Exception {
        mvc.perform(delete(URL + "/"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteWrongUserId() throws Exception {
        doThrow(new EmptyResultDataAccessException(1)).when(service).deleteById(4L);
        String errorMessage = "В базе данных нет записи с таким идентификатором";
        mvc.perform(delete(URL + "/4"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(errorMessage)));

        verify(service, times(1)).deleteById(4);
    }

    @Test
    void testDeleteNegativeUserId() throws Exception {
        String errorMessage = "Id не может быть отрицательным";
        mvc.perform(delete(URL + "/-4"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(errorMessage)));
    }

    @Test
    void testDeleteStandard() throws Exception {
        mvc.perform(delete(URL + "/2"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(service, times(1)).deleteById(2);

    }

    @Test
    void testGetByIdEmptyUserId() throws Exception {
        mvc.perform(get(URL + "/"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetByIdWrongUserId() throws Exception {
        doThrow(new EmptyResultDataAccessException(1)).when(service).getUserById(4L);
        String errorMessage = "В базе данных нет записи с таким идентификатором";
        mvc.perform(get(URL + "/4"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(errorMessage)));

        verify(service, times(1)).getUserById(4);
    }

    @Test
    void testGetByIdNegativeUserId() throws Exception {
        String errorMessage = "Id не может быть отрицательным";
        mvc.perform(get(URL + "/-4"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(errorMessage)));
    }

    @Test
    void testGetByIdStandard() throws Exception {
        mvc.perform(get(URL + "/2"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(service, times(1)).getUserById(2);

    }

    @Test
    void Patch() throws Exception {
    }

    @Test
    void testPatchEmptyUserId() throws Exception {
        UserDto user = builder.id(1L).build();
        String json = mapper.writeValueAsString(user);

        mvc.perform(patch(URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testPatchWrongUserId() throws Exception {
        UserDto userPatchBody = builder.id(1L).name("artemus").build();
        String json = mapper.writeValueAsString(userPatchBody);

        doThrow(new EmptyResultDataAccessException(1)).when(service).updateUser(4L, userPatchBody);
        String errorMessage = "В базе данных нет записи с таким идентификатором";
        mvc.perform(patch(URL + "/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(errorMessage)));

        verify(service, times(1)).updateUser(4, userPatchBody);
    }

    @Test
    void testPatchNegativeUserId() throws Exception {
        UserDto userPatchBody = builder.id(1L).name("artemus").build();
        String json = mapper.writeValueAsString(userPatchBody);
        String errorMessage = "Id не может быть отрицательным";
        mvc.perform(patch(URL + "/-4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(errorMessage)));
    }

    @Test
    void testPatchStandard() throws Exception {
        UserDto userPatchBody = builder.id(1L).name("artemus").build();
        String json = mapper.writeValueAsString(userPatchBody);
        when(service.updateUser(1L, userPatchBody)).thenReturn(userPatchBody);

        mvc.perform(patch(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk());
        verify(service, times(1)).updateUser(1, userPatchBody);

    }

    //Тесты валидации DTO объекта, взял первый попавшийся метод с Valid группой create

    @Test
    void testValidationDtoFailEmailFormat() throws Exception {
        UserDto userWithoutId = builder.email("emailWithout").build();

        String json = mapper.writeValueAsString(userWithoutId);
        String errorMessage = "Email не подходит под формат";
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(errorMessage)));

    }

    @Test
    void testValidationDtoFailEmailBlank() throws Exception {
        UserDto userWithoutId = builder.email("").build();

        String json = mapper.writeValueAsString(userWithoutId);
        String errorMessage = "Email при создании не может быть пустым";
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(errorMessage)));

    }

    @Test
    void testValidationDtoFailEmailSize() throws Exception {
        UserDto userWithoutId = builder.email(
                "123456789012345678901234567890123456789012345678901234567890" +
                        "12345678901234567890@gmail.com").build();

        String json = mapper.writeValueAsString(userWithoutId);
        String errorMessage = "Нельзя превышать 64 символа в поле Email";
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(errorMessage)));

    }

    @Test
    void testValidationDtoFailPhoneNumberFormat() throws Exception {
        UserDto userWithoutId = builder.phoneNumber("229515135655").build();

        String json = mapper.writeValueAsString(userWithoutId);
        String errorMessage = "ОШИБКА: номер может поддерживать только Российский формат";
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(errorMessage)));

    }

    @Test
    void testValidationDtoFailPhoneNumberSizePlus() throws Exception {
        UserDto userWithoutId = builder.phoneNumber("+795151356552").build();

        String json = mapper.writeValueAsString(userWithoutId);
        String errorMessage = "Мобильный телефон не может быть больше или меньше 12 символов";
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(errorMessage)));

    }

    @Test
    void testValidationDtoFailPhoneNumberSizeMinus() throws Exception {
        UserDto userWithoutId = builder.phoneNumber("+795155").build();

        String json = mapper.writeValueAsString(userWithoutId);
        String errorMessage = "Мобильный телефон не может быть больше или меньше 12 символов";
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(errorMessage)));

    }

    @Test
    void testValidationDtoFailNameBlank() throws Exception {
        UserDto userWithoutId = builder.name("").build();

        String json = mapper.writeValueAsString(userWithoutId);
        String errorMessage = "Имя при создании не может быть пустым";
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(errorMessage)));

    }

    @Test
    void testValidationDtoFailNameSize() throws Exception {
        UserDto userWithoutId = builder.name(
                "1234567890123456789012345678901234567890" +
                        "1234567890123456789012345678901234567890").build();

        String json = mapper.writeValueAsString(userWithoutId);
        String errorMessage = "Нельзя превышать 64 символа в поле Name";
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(errorMessage)));

    }

    @Test
    void testValidationDtoFailLastNameBlank() throws Exception {
        UserDto userWithoutId = builder.lastName("").build();

        String json = mapper.writeValueAsString(userWithoutId);
        String errorMessage = "Фамилия при создании не может быть пустой";
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(errorMessage)));

    }

    @Test
    void testValidationDtoFailLastNameSize() throws Exception {
        UserDto userWithoutId = builder.lastName(
                "1234567890123456789012345678901234567890" +
                        "1234567890123456789012345678901234567890").build();

        String json = mapper.writeValueAsString(userWithoutId);
        String errorMessage = "Нельзя превышать 64 символа в поле LastName";
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(errorMessage)));

    }
}