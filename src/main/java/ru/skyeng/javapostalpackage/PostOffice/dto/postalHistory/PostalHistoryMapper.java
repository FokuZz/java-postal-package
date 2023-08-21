package ru.skyeng.javapostalpackage.PostOffice.dto.postalHistory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.skyeng.javapostalpackage.Mail.model.Mail;
import ru.skyeng.javapostalpackage.PostOffice.model.PostalHistory;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostalHistoryMapper {
    public static PostalHistory toHistory(PostalHistoryDtoOut postalHistoryDtoOut, Mail mail) {
        PostalHistory postal = PostalHistory.builder()
                .id(postalHistoryDtoOut.getId())
                .mail(mail)
                .info(postalHistoryDtoOut.getInfo())
                .created(postalHistoryDtoOut.getCreated())
                .build();
        return postal;
    }

    public static PostalHistoryDtoOut toHistoryDtoOut(PostalHistory postalHistory) {
        return PostalHistoryDtoOut.builder()
                .id(postalHistory.getId())
                .mailId(postalHistory.getMail().getId())
                .info(postalHistory.getInfo())
                .created(postalHistory.getCreated())
                .build();
    }

    public static List<PostalHistoryDtoOut> toHistoryDtoOut(Iterable<PostalHistory> histories) {
        List<PostalHistoryDtoOut> postalHistoryDtoOuts = new ArrayList<>();
        for (PostalHistory postalHistory : histories) {
            postalHistoryDtoOuts.add(toHistoryDtoOut(postalHistory));
        }
        return postalHistoryDtoOuts;
    }
}
