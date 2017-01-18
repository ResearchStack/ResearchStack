package org.researchstack.backbone;
import android.app.Application;
import android.content.Context;

import org.researchstack.backbone.model.ConsentSignature;
import org.researchstack.backbone.model.ConsentSignatureBody;
import org.researchstack.backbone.model.SchedulesAndTasksModel;
import org.researchstack.backbone.model.User;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.storage.file.FileAccess;
import org.researchstack.backbone.task.Task;

import rx.Observable;

/**
 * Class used to as a buffer between the network layer and UI layer. The implementation allows the
 * framework to be backend-agnostic
 */
public abstract class DataProvider
{
    public final static String ERROR_NOT_AUTHENTICATED = "ERROR_NOT_AUTHENTICATED";
    public final static String ERROR_CONSENT_REQUIRED  = "ERROR_CONSENT_REQUIRED";

    private static DataProvider instance;

    /**
     * Default Constructor
     */
    public DataProvider()
    {
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    public static DataProvider getInstance()
    {
        if(instance == null)
        {
            throw new RuntimeException(
                    "DataProvider instance is null. Make sure to init a concrete implementation of ResearchStack in Application.onCreate()");
        }

        return instance;
    }

    /**
     * Initializes the DataProvider singleton. It is best to call this method inside your {@link
     * Application#onCreate()} method.
     *
     * @param instance an implementation of DataProvider
     */
    public static void init(DataProvider instance)
    {
        DataProvider.instance = instance;
    }

    /**
     * Called to initialize the state of the app. The state includes if
     * the user is not signed in/up, not consented, etc..
     *
     * @param context android context
     * @return Observable of the result of the method, with {@link DataResponse#isSuccess()}
     * returning true if signUp was successful
     */
    public abstract Observable<DataResponse> initialize(Context context);

    /**
     * Called to sign the user up to the backend service
     *
     * @param context android context
     * @return Observable of the result of the method, with {@link DataResponse#isSuccess()}
     * returning true if signUp was successful
     */
    public abstract Observable<DataResponse> signUp(Context context, String email, String username, String password);

    /**
     * Called to sign the user in to the backend service
     *
     * @param context android context
     * @return Observable of the result of the method, with {@link DataResponse#isSuccess()}
     * returning true if signIn was successful
     */
    public abstract Observable<DataResponse> signIn(Context context, String username, String password);

    /**
     * Currently not used within the framework
     *
     * @param context android context
     * @return Observable of the result of the method, with {@link DataResponse#isSuccess()}
     * returning true if signOut was successful
     */
    public abstract Observable<DataResponse> signOut(Context context);

    /**
     * Called to alert the backend to resend a vertification
     * email
     *
     * @param context android context
     * @return Observable of the result of the method, with {@link DataResponse#isSuccess()}
     * returning true if signIn was successful
     */
    public abstract Observable<DataResponse> resendEmailVerification(Context context, String email);

    /**
     * Called to verify the user's email address
     * Behind the scenes this calls signIn with securely stored username and password
     *
     * @param context android context
     * @return Observable of the result of the method, with {@link DataResponse#isSuccess()}
     * returning true if verifyEmail was successful
     */
    public abstract Observable<DataResponse> verifyEmail(Context context);

    /**
     * Returns true if user is currently signed up
     *
     * @param context android context
     * @return true if user is signed up
     */
    public abstract boolean isSignedUp(Context context);

    /**
     * Returns true if user is currently signed in
     *
     * @param context android context
     * @return true if user is signed in
     */
    public abstract boolean isSignedIn(Context context);

    /**
     * Returns true if user is currently consented to the study
     *
     * @param context android context
     * @return true if user is currently consented
     *
     * @Deprecated use isConsented() no params instead
     */
    @Deprecated
    public boolean isConsented(Context context) {
        return false;
    }

    /**
     * @return true if user is currently consented to the study
     */
    public abstract boolean isConsented();

    /**
     * Called to alert the backend that the user wants to withdraw from
     * the study
     *
     * @param context android context
     * @return Observable of the result of the method, with {@link DataResponse#isSuccess()}
     * returning true if withdrawl was successful
     */
    public abstract Observable<DataResponse> withdrawConsent(Context context, String reason);

    /**
     * This method is responsible in uploading the user consent information (e.g. Name, Birthdate,
     * Signature) to the backend.  Usually, this is done by looking into the TaskResult
     * object and filling up the ConsentSignature and then calling the method below this
     * with the signature parameter
     *
     * @param context android context
     */
    public abstract void uploadConsent(Context context, TaskResult consentResult);

    /**
     * This method is responsible in uploading the user consent information (e.g. Name, Birthdate,
     * Signature) to the backend.
     *
     * @param context android context
     * @param signature Valid ConsentSignature object
     */
    public abstract Observable<DataResponse> uploadConsent(Context context, ConsentSignatureBody signature);

    /**
     * This method is responsible in saving user consent information (e.g. Name, Birthdate,
     * Signature) locally.
     * <p>
     * Please use {@link FileAccess} class to encrypt user information when saving.
     *
     * @param context android context
     */
    public abstract void saveConsent(Context context, TaskResult consentResult);

    /**
     * This method is responsible in saving user consent information (e.g. Name, Birthdate,
     * Signature) locally for use after the user successfully signs in
     *
     * @param context android context
     * @param consentSignatureBody object which will be saved
     */
    public abstract void saveConsent(Context context, ConsentSignatureBody consentSignatureBody);

    /**
     * Returns the user object that contains any sort of information. This information can be
     * collected in the inital survey and sorted using this object
     *
     * @param context android context
     * @return User object
     */
    public abstract User getUser(Context context);

    /**
     * Saves the user object
     * @param context android context
     * @param user User object to save
     */
    public abstract void setUser(Context context, User user);

    /**
     * Gets the current sharing scope of the user.
     * <p>
     * This scope can be: <ul><li>sponsors_and_partners</li> <li>all_quali5dfied_researchers</li></ul>
     *
     * @param context android context
     * @return the sharing scope of the user
     */
    public abstract String getUserSharingScope(Context context);

    /**
     * Sets the current sharing scope of the user. This method should tell the backend that the
     * sharing scope has changed.
     *
     * @param context android context
     * @param scope   the new sharing scope of the user
     */
    public abstract void setUserSharingScope(Context context, String scope);

    /**
     * Returns the email that the user used to sign in / up
     *
     * @param context android context
     * @return return the email that the user used to sign in/up
     */
    public abstract String getUserEmail(Context context);

    /**
     * Method used to upload a TaskResult to the backend
     *
     * @param context    android context
     * @param taskResult TaskResult object to upload
     */
    public abstract void uploadTaskResult(Context context, TaskResult taskResult);

    /**
     * Loads the SchedulesAndTasksModel object
     *
     * @param context android context
     * @return a SchedulesAndTasksModel object
     */
    public abstract SchedulesAndTasksModel loadTasksAndSchedules(Context context);

    /**
     * Loads a Task object
     *
     * @param context android context
     * @param task the TaskScheduleModel model
     * @return a Task object with defined sub-steps
     */
    public abstract Task loadTask(Context context, SchedulesAndTasksModel.TaskScheduleModel task);

    /**
     * This initial task may include profile items such as height and weight that may need to be
     * processed differently than a normal task result.
     *
     * @param context android context
     * @param taskResult initial TaskResult object to process
     */
    public abstract void processInitialTaskResult(Context context, TaskResult taskResult);

    /**
     * Called during sign-in if the user has forgotten their password. This should make a network
     * call to notify the backend.
     *
     * @param context android context
     * @param email email of the user
     * @return Observable of the result of the method, with {@link DataResponse#isSuccess()}
     * returning true if forgitpassword request was successful
     */
    public abstract Observable<DataResponse> forgotPassword(Context context, String email);

    /**
     * @return the Study ID for the study
     */
    public abstract String getStudyId();
}
