package ru.skyeng.javapostalpackage.Mail.model;

import java.util.Optional;

public enum TypeMail {
    LETTER, // Письмо
    PACKAGE, // Посылка
    PARCEL, // Бандероль
    POSTCARD;// Открытка

    public static Optional<TypeMail> from(String stringState) {
        for (TypeMail type : values()) {
            if (type.name().equalsIgnoreCase(stringState)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
