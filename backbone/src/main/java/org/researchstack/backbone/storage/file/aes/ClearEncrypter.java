package org.researchstack.backbone.storage.file.aes;

public class ClearEncrypter implements Encrypter
{
    @Override
    public byte[] encrypt(byte[] data)
    {
        return data;
    }

    @Override
    public byte[] decrypt(byte[] data)
    {
        return data;
    }

    @Override
    public String getDbKey()
    {
        return null;
    }
}
