package org.researchstack.backbone.storage.file.aes;
import java.security.GeneralSecurityException;

/**
 * Created by bradleymcdermott on 1/26/16.
 */
public interface Encrypter
{
    byte[] encrypt(byte[] data) throws GeneralSecurityException;

    byte[] decrypt(byte[] data) throws GeneralSecurityException;

    String getDbKey();
}
