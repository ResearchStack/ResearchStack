package org.researchstack.sampleapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.helpers.LogExt;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.storage.database.AppDatabase;
import org.researchstack.backbone.storage.database.TaskNotification;
import org.researchstack.backbone.storage.file.FileAccessException;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.sampleapp.bridge.BridgeMessageResponse;
import org.researchstack.sampleapp.bridge.IdentifierHolder;
import org.researchstack.sampleapp.network.UserSessionInfo;
import org.researchstack.sampleapp.network.body.ConsentSignatureBody;
import org.researchstack.sampleapp.network.body.EmailBody;
import org.researchstack.sampleapp.network.body.SharingOptionBody;
import org.researchstack.sampleapp.network.body.SignInBody;
import org.researchstack.sampleapp.network.body.SignUpBody;
import org.researchstack.sampleapp.network.body.SurveyAnswer;
import org.researchstack.sampleapp.network.body.SurveyResponse;
import org.researchstack.skin.AppPrefs;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.DataResponse;
import org.researchstack.skin.model.SchedulesAndTasksModel;
import org.researchstack.skin.model.TaskModel;
import org.researchstack.skin.model.User;
import org.researchstack.skin.notification.TaskAlertReceiver;
import org.researchstack.skin.schedule.ScheduleHelper;
import org.researchstack.skin.task.SmartSurveyTask;
import org.researchstack.skin.ui.layout.SignInStepLayout;
import org.researchstack.skin.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public class SampleDataProvider extends DataProvider
{
    public static final String TEMP_CONSENT_JSON_FILE_NAME = "/consent_sig";
    public static final String USER_SESSION_PATH           = "/user_session";
    public static final String USER_PATH                   = "/user";

    //TODO Add build flavors, add var to BuildConfig for STUDY_ID
    public static final  String STUDY_ID = "ohsu-molemapper";
    private static final String CLIENT   = "android";

    //TODO Add build flavors, add var to BuildConfig for BASE_URL
    String BASE_URL = "https://webservices-staging.sagebridge.org/v3/";

    private BridgeService   service;
    private UserSessionInfo userSessionInfo;
    private Gson    gson     = new Gson();
    private boolean signedIn = false;

    // TODO figure out if there's a better way to do this
    // these are used to get task/step guids without rereading the json files and iterating through
    private Map<String, TaskModel> loadedTasks     = new HashMap<>();
    private Map<String, String>    loadedTaskGuids = new HashMap<>();

    public SampleDataProvider()
    {
        buildRetrofitService(null);
    }

    private void buildRetrofitService(UserSessionInfo userSessionInfo)
    {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> LogExt.i(
                SignInStepLayout.class,
                message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        final String sessionToken;
        if(userSessionInfo != null)
        {
            sessionToken = userSessionInfo.getSessionToken();
        }
        else
        {
            sessionToken = "";
        }

        Interceptor headerInterceptor = chain -> {
            Request original = chain.request();

            //TODO Get proper app-name and version name
            Request request = original.newBuilder()
                    .header("User-Agent", " Mole Mapper/1")
                    .header("Content-Type", "application/json")
                    .header("Bridge-Session", sessionToken)
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        };

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(headerInterceptor)
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(client)
                .build();
        service = retrofit.create(BridgeService.class);
    }

    @Override
    public Observable<DataResponse> initialize(Context context)
    {
        return Observable.create(subscriber -> {
            userSessionInfo = loadUserSession(context);
            signedIn = userSessionInfo != null;

            buildRetrofitService(userSessionInfo);
            subscriber.onNext(new DataResponse(true, null));
            checkForTempConsentAndUpload(context);
        });
    }

    private void checkForTempConsentAndUpload(Context context)
    {
        // If we are signed in, not consented on the server, but consented locally, upload consent
        if(isSignedIn(context) && ! userSessionInfo.isConsented() && StorageAccess.getInstance()
                .getFileAccess()
                .dataExists(context, TEMP_CONSENT_JSON_FILE_NAME))
        {
            try
            {
                ConsentSignatureBody consent = loadConsentSignatureBody(context);
                uploadConsent(context, consent);
            }
            catch(Exception e)
            {
                throw new RuntimeException("Error loading consent", e);
            }
        }
    }

    /**
     *
     * @param context
     * @return true if we are consented
     */
    @Override
    public boolean isConsented(Context context)
    {
        return userSessionInfo.isConsented() || StorageAccess.getInstance()
                .getFileAccess()
                .dataExists(context, TEMP_CONSENT_JSON_FILE_NAME);
    }

    @Override
    public Observable<DataResponse> withdrawConsent(Context context, String reason)
    {
        return service.withdrawConsent(new WithdrawalBody(reason)).doOnNext(response -> {
            if(response.isSuccess())
            {
                userSessionInfo.setConsented(false);
                saveUserSession(context, userSessionInfo);
                buildRetrofitService(userSessionInfo);
            }
        }).map(response -> {
            boolean success = response.isSuccess();
            return new DataResponse(success, response.message());
        });
    }

    @Override
    public Observable<DataResponse> signUp(Context context, String email, String username, String password)
    {
        //TODO pass in data groups, remove roles
        SignUpBody body = new SignUpBody(STUDY_ID, email, username, password, null, null);

        // TODO Saving email to user object should exist elsewhere.
        // Save email to user object.
        User user = loadUser(context);
        if (user == null)
        {
            user = new User();
        }
        user.setEmail(email);
        saveUser(context, user);

        return service.signUp(body).map(message -> {
            DataResponse response = new DataResponse();
            response.setSuccess(true);
            return response;
        });
    }

    @Override
    public Observable<DataResponse> signIn(Context context, String username, String password)
    {
        SignInBody body = new SignInBody(STUDY_ID, username, password);

        // response 412 still has a response body, so catch all http errors here
        return service.signIn(body).doOnNext(response -> {

            if(response.code() == 200)
            {
                userSessionInfo = response.body();
            }
            else if(response.code() == 412)
            {
                try
                {
                    String errorBody = response.errorBody().string();
                    userSessionInfo = gson.fromJson(errorBody, UserSessionInfo.class);
                }
                catch(IOException e)
                {
                    throw new RuntimeException("Error deserializing server sign in response");
                }

            }

            if(userSessionInfo != null)
            {
                // TODO if we are direct from signing in, we need to load the user profile object
                // from the server.
                saveUserSession(context, userSessionInfo);
                buildRetrofitService(userSessionInfo);
                checkForTempConsentAndUpload(context);
            }
        }).map(response -> {
            boolean success = response.isSuccess() || response.code() == 412;
            return new DataResponse(success, response.message());
        });
    }

    @Override
    public Observable<DataResponse> signOut(Context context)
    {
        return service.signOut().map(response -> new DataResponse(response.isSuccess(), null));
    }

    @Override
    public Observable<DataResponse> resendEmailVerification(Context context, String email)
    {
        EmailBody body = new EmailBody(STUDY_ID, email);
        return service.resendEmailVerification(body);
    }

    @Override
    public boolean isSignedUp(Context context)
    {
        User user = loadUser(context);
        return user != null && user.getEmail() != null;
    }

    @Override
    public boolean isSignedIn(Context context)
    {
        return signedIn;
    }

    @Override
    public void saveConsent(Context context, String name, Date birthDate, String imageData, String signatureDate, String scope)
    {
        // User is not signed in yet, so we need to save consent info to disk for later upload
        ConsentSignatureBody signature = new ConsentSignatureBody(STUDY_ID,
                name,
                birthDate,
                imageData,
                "image/png",
                scope);
        writeJsonString(context, gson.toJson(signature), TEMP_CONSENT_JSON_FILE_NAME);

        // TODO init here isnt great. Refactor and create saveUser method.
        User user = loadUser(context);
        if (user == null)
        {
            user = new User();
        }
        user.setName(signature.name);
        user.setBirthDate(signature.birthdate);
        saveUser(context, user);
    }

    @Override
    public User getUser(Context context)
    {
        return loadUser(context);
    }

    @Override
    public String getUserSharingScope(Context context)
    {
        return userSessionInfo.getSharingScope();
    }

    @Override
    public void setUserSharingScope(Context context, String scope)
    {
        // Update scope on server
        service.dataSharing(new SharingOptionBody(scope))
                .compose(ObservableUtils.applyDefault())
                .doOnNext(response -> {
                    if(response.isSuccess())
                    {
                        userSessionInfo.setSharingScope(scope);
                        saveUserSession(context, userSessionInfo);
                    }
                })
                .subscribe(response -> LogExt.d(getClass(),
                        "Response: " + response.code() + ", message: " +
                                response.message()), error -> {
                    LogExt.e(getClass(), error.getMessage());
                });
    }

    private ConsentSignatureBody loadConsentSignatureBody(Context context)
    {
        String consentJson = loadJsonString(context, TEMP_CONSENT_JSON_FILE_NAME);
        return gson.fromJson(consentJson, ConsentSignatureBody.class);
    }

    private void uploadConsent(Context context, ConsentSignatureBody consent)
    {
        service.consentSignature(consent)
                .compose(ObservableUtils.applyDefault())
                .subscribe(response -> {
                    // TODO this isn't good, we should be getting an updated user session info from
                    // TODO the server, but there doesn't seem to be a way to do that without
                    // TODO signing in again with the username and password
                    if(response.code() == 201 ||
                            response.code() == 409) // success or already consented
                    {
                        userSessionInfo.setConsented(true);
                        saveUserSession(context, userSessionInfo);

                        LogExt.d(getClass(), "Response: " + response.code() + ", message: " +
                                response.message());

                        StorageAccess.getInstance().getFileAccess().clearData(context,
                                TEMP_CONSENT_JSON_FILE_NAME);
                    }
                    else
                    {
                        throw new RuntimeException(
                                "Error uploading consent, code: " + response.code() + " message: " +
                                        response.message());
                    }
                });
    }

    @Override
    public String getUserEmail(Context context)
    {
        User user = loadUser(context);
        return user == null ? null : user.getEmail();
    }

    private void saveUserSession(Context context, UserSessionInfo userInfo)
    {
        String userSessionJson = gson.toJson(userInfo);
        writeJsonString(context, userSessionJson, USER_SESSION_PATH);
    }

    private User loadUser(Context context)
    {
        try
        {
            String user = loadJsonString(context, USER_PATH);
            return gson.fromJson(user, User.class);
        }
        catch(FileAccessException e)
        {
            return null;
        }
    }

    private void saveUser(Context context, User profile)
    {
        writeJsonString(context, gson.toJson(profile), USER_PATH);
    }

    private void writeJsonString(Context context, String userSessionJson, String userSessionPath)
    {
        StorageAccess.getInstance().getFileAccess()
                .writeData(context, userSessionPath, userSessionJson.getBytes());
    }

    private UserSessionInfo loadUserSession(Context context)
    {
        try
        {
            String userSessionJson = loadJsonString(context, USER_SESSION_PATH);
            return gson.fromJson(userSessionJson, UserSessionInfo.class);
        }
        catch(FileAccessException e)
        {
            return null;
        }
    }

    private String loadJsonString(Context context, String path)
    {
        return new String(StorageAccess.getInstance().getFileAccess().readData(context, path));
    }

    @Override
    public List<SchedulesAndTasksModel.TaskModel> loadTasksAndSchedules(Context context)
    {
        SchedulesAndTasksModel schedulesAndTasksModel = JsonUtils.loadClass(context,
                SchedulesAndTasksModel.class,
                "tasks_and_schedules");

        AppDatabase db = StorageAccess.getInstance().getAppDatabase();

        ArrayList<SchedulesAndTasksModel.TaskModel> tasks = new ArrayList<>();
        for(SchedulesAndTasksModel.ScheduleModel schedule : schedulesAndTasksModel.schedules)
        {
            for(SchedulesAndTasksModel.TaskModel task : schedule.tasks)
            {
                if(task.taskFileName == null)
                {
                    LogExt.e(getClass(), "No filename found for task with id: " + task.taskID);
                    continue;
                }

                // TODO loading the task json here is bad, but the GUID is in the schedule
                // TODO json but the id is in the task json
                TaskModel taskModel = JsonUtils.loadClass(context,
                        TaskModel.class,
                        task.taskFileName);
                TaskResult result = db.loadLatestTaskResult(taskModel.identifier);

                if(result == null)
                {
                    tasks.add(task);
                }
                else if(StringUtils.isNotEmpty(schedule.scheduleString))
                {
                    Date date = ScheduleHelper.nextSchedule(schedule.scheduleString,
                            result.getEndDate());
                    if(date.before(new Date()))
                    {
                        tasks.add(task);
                    }
                }
            }
        }
        return tasks;
    }

    @Override
    public SmartSurveyTask loadTask(Context context, SchedulesAndTasksModel.TaskModel task)
    {
        // TODO 2 types of taskmodels here, confusing
        TaskModel taskModel = JsonUtils.loadClass(context, TaskModel.class, task.taskFileName);
        loadedTasks.put(taskModel.identifier, taskModel);
        loadedTaskGuids.put(taskModel.identifier, task.taskID);
        return new SmartSurveyTask(taskModel);
    }

    @Override
    public void uploadTaskResult(Context context, TaskResult taskResult)
    {
        // Add to database
        StorageAccess.getInstance().getAppDatabase().saveTaskResult(taskResult);

        // Update/Create TaskNotificationService
        if (AppPrefs.getInstance(context).isTaskReminderEnabled())
        {
            Log.i("SampleDataProvider", "uploadTaskResult() _ isTaskReminderEnabled() = true");

            String chronTime = findChronTime(context, taskResult.getIdentifier());

            // If chronTime is null then either the task is not repeating OR its not found within
            // the task_and_schedules.xml
            if (chronTime != null)
            {
                scheduleReminderNotification(context, taskResult.getEndDate(), chronTime);
            }
        }

        // Upload using the world-wide-web
        // TODO use UploadResult queue for this? (upload encrypted)
        TaskModel taskModel = loadedTasks.get(taskResult.getIdentifier());
        List<TaskModel.StepModel> elements = taskModel.elements;
        Map<String, TaskModel.StepModel> stepModels = new HashMap<>(elements.size());

        for(TaskModel.StepModel stepModel : elements)
        {
            stepModels.put(stepModel.identifier, stepModel);
        }

        ArrayList<SurveyAnswer> surveyAnswers = new ArrayList<>();

        for(StepResult stepResult : taskResult.getResults().values())
        {
            boolean declined = stepResult.getResults().size() == 0;
            List<String> answers = new ArrayList<>();
            for(Object answer : stepResult.getResults().values())
            {
                answers.add(answer.toString());
            }
            SurveyAnswer surveyAnswer = new SurveyAnswer(stepModels.get(stepResult.getIdentifier()).guid,
                    declined,
                    CLIENT,
                    stepResult.getEndDate(),
                    answers);
            surveyAnswers.add(surveyAnswer);
        }

        SurveyResponse response = new SurveyResponse(taskResult.getIdentifier(),
                taskResult.getStartDate(),
                taskResult.getEndDate(),
                loadedTaskGuids.get(taskResult.getIdentifier()),
                // TODO createdOn date for survey not in the schedule json, not sure what date to use
                FormatHelper.DEFAULT_FORMAT.format(taskResult.getStartDate()),
                SurveyResponse.Status.FINISHED,
                surveyAnswers);
        // TODO handle errors, add queue?
        service.surveyResponses(response)
                .compose(ObservableUtils.applyDefault())
                .subscribe(httpResponse -> LogExt.d(getClass(),
                        "Successful upload of survey, identifier: " + httpResponse.identifier),
                        error -> {
                            LogExt.e(getClass(), "Error uploading survey");
                            error.printStackTrace();
                        });
    }

    // TODO this stinks, I should be able to query the DB and find the chrono time.
    private String findChronTime(Context context, String identifier)
    {
        SchedulesAndTasksModel schedulesAndTasksModel = JsonUtils.loadClass(context,
                SchedulesAndTasksModel.class,
                "tasks_and_schedules");

        for(SchedulesAndTasksModel.ScheduleModel schedule : schedulesAndTasksModel.schedules)
        {
            for(SchedulesAndTasksModel.TaskModel task : schedule.tasks)
            {
                // TODO loading the task json here is bad, but the GUID is in the schedule
                // TODO json but the id is in the task json
                TaskModel taskModel = JsonUtils.loadClass(context, TaskModel.class,
                        task.taskFileName);

                if (taskModel.identifier.equals(identifier))
                {
                    return schedule.scheduleString;
                }
            }
        }
        return null;
    }

    private void scheduleReminderNotification(Context context, Date endDate, String chronTime)
    {
        Log.i("SampleDataProvider", "scheduleReminderNotification()");

        // Save TaskNotification to DB
        TaskNotification notification = new TaskNotification();
        notification.endDate = endDate;
        notification.chronTime = chronTime;
        NotificationHelper.getInstance(context).saveTaskNotification(notification);

        // Add notification to Alarm Manager
        Intent intent = new Intent(TaskAlertReceiver.ALERT_CREATE);
        intent.putExtra(TaskAlertReceiver.KEY_NOTIFICATION, notification);
        context.sendBroadcast(intent);
    }

    @Override
    public void processInitialTaskResult(Context context, TaskResult taskResult)
    {
        // do something with initial survey here
    }

    public interface BridgeService
    {

        /**
         * @return One of the following responses
         * <ul>
         * <li><b>201</b> returns message that user has been signed up</li>
         * <li><b>473</b> error - returns message that study is full</li>
         * </ul>
         */
        @POST("auth/signUp")
        Observable<BridgeMessageResponse> signUp(@Body SignUpBody body);

        /**
         * @return One of the following responses
         * <ul>
         * <li><b>200</b> returns UserSessionInfo Object</li>
         * <li><b>404</b> error - "Credentials incorrect or missing"</li>
         * <li><b>412</b> error - "User has not consented to research"</li>
         * </ul>
         */
        @POST("auth/signIn")
        Observable<Response<UserSessionInfo>> signIn(@Body SignInBody body);

        @POST("subpopulations/" + STUDY_ID + "/consents/signature")
        Observable<Response<BridgeMessageResponse>> consentSignature(@Body ConsentSignatureBody body);

        /**
         * @return Response code <b>200</b> w/ message explaining instructions on how the user should
         * proceed
         */
        @POST("auth/requestResetPassword")
        Observable<Response> requestResetPassword(@Body EmailBody body);


        @POST("subpopulations/" + STUDY_ID + "/consents/signature/withdraw")
        Observable<Response<BridgeMessageResponse>> withdrawConsent(@Body WithdrawalBody withdrawal);

        /**
         * @return Response code <b>200</b> w/ message explaining instructions on how the user should
         * proceed
         */
        @POST("auth/resendEmailVerification")
        Observable<DataResponse> resendEmailVerification(@Body EmailBody body);

        /**
         * @return Response code 200 w/ message telling user has been signed out
         */
        @POST("auth/signOut")
        Observable<Response> signOut();

        @POST("users/self/dataSharing")
        Observable<Response<BridgeMessageResponse>> dataSharing(@Body SharingOptionBody body);

        @POST("surveyresponses")
        Observable<IdentifierHolder> surveyResponses(@Body SurveyResponse body);
    }

}
