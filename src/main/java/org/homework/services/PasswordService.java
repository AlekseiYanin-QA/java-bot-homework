package org.homework.services;

import org.homework.api.EncryptionService;
import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.homework.model.PasswordEntry;

import java.util.*;

/**
 * Сервис для управления паролями.
 * Позволяет добавлять, получать, удалять и перечислять пароли.
 */
@Register
public class PasswordService {
    @Resolve
    private EncryptionService encryptionService; // Сервис шифрования

    private final Map<String, PasswordEntry> passwords = new HashMap<>(); // Хранилище паролей

    /**
     * Добавляет пароль в хранилище.
     *
     * @param name     Название сервиса.
     * @param username Имя пользователя.
     * @param password Пароль.
     */
    public void addPassword(String name, String username, String password) {
        String encryptedPassword = encryptionService.encrypt(password);
        passwords.put(name, new PasswordEntry(name, username, encryptedPassword));
    }

    /**
     * Получает пароль по названию сервиса.
     *
     * @param name Название сервиса.
     * @return Запись о пароле или null, если пароль не найден.
     */
    public PasswordEntry getPassword(String name) {
        PasswordEntry entry = passwords.get(name);
        if (entry != null) {
            String decryptedPassword = encryptionService.decrypt(entry.getPassword());
            return new PasswordEntry(entry.getName(), entry.getUsername(), decryptedPassword);
        }
        return null;
    }

    /**
     * Удаляет пароль по названию сервиса.
     *
     * @param name Название сервиса.
     */
    public void deletePassword(String name) {
        passwords.remove(name);
    }

    /**
     * Возвращает список всех сохранённых сервисов.
     *
     * @return Список названий сервисов.
     */
    public List<String> getAllServices() {
        return new ArrayList<>(passwords.keySet());
    }
}