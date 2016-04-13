package org.researchstack.backbone.storage.file;

public class FileAccessException extends RuntimeException
{
    public FileAccessException()
    {
    }

    public FileAccessException(String detailMessage)
    {
        super(detailMessage);
    }

    public FileAccessException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }

    public FileAccessException(Throwable throwable)
    {
        super(throwable);
    }
}
