package ru.skyeng.javapostalpackage.Mail.model;

import jakarta.persistence.*;
import lombok.*;
import ru.skyeng.javapostalpackage.PostOffice.model.PostOffice;
import ru.skyeng.javapostalpackage.User.model.User;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mail")
public class Mail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_mail")
    @Enumerated(EnumType.STRING)
    private TypeMail typeMail;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "office_id")
    private PostOffice office;

    @Column(name = "mail_index", nullable = false)
    private String mailIndex;

    @Column(nullable = false)
    private String address;
}

