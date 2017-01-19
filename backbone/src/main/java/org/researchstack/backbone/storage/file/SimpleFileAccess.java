package org.researchstack.backbone.storage.file;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import org.researchstack.backbone.storage.file.aes.Encrypter;
import org.researchstack.backbone.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * This class is a simple implementation of FileAccess that passes all data read/written through the
 * encrypter for encryption/decryption. An encrypter such as {@link UnencryptedProvider} can be used
 * to write unencrypted data instead.
 */
public class SimpleFileAccess implements FileAccess {
    private Encrypter encrypter;

    @Override
    @WorkerThread
    public void writeData(Context context, String path, byte[] data) {
        try {
            File localFile = findLocalFile(context, path);
            FileUtils.makeParent(localFile);
            FileUtils.writeSafe(localFile, encrypter.encrypt(data));
        } catch (GeneralSecurityException e) {
            throw new StorageAccessException(e);
        }
    }

    @Override
    @WorkerThread
    public byte[] readData(Context context, String path) {
        try {
            File localFile = findLocalFile(context, path);
            return encrypter.decrypt(FileUtils.readAll(localFile));
        } catch (IOException | GeneralSecurityException e) {
            throw new StorageAccessException(e);
        }
    }

    @Override
    public void moveData(Context context, String fromPath, String toPath) {
        File from = findLocalFile(context, fromPath);

        File to = findLocalFile(context, toPath);
        FileUtils.makeParent(to);

        try {
            FileUtils.copy(new FileInputStream(from), to);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!from.delete()) {
            throw new RuntimeException("Failed to delete temp file");
        }
    }

    @NonNull
    private File findLocalFile(Context context, String path) {
        checkPath(path);
        return new File(context.getFilesDir() + path);
    }

    @Override
    @WorkerThread
    public boolean dataExists(Context context, String path) {
        return findLocalFile(context, path).exists();
    }

    @Override
    public void clearData(Context context, String path) {
        File localFile = findLocalFile(context, path);
        localFile.delete();
    }

    @Override
    public void setEncrypter(Encrypter encrypter) {
        this.encrypter = encrypter;
    }

    public void checkPath(String path) {
        if (!path.startsWith("/")) {
            throw new StorageAccessException("Path must be absolute (ie start with '/')");
        }
    }
}
