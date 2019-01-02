package org.researchstack.backbone.storage.file.aes;

/**
 * This encrypter doesn't actually encrypt or decrypt anything that passes through it. Allows for
 * implementations like: {@link UnencryptedPinProvider}
 */
public class ClearEncrypter implements Encrypter {
    @Override
    public byte[] encrypt(byte[] data) {
        return data;
    }

    @Override
    public byte[] decrypt(byte[] data) {
        return data;
    }

    @Override
    public String getDbKey() {
        return null;
    }
}
