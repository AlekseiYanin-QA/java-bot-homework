package org.homework;

import java.io.InputStream;
import java.util.Properties;

/**
 * Загружает конфигурацию из файла application.properties.
 */
public class ConfigLoader {
    private final Properties properties;

    public ConfigLoader() {
        properties = new Properties();
        loadProperties();
    }

    /**
     * Загружает свойства из файла application.properties.
     */
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Файл application.properties не найден!");
            }
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке application.properties", e);
        }
    }

    /**
     * Возвращает значение свойства по ключу.
     *
     * @param key Ключ свойства.
     * @return Значение свойства.
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}