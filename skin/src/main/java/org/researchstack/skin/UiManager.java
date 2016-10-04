package org.researchstack.skin;
import android.app.Application;
import android.content.Context;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.TextUtils;
import org.researchstack.skin.notification.TaskNotificationReceiver;

import java.util.List;

/**
 * UiManager is responsible for providing an a way to define certain aspects of the UI that cannot
 * be provided / genreated by the framework.
 */
public abstract class UiManager
{
    private static UiManager instance;

    /**
     * Initializes the UiManager singleton. It is best to call this method inside your {@link
     * Application#onCreate()} method.
     *
     * @param manager an implementation of UiManager
     */
    public static void init(UiManager manager)
    {
        UiManager.instance = manager;
    }

    /**
     * Returns a singleton static instance of the this class
     *
     * @return A singleton static instance of the this class
     */
    public static UiManager getInstance()
    {
        if(instance == null)
        {
            throw new RuntimeException(
                    "UiManager instance is null. Make sure to init a concrete implementation of ResearchStack in Application.onCreate()");
        }

        return instance;
    }

    /**
     * All ActionItems returned by this method should define a title, icon, and class. These
     * ActionItems are used to populate an ActionBar in the main Activity, so the class should be
     * that of activity (either SettingsActivity & LearnActivity or your own items)
     *
     * @return a list of ActionItems for display in the MainActivity ActionBar
     */
    public abstract List<ActionItem> getMainActionBarItems();

    /**
     * All ActionItems returned by this method should define a title, icon, and class. These items
     * are used to fill a pager in the MainActivity. The framework uses the class objects from this
     * list to create a Fragments for tha pager. It is imperative that the defined classes be of
     * instance  {@link android.support.v4.app.Fragment}.
     *
     * @return a list of ActionItems for display in the MainActivity ActionBar
     */
    public abstract List<ActionItem> getMainTabBarItems();

    /**
     * Includsion Criteria Step is one of the first Steps the user will come in contact with. It is
     * a question / form of questions whos result is used to see if the user elligible or
     * inelligible for the study. That result in calculate and returned within {@link
     * #isInclusionCriteriaValid(StepResult)}
     *
     * @param context android context
     * @return a Step used for Eligibility within the onboarding process
     */
    public abstract Step getInclusionCriteriaStep(Context context);

    /**
     * Method used by the framework to show if the user the result of the {@link
     * #getInclusionCriteriaStep(Context)}.
     *
     * @param result StepResult object that contains the answers of the InclusionCriteria step
     * @return <code>true</code> if the user is elligible for the study
     */
    public abstract boolean isInclusionCriteriaValid(StepResult result);

    /**
     * Used in onboarding to hide the "skip consent" button. Override this if you want to allow the
     * user to get to the main portion of the app without signing up for the study and consenting.
     * All data will still be collected and uploaded when the user successfully signs up for the
     * first time Defaults to false.
     *
     * @return true if consent is skippable
     */
    public boolean isConsentSkippable()
    {
        return false;
    }

    /**
     * Returns <code>true</code> if the password supplied by the user in the sign up step is valid.
     * @param password the password to validate
     * @return <code>true</code> if the password is valid, <code>false</code> otherwise
     */
    public boolean isValidPassword(String password) {
        return ! TextUtils.isEmpty(password);
    }

    /**
     * Returns the BroadCastReceiver class responsible for consuming alarms for triggering
     * Notifications
     *
     * @return BroadCastReceiver class responsible for consuming alarms for triggering Notifications
     */
    public Class<?> getTaskNotificationReceiver()
    {
        return TaskNotificationReceiver.class;
    }
}
