package ru.skyeng.javapostalpackage.Mail.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skyeng.javapostalpackage.Mail.model.TypeMail;
import ru.skyeng.javapostalpackage.validation.ValidationGroups;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailDto {
    private Long id;

    private TypeMail typeMail;

    @Positive(message = "officeId не может быть отрицательным")
    private Long officeId;

    @Size(min = 6, max = 6, message = "Нельзя использовать меньше или больше 6 символов")
    @NotBlank(groups = ValidationGroups.Create.class, message = "Index при создании не может быть пустым")
    private String mailIndex;

    @Size(max = 256, message = "Адрес не может превышать 256 символов")
    @NotNull(groups = ValidationGroups.Create.class, message = "Index при создании не может быть пустым")
    private String address;
}
