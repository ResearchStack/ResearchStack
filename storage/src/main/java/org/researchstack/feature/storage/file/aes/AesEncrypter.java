package org.researchstack.backbone.storage.file.aes;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.security.GeneralSecurityException;

/**
 * Encrypts all bytes passed through with {@link AesCbcWithIntegrity}'s standard AES encryption.
 */
public class AesEncrypter implements Encrypter {
    private AesCbcWithIntegrity.SecretKeys secretKeys;

    public AesEncrypter(AesCbcWithIntegrity.SecretKeys secretKeys) {
        this.secretKeys = secretKeys;
    }

    @Override
    public byte[] encrypt(byte[] data) throws GeneralSecurityException {
        return AesCbcWithIntegrity.encrypt(data, secretKeys).toString().getBytes();
    }

    @Override
    public byte[] decrypt(byte[] data) throws GeneralSecurityException {
        String encrypted = new String(data);
        AesCbcWithIntegrity.CipherTextIvMac cipherText = new AesCbcWithIntegrity.CipherTextIvMac(
                encrypted);
        return AesCbcWithIntegrity.decrypt(cipherText, secretKeys);
    }

    @Override
    public String getDbKey() {
        return secretKeys.toString();
    }
}
