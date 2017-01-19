package org.researchstack.backbone.storage.file;

public class StorageAccessException extends RuntimeException {
    public StorageAccessException() {
    }

    public StorageAccessException(String detailMessage) {
        super(detailMessage);
    }

    public StorageAccessException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public StorageAccessException(Throwable throwable) {
        super(throwable);
    }
}
