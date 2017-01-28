package org.researchstack.backbone.onboarding;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.model.ConsentSection;
import org.researchstack.backbone.model.ConsentSectionAdapter;
import org.researchstack.backbone.model.survey.CustomSurveyItem;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.SurveyItemAdapter;
import org.researchstack.backbone.model.survey.factory.ConsentDocumentFactory;
import org.researchstack.backbone.step.CustomStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.SubtaskStep;
import org.researchstack.backbone.task.NavigableOrderedTask;
import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.ResourceManager;
import org.researchstack.backbone.ui.OnboardingTaskActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by TheMDP on 12/22/16.
 *
 * OnboardingManager is a more sophisticated version of TaskProvider
 * It deserializes JSON from the onboarding JSON file, and converts it ultimately into Steps
 * Most interactions with the OnboardingManager come from launchOnboarding
 * which can launch onboarding of a certain type, and will provide Steps in the correct
 * order in which the user will go through without much work from the app developer
 */

public class OnboardingManager implements OnboardingSectionAdapter.GsonProvider, SurveyFactory.CustomStepCreator {

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
    ResourceNameToStringConverter converter;

    /*
     * Always initalize using this class
     * @param Context used in reference to the ResourceManager to load JSON resources to construct
     *                the onboarding manager
     * @return OnboardingManager set up using ResourceManager, so make sure it is initialized
     */
    public OnboardingManager(Context context) {
        this(context,
             ResourceManager.getInstance().getOnboardingManager().getName(),
             new ResourceManagerNameJsonProvider(context));
    }

    /*
     * Constructor used for unit testing, also can be used to provide a custom ResourceManager
     * for the original onboardingResourceName, and also
     * for any nested "resourceName" attributes that are found during the deserialization
     *
     * @param Context used in reference to the ResourceManager and for the SurveyFactory
     * @param onboardingResourceName root onboarding json file
     * @param converter a custom json provider for providing json for resources
     *        developer is guided to the correct one by having to override the default
     * @return OnboardingManager set up using ResourceManager, so make sure it is initialized
     */
    public OnboardingManager(Context context,
                             String onboardingResourceName,
                             ResourceNameToStringConverter converter)
    {
        this.converter = converter;
        mGson = buildGson(context, converter);
        String onboardingJson = converter.getJsonStringForResourceName(onboardingResourceName);

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
     * @param builder the gson build to add the survey item adapter to
     */
    public void registerSurveyItemAdapter(GsonBuilder builder) {
        builder.registerTypeAdapter(SurveyItem.class, new SurveyItemAdapter());
    }

    /**
     * @param convertor used to find recursive json and html resourceNames and load them while parsing
     * @return a Gson to be used by the OnboardingManager
     */
    private Gson buildGson(Context context, ResourceNameToStringConverter convertor) {
        GsonBuilder onboardingGson = new GsonBuilder();
        registerSurveyItemAdapter(onboardingGson);
        onboardingGson.registerTypeAdapter(OnboardingSection.class, new OnboardingSectionAdapter(convertor));
        onboardingGson.registerTypeAdapter(ConsentSection.class, new ConsentSectionAdapter(context, convertor));
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

            // Passcode is a special case right now, since

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
                setStepTitles(section, subSteps);
                steps.addAll(subSteps);
            }
        }

        String identifier = taskType.toString();

        NavigableOrderedTask task = createOnboardingTask(identifier, steps);
        Intent onboardingTaskIntent = createOnboardingTaskActivityIntent(context, task);
        context.startActivity(onboardingTaskIntent);
    }

    /**
     * Can be overridden by a sub-class to inject their own Task
     *
     * @param identifier Task Identifier
     * @param stepList to make NavigableOrderedTask
     * @return NavigableOrderedTask with step list
     */
    public OnboardingManagerTask createOnboardingTask(String identifier, List<Step> stepList) {
        return new OnboardingManagerTask(identifier, stepList);
    }

    /**
     * Intent must contain class of type OnboardingTaskActivity, otherwise it may not operate correctly
     *
     * @param context used to launch activity
     * @param task to be sent to OnboardingTaskActivity or sub-class Activity
     *
     * @return an intent holding a reference to OnboardingTaskActivity or a sub-class of it
     */
    public Intent createOnboardingTaskActivityIntent(Context context, NavigableOrderedTask task) {
        return OnboardingTaskActivity.newIntent(context, task);
    }

    /**
     * Get the steps that should be included for a given `SBAOnboardingSection` and `SBAOnboardingTaskType`.
     * By default, this will return the steps created using the default onboarding survey factory for that section
     * or nil if the steps for that section should not be included for the given task.
     * @param context used to determine if user is signed, registered, or consented
     * @param section get the steps for this section
     * @param taskType the type of task to control section steps
     * @return step list for this onboarding section and the task type
     */
    public List<Step> steps(Context context, OnboardingSection section, OnboardingTaskType taskType) {

        // Check to see that the steps for this section should be included
        if (shouldInclude(context, section.getOnboardingSectionType(), taskType) == false) {
            Log.d(LOG_TAG, "No sections for the task type " + taskType.ordinal());
            return null;
        }

        // Get the default factory
        SurveyFactory factory = section.getDefaultOnboardingSurveyFactory(context, converter, this);

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
        return DataProvider.getInstance().isSignedUp(context);
    }

    /**
     * @param context used to access if user has made a pin code yet
     * @return true if user has made a pin code yet, false otherwise
     */
    boolean hasPasscode(Context context) {
        return StorageAccess.getInstance().hasPinCode(context);
    }

    public static class ResourceManagerNameJsonProvider implements ResourceNameToStringConverter {

        Context context;

        ResourceManagerNameJsonProvider(Context context) {
            this.context = context;
        }

        @Override
        public String getJsonStringForResourceName(String resourceName) {
            // Look at all methods of ResourceManager
            Method[] resourceMethods = ResourceManager.class.getDeclaredMethods();
            for (Method method : resourceMethods) {
                if (method.getReturnType().equals(ResourcePathManager.Resource.class)) {
                    String errorMessage = null;
                    try {
                        Object resourceObj = method.invoke(ResourceManager.getInstance());
                        if (resourceObj instanceof ResourcePathManager.Resource) {
                            ResourcePathManager.Resource resource = (ResourcePathManager.Resource)resourceObj;
                            if (resourceName.equals(resource.getName())) {
                                // Resource name match, return its contents as a JSON string
                                return ResourceManager.getResourceAsString(context, resource.getRelativePath());
                            }
                        }
                    } catch (IllegalAccessException e) {
                        errorMessage = e.getMessage();
                    } catch (InvocationTargetException e) {
                        errorMessage = e.getMessage();
                    }
                    if (errorMessage != null) {
                        throw new IllegalStateException("You must define a method in ResourceManager that returns a Resource for the resourceName " + resourceName);
                    }
                }
            }
            // This should never happen unless you have an invalid resource name referenced in json
            Log.e(LOG_TAG, "No resource with name " + resourceName + " found");
            return null;
        }

        @Override
        public String getHtmlStringForResourceName(String resourceName) {
            String htmlFilePath = ResourceManager.getInstance()
                    .generatePath(ResourceManager.Resource.TYPE_HTML, resourceName);
            return ResourceManager.getResourceAsString(context, htmlFilePath);
        }
    }

    @Override
    public CustomStep createCustomStep(CustomSurveyItem item, SurveyFactory factory) {
        // Go with default implementation of this SurveyFactory
        return factory.createCustomStep(item);
    }

    /**
     * @param section that these steps belong to
     * @param stepList the list of steps created from this section and the JSON
     */
    public void setStepTitles(OnboardingSection section, List<Step> stepList) {
        for (Step step : stepList) {
            if (step.getStepTitle() == 0) {
                switch (section.getOnboardingSectionType()) {
                    case LOGIN:
                        step.setStepTitle(R.string.rsb_login_step_title);
                        break;
                    case PASSCODE:
                        step.setStepTitle(R.string.rsb_passcode);
                        break;
                    case REGISTRATION:
                        step.setStepTitle(R.string.rsb_registration_step_title);
                        break;
                    case PERMISSIONS:
                        step.setStepTitle(R.string.rsb_permissions_step_title);
                        break;
                    case PROFILE:
                        step.setStepTitle(R.string.rsb_profile_step_title);
                        break;
                    case CONSENT:
                        step.setStepTitle(R.string.rsb_consent_step_title);
                        break;
                    case EMAIL_VERIFICATION:
                        step.setStepTitle(R.string.rsb_email_verification_step_title);
                        break;
                    case ELIGIBILITY:
                        step.setStepTitle(R.string.rsb_eligibility_step_title);
                        break;
                    case COMPLETION:
                        step.setStepTitle(R.string.rsb_completion_step_title);
                        break;
                }
            }
        }
    }
}


