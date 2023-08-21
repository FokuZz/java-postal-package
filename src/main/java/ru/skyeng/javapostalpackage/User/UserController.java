package ru.skyeng.javapostalpackage.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skyeng.javapostalpackage.User.dto.UserDto;
import ru.skyeng.javapostalpackage.User.service.UserService;
import ru.skyeng.javapostalpackage.validation.Validation;
import ru.skyeng.javapostalpackage.validation.ValidationGroups;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDto> getAll() {
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(ValidationGroups.Create.class)
    public UserDto post(@RequestBody @Valid UserDto userDto) {
        if (userDto.getPhoneNumber() != null) {
            Validation.checkNumber(userDto.getPhoneNumber());
        }
        return service.create(userDto);
    }


    @DeleteMapping("/{userId}")
    public void delete(@PathVariable(name = "userId")
                       @Positive(message = "Id не может быть отрицательным") Long userId) {
        service.deleteById(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable(name = "userId")
                           @Positive(message = "Id не может быть отрицательным") Long userId) {
        return service.getUserById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto patch(@PathVariable(name = "userId")
                         @Positive(message = "Id не может быть отрицательным") Long userId,
                         @RequestBody @Valid UserDto userDto) {
        if (userDto.getPhoneNumber() != null) {
            Validation.checkNumber(userDto.getPhoneNumber());
        }
        return service.updateUser(userId, userDto);
    }
}
