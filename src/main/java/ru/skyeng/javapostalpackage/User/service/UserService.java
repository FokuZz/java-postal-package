package ru.skyeng.javapostalpackage.User.service;

import ru.skyeng.javapostalpackage.User.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto create(UserDto userDto);

    void deleteById(long userId);

    UserDto getUserById(long userId);

    UserDto updateUser(long userId, UserDto userDto);
}
