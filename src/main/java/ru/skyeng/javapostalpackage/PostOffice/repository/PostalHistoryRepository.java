package ru.skyeng.javapostalpackage.PostOffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skyeng.javapostalpackage.PostOffice.model.PostalHistory;

import java.util.List;
import java.util.Optional;

public interface PostalHistoryRepository extends JpaRepository<PostalHistory, Long> {
    List<PostalHistory> findAllByMailIdOrderByCreated(long mailId);

    Optional<PostalHistory> findByMailIdAndInfo(long mailId, String info);

    List<PostalHistory> findByMailId(long mailId);
}
