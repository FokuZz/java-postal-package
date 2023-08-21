package ru.skyeng.javapostalpackage.PostOffice.model;

import jakarta.persistence.*;
import lombok.*;
import ru.skyeng.javapostalpackage.Mail.model.Mail;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mail_history")
public class PostalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "mail_id")
    private Mail mail;

    @Column(nullable = false)
    private String info;

    @Column(nullable = false)
    private LocalDateTime created;

    @Override
    public String toString() {
        return "PostalHistory{" +
                "id=" + id +
                ", mail=" + mail +
                ", info='" + info + '\'' +
                ", created=" + created +
                '}';
    }
}
