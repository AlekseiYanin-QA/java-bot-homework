package org.homework.bot;

import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.homework.logger.Logger;
import org.homework.services.CommandService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Основной класс бота, обрабатывающий входящие сообщения.
 */
@Register
public class PasswordManagerBot extends TelegramLongPollingBot {
    @Resolve
    private CommandService commandService; // Сервис для обработки команд

    @Resolve
    private Logger logger; // Логгер

    @Override
    public String getBotUsername() {
        return System.getProperty("bot.username");
    }

    @Override
    public String getBotToken() {
        return System.getProperty("bot.token");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            try {
                if (messageText.startsWith("/start")) {
                    execute(commandService.start(chatId));
                } else if (messageText.startsWith("/add")) {
                    String[] parts = messageText.split(" ", 4);
                    if (parts.length == 4) {
                        execute(commandService.addPassword(chatId, parts[1], parts[2], parts[3]));
                    } else {
                        execute(new SendMessage(chatId, "Неверный формат команды. Используйте /add <service> <username> <password>"));
                    }
                } else if (messageText.startsWith("/get")) {
                    String[] parts = messageText.split(" ", 2);
                    if (parts.length == 2) {
                        execute(commandService.getPassword(chatId, parts[1]));
                    } else {
                        execute(new SendMessage(chatId, "Неверный формат команды. Используйте /get <service>"));
                    }
                } else if (messageText.startsWith("/delete")) {
                    String[] parts = messageText.split(" ", 2);
                    if (parts.length == 2) {
                        execute(commandService.deletePassword(chatId, parts[1]));
                    } else {
                        execute(new SendMessage(chatId, "Неверный формат команды. Используйте /delete <service>"));
                    }
                } else if (messageText.equals("/list")) {
                    execute(commandService.listPasswords(chatId));
                } else if (messageText.equals("/help")) {
                    execute(commandService.getHelp(chatId));
                } else {
                    execute(new SendMessage(chatId, "Неизвестная команда. Используйте /help для списка команд."));
                }
            } catch (TelegramApiException e) {
                logger.error("Ошибка при обработке команды: " + e.getMessage());
            }
        }
    }
}