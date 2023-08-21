package ru.skyeng.javapostalpackage.User.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skyeng.javapostalpackage.User.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
