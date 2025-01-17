package org.homework;

/**
 * Точка входа в приложение.
 */
public class Main {
    public static void main(String[] args) {
        try {
            BotInitializer botInitializer = new BotInitializer();
            botInitializer.initializeBot();
        } catch (Exception e) {
            System.err.println("Ошибка при запуске бота: " + e.getMessage());
            e.printStackTrace();
        }
    }
}