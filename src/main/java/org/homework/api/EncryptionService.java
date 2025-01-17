package org.homework.api;

public interface EncryptionService {
    String encrypt(String data);
    String decrypt(String encryptedData);
}