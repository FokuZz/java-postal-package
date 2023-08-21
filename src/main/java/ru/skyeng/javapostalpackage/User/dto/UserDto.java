package ru.skyeng.javapostalpackage.User.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skyeng.javapostalpackage.validation.ValidationGroups;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @Email(message = "Email не подходит под формат")
    @NotBlank(groups = ValidationGroups.Create.class, message = "Email при создании не может быть пустым")
    @Size(max = 64, message = "Нельзя превышать 64 символа в поле Email")
    private String email;

    @Size(max = 12, min = 12, message = "Мобильный телефон не может быть больше или меньше 12 символов")
    private String phoneNumber;

    @NotBlank(groups = ValidationGroups.Create.class, message = "Имя при создании не может быть пустым")
    @Size(max = 64, message = "Нельзя превышать 64 символа в поле Name")
    private String name;

    @NotBlank(groups = ValidationGroups.Create.class, message = "Фамилия при создании не может быть пустой")
    @Size(max = 64, message = "Нельзя превышать 64 символа в поле LastName")
    private String lastName;
}
