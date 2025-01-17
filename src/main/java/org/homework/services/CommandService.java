package org.homework.services;

import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.homework.logger.Logger;
import org.homework.model.PasswordEntry;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

/**
 * Сервис для обработки команд бота.
 */
@Register
public class CommandService {
    @Resolve
    private PasswordService passwordService; // Сервис для работы с паролями

    @Resolve
    private Logger logger; // Логгер

    /**
     * Обрабатывает команду /start.
     *
     * @param chatId ID чата.
     * @return Сообщение с приветствием и списком команд.
     */
    public SendMessage start(String chatId) {
        return new SendMessage(chatId, "Добро пожаловать в PasswordManagerBot!\n\n" +
                "Используйте команды:\n" +
                "/add <service> <username> <password> - Добавить пароль\n" +
                "/get <service> - Получить пароль\n" +
                "/delete <service> - Удалить пароль\n" +
                "/list - Показать все сервисы\n" +
                "/help - Справка");
    }

    /**
     * Обрабатывает команду /add.
     *
     * @param chatId   ID чата.
     * @param name     Название сервиса.
     * @param username Имя пользователя.
     * @param password Пароль.
     * @return Сообщение о результате операции.
     */
    public SendMessage addPassword(String chatId, String name, String username, String password) {
        passwordService.addPassword(name, username, password);
        logger.info("Пароль добавлен для сервиса: " + name);
        return new SendMessage(chatId, "Пароль для " + name + " успешно добавлен!");
    }

    /**
     * Обрабатывает команду /get.
     *
     * @param chatId ID чата.
     * @param name   Название сервиса.
     * @return Сообщение с паролем или ошибкой.
     */
    public SendMessage getPassword(String chatId, String name) {
        PasswordEntry entry = passwordService.getPassword(name);
        if (entry != null) {
            return new SendMessage(chatId, entry.toString());
        } else {
            return new SendMessage(chatId, "Пароль для " + name + " не найден.");
        }
    }

    /**
     * Обрабатывает команду /delete.
     *
     * @param chatId ID чата.
     * @param name   Название сервиса.
     * @return Сообщение о результате операции.
     */
    public SendMessage deletePassword(String chatId, String name) {
        passwordService.deletePassword(name);
        logger.info("Пароль удален для сервиса: " + name);
        return new SendMessage(chatId, "Пароль для " + name + " удален.");
    }

    /**
     * Обрабатывает команду /list.
     *
     * @param chatId ID чата.
     * @return Сообщение со списком всех сервисов.
     */
    public SendMessage listPasswords(String chatId) {
        List<String> services = passwordService.getAllServices();
        if (services.isEmpty()) {
            return new SendMessage(chatId, "Сохраненных паролей нет.");
        } else {
            StringBuilder response = new StringBuilder("Сохраненные сервисы:\n");
            for (String service : services) {
                response.append("- ").append(service).append("\n");
            }
            return new SendMessage(chatId, response.toString());
        }
    }

    /**
     * Обрабатывает команду /help.
     *
     * @param chatId ID чата.
     * @return Сообщение со списком команд.
     */
    public SendMessage getHelp(String chatId) {
        return new SendMessage(chatId, "Доступные команды:\n" +
                "/add <service> <username> <password> - Добавить пароль\n" +
                "/get <service> - Получить пароль\n" +
                "/delete <service> - Удалить пароль\n" +
                "/list - Показать все сервисы\n" +
                "/help - Справка");
    }
}