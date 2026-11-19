package org.example.HomeworkFour.service;

import jakarta.transaction.Transactional;
import org.example.HomeworkFour.DTO.request.UserRequestDto;
import org.example.HomeworkFour.DTO.response.UserResponseDto;
import org.example.HomeworkFour.model.User;
import org.example.HomeworkFour.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDto saveUser(UserRequestDto userRequestDTO) {
        User saveUser = new User(userRequestDTO.getName(), userRequestDTO.getEmail(), userRequestDTO.getAge());
        User saved = userRepository.save(saveUser);
        return new UserResponseDto(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getAge(),
                saved.getCreatedAt()
        );
    }

    @Transactional
    public Optional<User> updateUser(long id, User updateUser) {
        Optional<User> currentUserOpt = userRepository.findById(id);
        if (currentUserOpt.isEmpty()) {
            return Optional.empty();
        }

        User currentUser = currentUserOpt.get();
        currentUser.setName(updateUser.getName());

        return Optional.of(userRepository.save(currentUser));
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }
}
