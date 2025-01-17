package org.homework.api;

/**
 * Интерфейс для шифрования и дешифрования данных.
 */
public interface EncryptionService {
    /**
     * Шифрует данные.
     *
     * @param data Данные для шифрования.
     * @return Зашифрованные данные.
     */
    String encrypt(String data);

    /**
     * Дешифрует данные.
     *
     * @param encryptedData Зашифрованные данные.
     * @return Расшифрованные данные.
     */
    String decrypt(String encryptedData);
}