package org.researchstack.skin.onboarding;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.SurveyItemAdapter;
import org.researchstack.backbone.onboarding.OnboardingSection;
import org.researchstack.backbone.onboarding.OnboardingSectionType;
import org.researchstack.backbone.onboarding.OnboardingSectionAdapter;
import org.researchstack.backbone.onboarding.OnboardingTaskType;
import org.researchstack.backbone.onboarding.ResourceNameJsonProvider;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.ConsentDocumentFactory;
import org.researchstack.skin.AppPrefs;
import org.researchstack.skin.DataProvider;
import org.researchstack.backbone.utils.SurveyFactory;
import org.researchstack.skin.ResourceManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by TheMDP on 12/22/16.
 */

public class OnboardingManager implements OnboardingSectionAdapter.GsonProvider {

    static final String LOG_TAG = OnboardingManager.class.getCanonicalName();

    static final String SECTIONS_JSON_NAME = "sections";
    @SerializedName(SECTIONS_JSON_NAME)
    List<OnboardingSection> sections;

    Gson mGson;

    /*
     * Always initalize using this class
     * @param Context used in reference to the ResourceManager to load JSON resources to construct
     *                the onboarding manager
     * @return OnboardingManager set up using ResourceManager, so make sure it is initialized
     */
    public static OnboardingManager createOnboardingManager(Context context) {
        return createOnboardingManager(
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
    static OnboardingManager createOnboardingManager(
            String onboardingResourceName,
            ResourceNameJsonProvider jsonProvider)
    {
        GsonBuilder onboardingGson = new GsonBuilder();
        onboardingGson.registerTypeAdapter(SurveyItem.class, new SurveyItemAdapter());
        onboardingGson.registerTypeAdapter(OnboardingSection.class, new OnboardingSectionAdapter(jsonProvider));
        Gson gson = onboardingGson.create();

        String onboardingJson = jsonProvider.getJsonStringForResourceName(onboardingResourceName);

        OnboardingManager manager = gson.fromJson(onboardingJson, OnboardingManager.class);
        Collections.sort(manager.sections, manager.getSectionComparator());
        return manager;
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
            OnboardingSectionType lhsType = lhs.onboardingType;
            OnboardingSectionType rhsType = rhs.onboardingType;
            Integer lhsOrdinal = lhsType.ordinal();
            Integer rhsOrdinal = rhsType.ordinal();
            return lhsOrdinal.compareTo(rhsOrdinal);
        }
    }

    public void launchOnboarding(OnboardingTaskType taskType, Context context) {
        if (sections == null) {
            Log.e(LOG_TAG, "Improper Onboarding json file, sections is null");
            return;
        }

        if (sections.isEmpty()) {
            Log.e(LOG_TAG, "Improper Onboarding json file, sections are empty");
            return;
        }

        List<Step> steps = new ArrayList<>();
        for (OnboardingSection section : sections) {
            List<Step> subSteps = steps(context, section, taskType);
            if (subSteps != null) {
                steps.addAll(subSteps);
            }
        }

        String identifier = taskType.toString();

        // TODO: complete NavigableOrderedTask
//        NavigableOrderedTask task = new NavigableOrderedTask(identifier, steps);
//        Intent taskIntent = ViewTaskActivity.newIntent(context, task);
//        context.startActivity(taskIntent);
    }

    /**
     Get the steps that should be included for a given `SBAOnboardingSection` and `SBAOnboardingTaskType`.
     By default, this will return the steps created using the default onboarding survey factory for that section
     or nil if the steps for that section should not be included for the given task.
     @return    Optional array of `ORKStep`
     */
    public List<Step> steps(Context context, OnboardingSection section, OnboardingTaskType taskType) {

        // Check to see that the steps for this section should be included
        if (shouldInclude(context, section, taskType) == false) {
            Log.d(LOG_TAG, "No sections for the task type " + taskType.ordinal());
            return null;
        }

        // Get the default factory
        SurveyFactory factory = section.getDefaultOnboardingSurveyFactory();

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
    public boolean shouldInclude(Context context, OnboardingSection section, OnboardingTaskType taskType) {
        switch (section.onboardingType) {
            case LOGIN:
                return taskType == OnboardingTaskType.LOGIN;
            case CONSENT:
                // All types *except* email verification include consent
                return (taskType != OnboardingTaskType.REGISTRATION) ||
                        !AppPrefs.getInstance(context).isOnboardingComplete();
            case ELIGIBILITY:
            case REGISTRATION:
                // Intro, eligibility and registration are only included in registration
                return (taskType == OnboardingTaskType.REGISTRATION) ||
                        !AppPrefs.getInstance(context).isOnboardingComplete();
            case PASSCODE:
                // Passcode is included if it has not already been set
                return !hasPasscode();
            case EMAIL_VERIFICATION:
                // Only registration where the login has not been verified includes verification
                return (taskType == OnboardingTaskType.REGISTRATION) &&
                        !DataProvider.getInstance().isSignedIn(context);
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

    boolean hasPasscode() {
        // TODO: grab from StorageAccess
        //StorageAccess.getInstance().hasPinCode(this);
        return false;
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


