package ru.skyeng.javapostalpackage.User.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skyeng.javapostalpackage.Exception.model.NotFoundException;
import ru.skyeng.javapostalpackage.User.dto.UserDto;
import ru.skyeng.javapostalpackage.User.dto.UserMapper;
import ru.skyeng.javapostalpackage.User.model.User;
import ru.skyeng.javapostalpackage.User.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl service;
    @Mock
    UserRepository repository;
    User userWithIdOne;

    User userWithNoId;

    @BeforeEach
    public void setup() {
        userWithIdOne = User.builder()
                .id(1L)
                .email("email@gmail.com")
                .phoneNumber("+70005566771")
                .name("name")
                .lastName("lastName")
                .build();

        userWithNoId = User.builder()
                .email("email@gmail.com")
                .phoneNumber("+70005566771")
                .name("name")
                .lastName("lastName")
                .build();
    }

    @Test
    void testGetAllEmpty() {
        when(repository.findAll()).thenReturn(new ArrayList<>());
        List<UserDto> users = service.getAll();
        assertEquals(0, users.size());
    }

    @Test
    void testGetAllStandard() {
        when(repository.findAll()).thenReturn(List.of(userWithIdOne));
        List<UserDto> users = service.getAll();
        assertEquals(1, users.size());

    }

    @Test
    void testCreateStandard() {
        when(repository.save(userWithNoId)).thenReturn(userWithIdOne);
        UserDto userDto = service.create(UserMapper.toUserDto(userWithNoId));
        assertEquals(1, userDto.getId());
        assertEquals("name", userDto.getName());
    }

    @Test
    void testDeleteByIdStandard() {
        service.deleteById(1);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testGetUserByIdStandard() {
        when(repository.findById(1L)).thenReturn(Optional.of(userWithIdOne));
        UserDto userDto = service.getUserById(1);
        assertEquals(1, userDto.getId());
        assertEquals("name", userDto.getName());
    }

    @Test
    void testGetUserByIdFailNoFound() {
        when(repository.findById(1L)).thenThrow(new NotFoundException("User по id = 1 не найден"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            service.getUserById(1);
        });
        assertEquals("User по id = 1 не найден", exception.getMessage());
    }

    @Test
    void testUpdateUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(userWithIdOne));
        UserDto userDto = UserMapper.toUserDto(userWithIdOne);
        userDto.setName("updated!");
        when(repository.save(UserMapper.toUser(userDto))).thenReturn(UserMapper.toUser(userDto));

        UserDto userAnswer = service.updateUser(1, userDto);
        assertEquals(1, userAnswer.getId());
        assertEquals("updated!", userAnswer.getName());
    }
}