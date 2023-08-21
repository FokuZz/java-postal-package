package ru.skyeng.javapostalpackage.User.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skyeng.javapostalpackage.Exception.model.NotFoundException;
import ru.skyeng.javapostalpackage.User.dto.UserDto;
import ru.skyeng.javapostalpackage.User.dto.UserMapper;
import ru.skyeng.javapostalpackage.User.model.User;
import ru.skyeng.javapostalpackage.User.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<UserDto> getAll() {
        log.info("User Get All");
        return UserMapper.toUserDto(repository.findAll());
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        log.info("User Post user = {}", userDto);
        return UserMapper.toUserDto(
                repository.save(
                        UserMapper.toUser(userDto)
                )
        );
    }

    @Override
    @Transactional
    public void deleteById(long userId) {
        log.info("User Delete by userId = {}", userId);
        repository.deleteById(userId);
    }

    @Override
    public UserDto getUserById(long userId) {
        log.info("User Get by userId = {}", userId);
        return UserMapper.toUserDto(getUser(userId));

    }

    @Override
    @Transactional
    public UserDto updateUser(long userId,
                              UserDto userDto) {
        log.info("User Patch by userId = {}, User = {}", userId, userDto);
        User oldUser = getUser(userId);
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) oldUser.setEmail(userDto.getEmail());
        if (userDto.getPhoneNumber() != null) oldUser.setPhoneNumber(userDto.getPhoneNumber());
        if (userDto.getName() != null && !userDto.getName().isBlank()) oldUser.setName(userDto.getName());
        if (userDto.getLastName() != null && !userDto.getLastName().isBlank())
            oldUser.setLastName(userDto.getLastName());

        UserDto newUser = UserMapper.toUserDto(repository.save(oldUser));  // Выполнения запроса на сохранение нового

        return newUser;
    }


    private User getUser(Long userId) {  // Приватный метод чтобы не загромождать код
        return repository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User по id = %s не найден", userId)));
    }
}
