package co.touchlab.researchstack.common.secure;
import android.content.Context;

/**
 * Created by kgalligan on 11/24/15.
 */
public class ClearFileAccess implements FileAccess
{
    @Override
    public void writeData(Context context, String path, byte[] data)
    {

    }

    @Override
    public byte[] readData(Context context, String path)
    {
        return new byte[0];
    }
}
