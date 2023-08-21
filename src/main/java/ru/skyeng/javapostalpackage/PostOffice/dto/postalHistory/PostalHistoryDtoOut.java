package ru.skyeng.javapostalpackage.PostOffice.dto.postalHistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostalHistoryDtoOut {

    private Long id;

    private Long mailId;

    private String info;

    private LocalDateTime created;
}
