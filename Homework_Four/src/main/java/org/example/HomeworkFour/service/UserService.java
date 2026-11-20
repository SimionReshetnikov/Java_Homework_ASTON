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
    public Optional<UserResponseDto> updateUser(long id, UserRequestDto updateUser) {
        Optional<User> currentUserOpt = userRepository.findById(id);
        if (currentUserOpt.isEmpty()) {
            return Optional.empty();
        }

        User currentUser = currentUserOpt.get();
        currentUser.setName(updateUser.getName());
        currentUser.setAge(updateUser.getAge());
        currentUser.setEmail(updateUser.getEmail());

        return Optional.of(new UserResponseDto(
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getEmail(),
                currentUser.getAge(),
                currentUser.getCreatedAt()
        ));
    }

    public Optional<UserResponseDto> findById(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return Optional.empty();
        }

        User currentUser = user.get();
        return Optional.of(new UserResponseDto(
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getEmail(),
                currentUser.getAge(),
                currentUser.getCreatedAt()
        ));
    }

    public List<UserResponseDto> findAll() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(u -> new UserResponseDto(u.getId(), u.getName(), u.getEmail(), u.getAge(), u.getCreatedAt()))
                .toList();
    }

    public boolean deleteUser(long id) {
        if (userRepository.findById(id).isEmpty()) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }
}
