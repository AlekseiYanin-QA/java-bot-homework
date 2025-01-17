package org.homework;

import org.homework.bot.PasswordManagerBot;
import org.homework.di.DIContainer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Инициализирует и регистрирует бота.
 */
public class BotInitializer {
    private final DIContainer container;
    private final ConfigLoader configLoader;

    public BotInitializer() {
        this.container = new DIContainer();
        this.configLoader = new ConfigLoader();
    }

    /**
     * Инициализирует и регистрирует бота.
     */
    public void initializeBot() {
        // Установка свойств из конфигурации
        System.setProperty("bot.token", configLoader.getProperty("bot.token"));
        System.setProperty("bot.username", configLoader.getProperty("bot.username"));

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            PasswordManagerBot bot = container.resolve(PasswordManagerBot.class);
            botsApi.registerBot(bot);
            System.out.println("Бот успешно зарегистрирован!");
        } catch (TelegramApiException e) {
            throw new RuntimeException("Ошибка при регистрации бота", e);
        }
    }
}