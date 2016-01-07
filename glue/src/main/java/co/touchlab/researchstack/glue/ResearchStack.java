package co.touchlab.researchstack.glue;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import co.touchlab.researchstack.core.StorageManager;
import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.storage.database.AppDatabase;
import co.touchlab.researchstack.core.storage.file.FileAccess;
import co.touchlab.researchstack.glue.model.User;
import co.touchlab.researchstack.glue.ui.fragment.ActivitiesFragment;
import co.touchlab.researchstack.glue.ui.fragment.DashboardFragment;
import co.touchlab.researchstack.glue.ui.fragment.LearnFragment;
import co.touchlab.researchstack.glue.ui.fragment.ProfileFragment;
import co.touchlab.researchstack.glue.ui.fragment.SettingsFragment;

public abstract class ResearchStack
{
    public static final String TEMP_USER_JSON_FILE_NAME = "/temp_user";
    protected static ResearchStack instance;
    protected        Context       context;
    private          User          currentUser;
    private          DataProvider  dataProvider;

    public ResearchStack(Context context)
    {
        this.context = context;
    }

    public static void init(ResearchStack concreteResearchStack)
    {
        instance = concreteResearchStack;

        StorageManager.init(concreteResearchStack.createFileAccessImplementation(),
                concreteResearchStack.createAppDatabaseImplementation());
    }

    public synchronized static ResearchStack getInstance()
    {
        if(instance == null)
        {
            throw new RuntimeException(
                    "Make sure to init a concrete implementation of ResearchStack in Application.onCreate()");
        }

        return instance;
    }

    /**
     * TODO Clean up implementation -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Not too happy about returning a list of objects. Could we / should we use XML impl?
     *
     * @return List of NavigationItems
     */
    public List<NavigationItem> getNavigationItems()
    {
        List<NavigationItem> navItems = new ArrayList<>();

        NavigationItem activities = new NavigationItem().setId(R.id.nav_activities)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.activities)
                .setIcon(R.drawable.ic_nav_activities)
                .setClass(ActivitiesFragment.class);
        navItems.add(activities);

        NavigationItem dashboard = new NavigationItem().setId(R.id.nav_dashboard)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.dashboard)
                .setIcon(R.drawable.ic_nav_dashboard)
                .setClass(DashboardFragment.class);
        navItems.add(dashboard);

        NavigationItem learn = new NavigationItem().setId(R.id.nav_learn)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.learn)
                .setIcon(R.drawable.ic_nav_learn)
                .setClass(LearnFragment.class);
        navItems.add(learn);

        NavigationItem profile = new NavigationItem().setId(R.id.nav_profile)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.profile)
                .setIcon(R.drawable.ic_nav_profile)
                .setClass(ProfileFragment.class);
        navItems.add(profile);

        NavigationItem settings = new NavigationItem().setId(R.id.nav_settings)
                .setGroupId(R.id.nav_group)
                .setTitle(R.string.settings)
                .setIcon(R.drawable.ic_nav_settings)
                .setClass(SettingsFragment.class);
        navItems.add(settings);

        return navItems;
    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Concrete Implementations
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    protected abstract AppDatabase createAppDatabaseImplementation();

    protected abstract FileAccess createFileAccessImplementation();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // File Names
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    public abstract int getStudyOverviewResourceId();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Resource Names
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    public abstract int getLargeLogoDiseaseIcon();

    public abstract int getConsentPDF();

    public abstract int getConsentSections();

    public abstract int getQuizSections();

    public abstract int getLearnSections();

    public abstract int getPrivacyPolicy();

    public abstract int getLicenseSections();

    public String getExternalSDAppFolder()
    {
        return "demo_researchstack";
    }

    public abstract int getAppName();

    public String getHTMLFilePath(String docName)
    {
        return getRawFilePath(docName, "html");
    }

    public String getPDFFilePath(String docName)
    {
        return getRawFilePath(docName, "pdf");
    }


    public String getRawFilePath(String docName, String postfix)
    {
        return "file:///android_res/raw/" + docName + "." + postfix;
    }

    public int getDrawableResourceId(Context context, String name)
    {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    public abstract boolean isSignatureEnabledInConsent();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Other (unorganized)
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

    // TODO use this for deciding what info to collect during signup, hardcoded in layouts for now
    public abstract User.UserInfoType[] getUserInfoTypes();

    public abstract Class getInclusionCriteriaSceneClass();

    //TODO: The whole user thing needs to change.  To discuss.
    public User getCurrentUser()
    {
        loadUser();
        return currentUser;
    }

    public boolean storedUserExists()
    {
        return StorageManager.getFileAccess().dataExists(context, TEMP_USER_JSON_FILE_NAME);
    }

    public void saveUser()
    {
        Gson gson = new Gson();
        String userJsonString = gson.toJson(getCurrentUser());

        LogExt.d(getClass(), "Writing user json:\n" + userJsonString);

        StorageManager.getFileAccess()
                .writeString(context, TEMP_USER_JSON_FILE_NAME, userJsonString);
    }

    public void loadUser()
    {
        Gson gson = new Gson();
        FileAccess fileAccess = StorageManager.getFileAccess();

        if(fileAccess.dataExists(context, TEMP_USER_JSON_FILE_NAME))
        {
            String jsonString = fileAccess.readString(context, TEMP_USER_JSON_FILE_NAME);
            currentUser = gson.fromJson(jsonString, User.class);
        }

        if(currentUser == null)
        {
            LogExt.d(getClass(), "No user file found, creating new user");
            currentUser = new User();
        }
    }

    public void clearUserData(Context context)
    {
        FileAccess fileAccess = StorageManager.getFileAccess();
        if(fileAccess.dataExists(context, TEMP_USER_JSON_FILE_NAME))
        {
            fileAccess.clearData(context, TEMP_USER_JSON_FILE_NAME);
        }
    }

    public void setDataProvider(DataProvider dataProvider)
    {
        this.dataProvider = dataProvider;
    }

    public DataProvider getDataProvider()
    {
        return dataProvider;
    }

}
