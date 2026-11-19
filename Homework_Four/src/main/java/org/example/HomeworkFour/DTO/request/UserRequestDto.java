package org.example.HomeworkFour.DTO.request;

import jakarta.validation.constraints.*;

public class UserRequestDto {
    @NotNull(message = "Имя пользователя не может быть пустым.")
    @Size(min = 2, max = 50, message = "Имя пользователя должно содержать от 2 до 50 символов.")
    private String name;

    @Min(value = 0, message = "Возраст пользователя не может быть меньше 0 лет.")
    @Max(value = 120, message = "Возраст пользователя не может быть больше 120 лет.")
    private int age;

    @NotNull(message = "Email пользователя не может быть пустым.")
    @Email(message = "Некорректный формат поля email.")
    private String email;

    public UserRequestDto() {

    }

    public UserRequestDto(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
