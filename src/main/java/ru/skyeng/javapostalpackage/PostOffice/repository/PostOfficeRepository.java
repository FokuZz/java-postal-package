package ru.skyeng.javapostalpackage.PostOffice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skyeng.javapostalpackage.PostOffice.model.PostOffice;

public interface PostOfficeRepository extends JpaRepository<PostOffice, Long> {
}
