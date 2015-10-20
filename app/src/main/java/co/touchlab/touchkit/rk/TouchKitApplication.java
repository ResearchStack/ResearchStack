package co.touchlab.touchkit.rk;
import android.app.Application;

public class TouchKitApplication extends Application
{

    public static AppDelegate manager;

    //TODO Thread safe
    public static AppDelegate getResouceLocationManager()
    {
        if (manager == null)
        {
            manager = createResourceLocationManager();
        }

        return manager;
    }

    public static AppDelegate createResourceLocationManager()
    {
        return AppDelegate.create();
    }
}
