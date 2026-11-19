package org.example.HomeworkFour.DTO.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserRequestDto {
    @NotNull(message = "Имя пользователя не может быть пустым.")
    @Size(min = 2, max = 50, message = "Имя пользователя должно содержать от 2 до 50 символов")
    private String firstName;
    

}
