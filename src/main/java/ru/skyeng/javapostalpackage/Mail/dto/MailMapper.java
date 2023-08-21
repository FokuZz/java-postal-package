package ru.skyeng.javapostalpackage.Mail.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.skyeng.javapostalpackage.Mail.model.Mail;
import ru.skyeng.javapostalpackage.PostOffice.model.PostOffice;
import ru.skyeng.javapostalpackage.User.model.User;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MailMapper {
    public static Mail toMail(MailDto mailDto, User owner, PostOffice office) {
        return Mail.builder()
                .id(mailDto.getId())
                .typeMail(mailDto.getTypeMail())
                .owner(owner)
                .office(office)
                .mailIndex(mailDto.getMailIndex())
                .address(mailDto.getAddress())
                .build();
    }

    public static MailDto toMailDto(Mail mail) {
        return MailDto.builder()
                .id(mail.getId())
                .typeMail(mail.getTypeMail())
                .officeId(mail.getOffice().getId())
                .mailIndex(mail.getMailIndex())
                .address(mail.getAddress())
                .build();
    }

    public static List<MailDto> toMailDto(Iterable<Mail> mail) {
        List<MailDto> mailDtos = new ArrayList<>();
        for (Mail oneMail : mail) {
            mailDtos.add(toMailDto(oneMail));
        }
        return mailDtos;
    }
}
