package ru.skyeng.javapostalpackage.Mail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.skyeng.javapostalpackage.Mail.model.Mail;

import java.util.Optional;

public interface MailRepository extends JpaRepository<Mail, Long> {

    void deleteByIdAndOwnerId(long mailId, long userId);

    Optional<Mail> findByIdAndOwnerId(long mailId, long userId);

    @Query("SELECT m " +
            "FROM Mail m " +
            "JOIN FETCH m.owner ow " +
            "JOIN FETCH m.office of " +
            "WHERE m.id = ?1")
    Optional<Mail> findByIdWithAll(long mailId);
}
