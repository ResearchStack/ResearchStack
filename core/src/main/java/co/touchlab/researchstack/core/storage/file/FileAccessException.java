package co.touchlab.researchstack.core.storage.file;
/**
 * Created by kgalligan on 11/24/15.
 */
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
