package org.researchstack.backbone.storage.file.aes;

import java.security.GeneralSecurityException;

/**
 * Implement this class and use in your {@link org.researchstack.backbone.storage.file.EncryptionProvider}
 * to encrypt/decrypt all date before it is written using {@link org.researchstack.backbone.storage.file.FileAccess}.
 */
public interface Encrypter {
    /**
     * Returns the data that has been encrypted using this Encrypter
     *
     * @param data the byte array of data to be encrypted
     * @return the encrypted data
     * @throws GeneralSecurityException
     */
    byte[] encrypt(byte[] data) throws GeneralSecurityException;

    /**
     * Returns the data that has been decrypted using this Encrypter
     *
     * @param data the byte array of data to be decrypted
     * @return the decrypted data
     * @throws GeneralSecurityException
     */
    byte[] decrypt(byte[] data) throws GeneralSecurityException;

    /**
     * Returns the encryption key to use for encrypting a SQLCipher database, if using one.
     *
     * @return a string key to use for encrypting/decrypting the database based on this encrypter
     */
    String getDbKey();
}
