package co.touchlab.researchstack.common.secure;
import android.content.Context;

/**
 * Created by kgalligan on 11/24/15.
 */
public interface FileAccess
{
    void writeData(Context context, String path, byte[] data);
    byte[] readData(Context context, String path);
}
