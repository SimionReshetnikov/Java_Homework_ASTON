package org.aston.homework_four.repository;

import org.aston.homework_four.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
