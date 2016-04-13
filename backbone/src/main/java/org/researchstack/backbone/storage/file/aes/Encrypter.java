package org.researchstack.backbone.storage.file.aes;
import java.security.GeneralSecurityException;


public interface Encrypter
{
    byte[] encrypt(byte[] data) throws GeneralSecurityException;

    byte[] decrypt(byte[] data) throws GeneralSecurityException;

    String getDbKey();
}
