package ru.skyeng.javapostalpackage.validation;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Validation {
    public static void checkNumber(String number) {
        if (!number.startsWith("+7")) {
            log.error("{} < ОШИБКА: номер может поддерживать только Российский формат", number);
            throw new ValidationException
                    (String.format("%s < ОШИБКА: номер может поддерживать только Российский формат", number));
        }
    }
}
