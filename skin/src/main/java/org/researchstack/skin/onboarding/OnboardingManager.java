package org.researchstack.skin.onboarding;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.model.ConsentSection;
import org.researchstack.backbone.model.ConsentSectionAdapter;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.SurveyItemAdapter;
import org.researchstack.backbone.model.survey.factory.ConsentDocumentFactory;
import org.researchstack.backbone.onboarding.OnboardingSection;
import org.researchstack.backbone.onboarding.OnboardingSectionType;
import org.researchstack.backbone.onboarding.OnboardingSectionAdapter;
import org.researchstack.backbone.onboarding.OnboardingTaskType;
import org.researchstack.backbone.onboarding.ResourceNameJsonProvider;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.skin.AppPrefs;
import org.researchstack.skin.DataProvider;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.skin.ResourceManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by TheMDP on 12/22/16.
 */

public class OnboardingManager implements OnboardingSectionAdapter.GsonProvider {

    static final String LOG_TAG = OnboardingManager.class.getCanonicalName();

    /**
     * Class used for easy deserialization of sections list
     */
    class SectionsGsonHolder {
        @SerializedName("sections")
        List<OnboardingSection> sections;
    }
    SectionsGsonHolder mSectionsGsonHolder;
    Gson mGson;

    /*
     * Always initalize using this class
     * @param Context used in reference to the ResourceManager to load JSON resources to construct
     *                the onboarding manager
     * @return OnboardingManager set up using ResourceManager, so make sure it is initialized
     */
    public OnboardingManager(Context context) {
        this(context,
             ResourceManager.getInstance().getOnboardingManager().getName(),
             new ResourceManagerResourceNameJsonProvider(context));
    }

    /*
     * Internal constructor used for unit testing, easiest way to construct is using
     * the method with @param Context
     * @param Context used in reference to the ResourceManager to load JSON resources to construct
     *                the onboarding manager
     * @return OnboardingManager set up using ResourceManager, so make sure it is initialized
     */
    OnboardingManager(Context context,
                      String onboardingResourceName,
                      ResourceNameJsonProvider jsonProvider)
    {
        mGson = buildGson(context, jsonProvider);
        String onboardingJson = jsonProvider.getJsonStringForResourceName(onboardingResourceName);

        mSectionsGsonHolder = mGson.fromJson(onboardingJson, SectionsGsonHolder.class);
        Collections.sort(mSectionsGsonHolder.sections, getSectionComparator());
    }

    public List<OnboardingSection> getSections() {
        return mSectionsGsonHolder.sections;
    }

    /**
     * Override to register custom SurveyItemAdapters,
     * but make sure that the adapter extends from SurveyItemAdapter, and only overrides
     * the method getCustomClass()
     */
    public void registerSurveyItemAdapter(GsonBuilder builder) {
        builder.registerTypeAdapter(SurveyItem.class, new SurveyItemAdapter());
    }

    /**
     * @param jsonProvider used to find recursive json resourceNames and load them while parsing
     * @return a Gson to be used by the OnboardingManager
     */
    private Gson buildGson(Context context, ResourceNameJsonProvider jsonProvider) {
        GsonBuilder onboardingGson = new GsonBuilder();
        registerSurveyItemAdapter(onboardingGson);
        onboardingGson.registerTypeAdapter(OnboardingSection.class, new OnboardingSectionAdapter(jsonProvider));
        onboardingGson.registerTypeAdapter(ConsentSection.class, new ConsentSectionAdapter(context));
        Gson gson = onboardingGson.create();
        return gson;
    }

    // Override this to control OnboardingSection sort order
    public Comparator<OnboardingSection> getSectionComparator() {
        return new SectionComparator();
    }

    @Override
    public Gson getGson() {
        return mGson;
    }

    /**
     * When initializing an onboarding manager with a json file,
     * the sections list will be sorted according to this class. By default,
     * this sort function will ensure that the sections conforming to `OnboardingSectionType` field
     * are ordered as needed to ensure the proper sequence of login, consent and registration according
     * to their ordinal position. All custom sections are left in the order they were included in the
     * original List.
     **/
    static class SectionComparator implements Comparator<OnboardingSection> {
        @Override
        public int compare(OnboardingSection lhs, OnboardingSection rhs) {
            if (lhs == null && rhs == null) {
                return 0;
            } else if (lhs == null) {
                return 1;
            }  else if (rhs == null) {
                return -1;
            }

            OnboardingSectionType lhsType = lhs.getOnboardingSectionType();
            OnboardingSectionType rhsType = rhs.getOnboardingSectionType();

            // If there is a CUSTOM type, just return same, or 0, so it does not shift positions
            if (lhsType == OnboardingSectionType.CUSTOM || rhsType == OnboardingSectionType.CUSTOM) {
                return 0;
            }

            Integer lhsOrdinal = lhsType.ordinal();
            Integer rhsOrdinal = rhsType.ordinal();
            return lhsOrdinal.compareTo(rhsOrdinal);
        }
    }

    /**
     * This is the method that developers should be calling to start off any onboarding process
     * @param taskType the type of onboarding process that should be kicked off
     *                 currently, it is either login, registration, or reconsent
     * @param context  used to transition app to the new onboarding activity
     */
    public void launchOnboarding(OnboardingTaskType taskType, Context context) {
        if (getSections() == null) {
            Log.e(LOG_TAG, "Improper Onboarding json file, sections is null");
            return;
        }

        if (getSections().isEmpty()) {
            Log.e(LOG_TAG, "Improper Onboarding json file, sections are empty");
            return;
        }

        List<Step> steps = new ArrayList<>();
        for (OnboardingSection section : getSections()) {
            List<Step> subSteps = steps(context, section, taskType);
            if (subSteps != null) {
                steps.addAll(subSteps);
            }
        }

        String identifier = taskType.toString();

        NavigableOrderedTask task = new NavigableOrderedTask(identifier, steps);
        Intent taskIntent = ViewTaskActivity.newIntent(context, task);
        context.startActivity(taskIntent);
    }

    /**
     Get the steps that should be included for a given `SBAOnboardingSection` and `SBAOnboardingTaskType`.
     By default, this will return the steps created using the default onboarding survey factory for that section
     or nil if the steps for that section should not be included for the given task.
     @return    Optional array of `ORKStep`
     */
    public List<Step> steps(Context context, OnboardingSection section, OnboardingTaskType taskType) {

        // Check to see that the steps for this section should be included
        if (shouldInclude(context, section.getOnboardingSectionType(), taskType) == false) {
            Log.d(LOG_TAG, "No sections for the task type " + taskType.ordinal());
            return null;
        }

        // Get the default factory
        SurveyFactory factory = section.getDefaultOnboardingSurveyFactory(context);

        // For consent, need to filter out steps that should not be included and group the steps into a substep.
        // This is to facilitate skipping reconsent for a user who is logging in where it is unknown whether
        // or not the user needs to reconsent. Returned this way because the steps in a subclass of ORKOrderedTask
        // are immutable but can be skipped using navigation rules.
        if (factory instanceof ConsentDocumentFactory) {
            ConsentDocumentFactory consentFactory = (ConsentDocumentFactory)factory;
            List<Step> steps = new ArrayList<>();
            switch (taskType) {
                case REGISTRATION:
                    steps.add(consentFactory.registrationConsentStep());
                    break;
                case LOGIN:
                    steps.add(consentFactory.loginConsentStep());
                    break;
                default: // RECONSENT
                    steps.add(consentFactory.reconsentStep());
                    break;
            }
            return steps;
        }

        // For all other cases, return the steps.
        return factory.getSteps();
    }

    /**
     Define the rules for including a given section in a given task type.
     @return    `true` if the `SBAOnboardingSection` should be included for this `SBAOnboardingTaskType`
     */
    boolean shouldInclude(Context context, OnboardingSectionType sectionType, OnboardingTaskType taskType) {
        switch (sectionType) {
            case LOGIN:
                return taskType == OnboardingTaskType.LOGIN;
            case CONSENT:
                // All types *except* email verification include consent
                return (taskType != OnboardingTaskType.REGISTRATION) ||
                        !isRegistered(context);
            case ELIGIBILITY:
            case REGISTRATION:
                // Intro, eligibility and registration are only included in registration
                return (taskType == OnboardingTaskType.REGISTRATION) &&
                        !isRegistered(context);
            case PASSCODE:
                // Passcode is included if it has not already been set
                return !hasPasscode(context);
            case EMAIL_VERIFICATION:
                // Only registration where the login has not been verified includes verification
                return (taskType == OnboardingTaskType.REGISTRATION) &&
                        !isLoginVerified(context);
            case PROFILE:
                return (taskType == OnboardingTaskType.REGISTRATION);
            case PERMISSIONS:
            case COMPLETION:
                // Permissions and completion are included for login and registration
                return taskType == OnboardingTaskType.REGISTRATION ||
                       taskType == OnboardingTaskType.LOGIN;
        }
        return false;
    }

    /**
     * @param context used to access if login is verified
     * @return true when the user has successfully signed in, false otherwise
     *         also, returns false on special case is if the user has signed up but not signed in yet
     */
    boolean isLoginVerified(Context context) {
        return DataProvider.getInstance().isSignedIn(context);
    }

    /**
     * @param context used to access if user is registered
     * @return true when the user has successfully signed up, or signed in, false otherwise
     */
    boolean isRegistered(Context context) {
        return AppPrefs.getInstance(context).isOnboardingComplete();
    }

    /**
     * @param context used to access if user has made a pin code yet
     * @return true if user has made a pin code yet, false otherwise
     */
    boolean hasPasscode(Context context) {
        return StorageAccess.getInstance().hasPinCode(context);
    }

    static class ResourceManagerResourceNameJsonProvider implements ResourceNameJsonProvider {

        Context context;

        ResourceManagerResourceNameJsonProvider(Context context) {
            this.context = context;
        }

        @Override
        public String getJsonStringForResourceName(String resourceName) {
            // Look at all methods of ResourceManager
            Method[] resourceMethods = ResourceManager.class.getDeclaredMethods();
            for (Method method : resourceMethods) {
                if (method.getReturnType().equals(ResourcePathManager.Resource.class)) {
                    try {
                        Object resourceObj = method.invoke(ResourceManager.getInstance(), method);
                        if (resourceObj instanceof ResourcePathManager.Resource) {
                            ResourcePathManager.Resource resource = (ResourcePathManager.Resource)resourceObj;
                            if (resourceName.equals(resource.getName())) {
                                // Resource name match, return its contents as a JSON string
                                return ResourceManager.getResourceAsString(context, resource.getAbsolutePath());
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            // This should never happen unless you have an invalid resource name referenced in json
            Log.e(LOG_TAG, "No resource with name " + resourceName + " found");
            return null;
        }
    }
}


