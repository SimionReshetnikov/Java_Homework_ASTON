package org.example.HomeworkFour.repository;

import org.example.HomeworkFour.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
