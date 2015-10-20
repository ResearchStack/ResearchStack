package co.touchlab.touchkit.rk;
public class AppDelegate
{

    public static AppDelegate instance;

    //TODO Thread safe
    public static AppDelegate create()
    {
        if(instance == null)
        {
            instance = new AppDelegate();
        }

        return instance;
    }

    public int getStudyOverviewResourceId()
    {
        return R.raw.study_overview;
    }

}
