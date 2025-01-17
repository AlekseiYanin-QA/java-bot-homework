package org.homework.model;

/**
 * Класс, представляющий запись о пароле.
 * Содержит название сервиса, имя пользователя и пароль.
 */
public class PasswordEntry {
    private final String name; // Название сервиса (например, "Google")
    private final String username; // Имя пользователя
    private final String password; // Пароль

    /**
     * Конструктор для создания записи о пароле.
     *
     * @param name     Название сервиса.
     * @param username Имя пользователя.
     * @param password Пароль.
     */
    public PasswordEntry(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Service: " + name + "\nUsername: " + username + "\nPassword: " + password;
    }
}