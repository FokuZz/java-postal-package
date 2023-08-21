package ru.skyeng.javapostalpackage.PostOffice.dto.postOffice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skyeng.javapostalpackage.validation.ValidationGroups;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostOfficeDto {

    private Long id;

    @Size(min = 6, max = 6, message = "Нельзя использовать меньше или больше 6 символов")
    @NotBlank(groups = ValidationGroups.Create.class, message = "Index при создании не может быть пустым")
    private String postalIndex;

    @NotBlank(groups = ValidationGroups.Create.class, message = "Имя при создании не может быть пустым")
    @Size(max = 64, message = "Имя не может превышать 64 символов")
    private String name;

    @NotBlank(groups = ValidationGroups.Create.class, message = "Адрес при создании не может быть пустым")
    @Size(max = 256, message = "Адрес не может превышать 256 символов")
    private String address;
}
