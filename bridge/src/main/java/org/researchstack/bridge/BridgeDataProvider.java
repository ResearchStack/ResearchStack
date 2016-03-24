package org.researchstack.bridge;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.helpers.LogExt;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.storage.NotificationHelper;
import org.researchstack.backbone.storage.database.AppDatabase;
import org.researchstack.backbone.storage.database.TaskNotification;
import org.researchstack.backbone.storage.file.FileAccessException;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.bridge.body.ConsentSignatureBody;
import org.researchstack.bridge.body.EmailBody;
import org.researchstack.bridge.body.SharingOptionBody;
import org.researchstack.bridge.body.SignInBody;
import org.researchstack.bridge.body.SignUpBody;
import org.researchstack.bridge.body.SurveyAnswer;
import org.researchstack.bridge.body.WithdrawalBody;
import org.researchstack.skin.AppPrefs;
import org.researchstack.skin.DataProvider;
import org.researchstack.skin.DataResponse;
import org.researchstack.skin.model.SchedulesAndTasksModel;
import org.researchstack.skin.model.TaskModel;
import org.researchstack.skin.model.User;
import org.researchstack.skin.notification.TaskAlertReceiver;
import org.researchstack.skin.schedule.ScheduleHelper;
import org.researchstack.skin.task.SmartSurveyTask;
import org.researchstack.skin.utils.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;
import rx.schedulers.Schedulers;

/*
* This is a very simple implementation that hits only part of the Sage Bridge REST API
* a complete port of the Sage Bridge Java SDK for android: https://github.com/Sage-Bionetworks/BridgeJavaSDK
 */
public abstract class BridgeDataProvider extends DataProvider
{
    public static final String TEMP_CONSENT_JSON_FILE_NAME = "/consent_sig";
    public static final String USER_SESSION_PATH           = "/user_session";
    public static final String USER_PATH                   = "/user";

    private   BridgeService   service;
    protected UserSessionInfo userSessionInfo;
    protected Gson    gson     = new Gson();
    protected boolean signedIn = false;

    // these are used to get task/step guids without rereading the json files and iterating through
    private Map<String, String> loadedTaskGuids = new HashMap<>();
    private Map<String, String> loadedTaskDates = new HashMap<>();
    private Map<String, String> loadedTaskCrons = new HashMap<>();

    protected abstract int getPublicKeyResId();

    protected abstract int getTasksAndSchedulesResId();

    protected abstract String getBaseUrl();

    protected abstract String getStudyId();

    protected abstract String getUserAgent();

    public BridgeDataProvider()
    {
        buildRetrofitService(null);
    }

    private void buildRetrofitService(UserSessionInfo userSessionInfo)
    {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> LogExt.i(getClass(),
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

            Request request = original.newBuilder().header("User-Agent", getUserAgent())
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
                .baseUrl(getBaseUrl())
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
            uploadPendingFiles(context);
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
        return service.withdrawConsent(getStudyId(), new WithdrawalBody(reason))
                .doOnNext(response -> {
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
        // we should pass in data groups, remove roles
        SignUpBody body = new SignUpBody(getStudyId(), email, username, password, null, null);

        // saving email to user object should exist elsewhere.
        // Save email to user object.
        User user = loadUser(context);
        if(user == null)
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
        SignInBody body = new SignInBody(getStudyId(), username, password);

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
                // if we are direct from signing in, we need to load the user profile object
                // from the server. that wouldn't work right now
                signedIn = true;
                saveUserSession(context, userSessionInfo);
                buildRetrofitService(userSessionInfo);
                checkForTempConsentAndUpload(context);
                uploadPendingFiles(context);
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
        EmailBody body = new EmailBody(getStudyId(), email);
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
        ConsentSignatureBody signature = new ConsentSignatureBody(getStudyId(),
                name,
                birthDate,
                imageData,
                "image/png",
                scope);
        writeJsonString(context, gson.toJson(signature), TEMP_CONSENT_JSON_FILE_NAME);

        User user = loadUser(context);
        if(user == null)
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
        service.consentSignature(getStudyId(), consent)
                .compose(ObservableUtils.applyDefault())
                .subscribe(response -> {
                    if(response.code() == 201 ||
                            response.code() == 409) // success or already consented
                    {
                        userSessionInfo.setConsented(true);
                        saveUserSession(context, userSessionInfo);

                        LogExt.d(getClass(), "Response: " + response.code() + ", message: " +
                                response.message());

                        StorageAccess.getInstance()
                                .getFileAccess()
                                .clearData(context, TEMP_CONSENT_JSON_FILE_NAME);
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
        StorageAccess.getInstance()
                .getFileAccess()
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
    public SchedulesAndTasksModel loadTasksAndSchedules(Context context)
    {
        SchedulesAndTasksModel schedulesAndTasksModel = JsonUtils.loadClass(context,
                SchedulesAndTasksModel.class,
                getTasksAndSchedulesResId());

        AppDatabase db = StorageAccess.getInstance().getAppDatabase();

        List<SchedulesAndTasksModel.ScheduleModel> schedules = new ArrayList<>();
        for(SchedulesAndTasksModel.ScheduleModel schedule : schedulesAndTasksModel.schedules)
        {
            if(schedule.tasks.size() == 0)
            {
                LogExt.e(getClass(), "No tasks in schedule");
                continue;
            }

            // only supporting one task per schedule for now
            SchedulesAndTasksModel.TaskScheduleModel task = schedule.tasks.get(0);

            if(task.taskFileName == null)
            {
                LogExt.e(getClass(), "No filename found for task with id: " + task.taskID);
                continue;
            }

            // loading the task json here is bad, but the taskID is in the schedule
            // json but the readable id is in the task json
            TaskModel taskModel = loadTaskModel(context, task);
            TaskResult result = db.loadLatestTaskResult(taskModel.identifier);

            // cache cron string for later lookup
            loadedTaskCrons.put(taskModel.identifier, schedule.scheduleString);

            if(result == null)
            {
                schedules.add(schedule);
            }
            else if(StringUtils.isNotEmpty(schedule.scheduleString))
            {
                Date date = ScheduleHelper.nextSchedule(schedule.scheduleString,
                        result.getEndDate());
                if(date.before(new Date()))
                {
                    schedules.add(schedule);
                }
            }
        }

        schedulesAndTasksModel.schedules = schedules;
        return schedulesAndTasksModel;
    }

    private TaskModel loadTaskModel(Context context, SchedulesAndTasksModel.TaskScheduleModel task)
    {
        TaskModel taskModel = JsonUtils.loadClass(context, TaskModel.class, task.taskFileName);

        // cache guid and createdOnDate
        loadedTaskGuids.put(taskModel.identifier, taskModel.guid);
        loadedTaskDates.put(taskModel.identifier, taskModel.createdOn);

        return taskModel;
    }

    @Override
    public SmartSurveyTask loadTask(Context context, SchedulesAndTasksModel.TaskScheduleModel task)
    {
        TaskModel taskModel = loadTaskModel(context, task);
        SmartSurveyTask smartSurveyTask = new SmartSurveyTask(taskModel);
        return smartSurveyTask;
    }

    @Override
    public void uploadTaskResult(Context context, TaskResult taskResult)
    {
        // Update/Create TaskNotificationService
        if(AppPrefs.getInstance(context).isTaskReminderEnabled())
        {
            Log.i("SampleDataProvider", "uploadTaskResult() _ isTaskReminderEnabled() = true");

            String chronTime = findChronTime(taskResult.getIdentifier());

            // If chronTime is null then either the task is not repeating OR its not found within
            // the task_and_schedules.xml
            if(chronTime != null)
            {
                scheduleReminderNotification(context, taskResult.getEndDate(), chronTime);
            }
        }

        List<BridgeDataInput> files = new ArrayList<>();

        for(StepResult stepResult : taskResult.getResults().values())
        {
            SurveyAnswer surveyAnswer = SurveyAnswer.create(stepResult);
            files.add(new BridgeDataInput(surveyAnswer,
                    SurveyAnswer.class,
                    stepResult.getIdentifier() + ".json",
                    FormatHelper.DEFAULT_FORMAT.format(stepResult.getEndDate())));
        }

        uploadBridgeData(context,
                new Info(getGuid(taskResult.getIdentifier()),
                        getCreatedOnDate(taskResult.getIdentifier())),
                files);
    }

    public void uploadBridgeData(Context context, Info info, BridgeDataInput... dataFiles)
    {
        uploadBridgeData(context, info, Arrays.asList(dataFiles));
    }

    public void uploadBridgeData(Context context, Info info, List<BridgeDataInput> dataFiles)
    {
        try
        {
            BridgeDataArchive archive = new BridgeDataArchive(info);
            archive.start(getFilesDir(context));

            for(BridgeDataInput dataFile : dataFiles)
            {
                archive.addFile(context, dataFile);
            }

            UploadRequest request = archive.finishAndEncrypt(context,
                    getPublicKeyResId(),
                    getFilesDir(context));

            ((UploadQueue) StorageAccess.getInstance().getAppDatabase()).saveUploadRequest(request);
            uploadPendingFiles(context);
        }
        catch(IOException e)
        {
            throw new RuntimeException("Error encrypting initial task data", e);
        }
    }

    // these stink, I should be able to query the DB and find these
    private String getCreatedOnDate(String identifier)
    {
        return loadedTaskDates.get(identifier);
    }

    private String getGuid(String identifier)
    {
        return loadedTaskGuids.get(identifier);
    }

    private String findChronTime(String identifier)
    {
        return loadedTaskCrons.get(identifier);
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
    public abstract void processInitialTaskResult(Context context, TaskResult taskResult);

    public void uploadPendingFiles(Context context)
    {
        if(! isSignedIn(context) || ! isConsented(context))
        {
            LogExt.d(getClass(), "User is not consented, skipping uploadPendingFiles()");
            return;
        }

        List<UploadRequest> uploadRequests = ((UploadQueue) StorageAccess.getInstance()
                .getAppDatabase()).loadUploadRequests();

        for(UploadRequest uploadRequest : uploadRequests)
        {
            if(uploadRequest.bridgeId == null)
            {
                LogExt.d(getClass(), "Starting upload for request: " + uploadRequest.name);
                uploadFile(context, uploadRequest);
            }
            else
            {
                LogExt.d(getClass(),
                        "Bridge ID found, confirming upload for: " + uploadRequest.name);
                confirmUpload(context, uploadRequest);
            }
        }
    }

    protected void uploadFile(Context context, UploadRequest request)
    {
        if(! isSignedIn(context) || ! isConsented(context))
        {
            LogExt.d(getClass(), "User is not consented, will upload later");
            return;
        }

        service.requestUploadSession(request)
                .flatMap(uploadSession -> uploadToS3(context, request, uploadSession))
                .flatMap(id -> {
                    LogExt.d(getClass(), "Notifying bridge of s3 upload: " + id);

                    // Updating request entry with Bridge ID for saving on success
                    request.bridgeId = id;

                    return service.uploadComplete(id);
                })
                .subscribeOn(Schedulers.io())
                .subscribe(completeResponse -> {
                    LogExt.d(getClass(), "Notified bridge of s3 upload, need to confirm");
                    // update UploadRequest in DB with id for later confirmation
                    ((UploadQueue) StorageAccess.getInstance().getAppDatabase()).saveUploadRequest(
                            request);
                }, error -> {
                    error.printStackTrace();
                    LogExt.e(getClass(), "Error uploading file to S3, will try again");
                    deleteUploadRequest(context, request);
                });
    }

    @NonNull
    private Observable<String> uploadToS3(Context context, UploadRequest request, UploadSession uploadSession)
    {
        // retrofit doesn't like making requests outside of your api, use okhttp to make the call
        return Observable.create(subscriber -> {
            // Request will fail without Content-MD5, Content-Type, and Content-Length
            LogExt.d(getClass(), "Uploading to S3");
            RequestBody body = RequestBody.create(MediaType.parse(request.contentType),
                    new File(getFilesDir(context), request.name));
            Request awsRequest = new Request.Builder().url(uploadSession.url)
                    .put(body)
                    .header("Content-MD5", request.contentMd5)
                    .build();

            okhttp3.Response response = null;
            try
            {
                response = new OkHttpClient().newCall(awsRequest).execute();

                if(response.isSuccessful())
                {
                    LogExt.d(getClass(), "Successful s3 upload");
                    subscriber.onNext(uploadSession.id);
                }
                else
                {
                    throw new RuntimeException("Response unsuccessful, code: " + response.code());
                }
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
        });
    }

    private void confirmUpload(Context context, UploadRequest request)
    {
        service.uploadStatus(request.bridgeId)
                .subscribeOn(Schedulers.io())
                .subscribe(uploadValidationStatus -> {
                    LogExt.d(getClass(), "Received validation status from Bridge(" +
                            uploadValidationStatus.getStatus() + ")");

                    switch(uploadValidationStatus.getStatus())
                    {
                        case UNKNOWN:
                        case VALIDATION_FAILED:
                            LogExt.e(getClass(), "Unrecoverable error, deleting");
                            // figure out what to actually do on unrecoverable, from a user perspective
                            deleteUploadRequest(context, request);
                            break;

                        case REQUESTED:
                            LogExt.e(getClass(),
                                    "Status is still 'requested' for some reason, will retry upload later");
                            // removing bridge id so upload is retried later
                            request.bridgeId = null;
                            ((UploadQueue) StorageAccess.getInstance()
                                    .getAppDatabase()).saveUploadRequest(request);
                            break;

                        case SUCCEEDED:
                            LogExt.d(getClass(), "Status is 'success', removing request locally");
                            deleteUploadRequest(context, request);
                            break;

                        case VALIDATION_IN_PROGRESS:
                        default:
                            LogExt.d(getClass(),
                                    "Status is pending, will retry confirmation later");
                            // No action necessary
                            break;

                    }
                }, error -> {
                    error.printStackTrace();
                    LogExt.e(getClass(), "Error connecting to Bridge server, will try again later");
                });
    }

    private void deleteUploadRequest(Context context, UploadRequest request)
    {
        ((UploadQueue) StorageAccess.getInstance().getAppDatabase()).deleteUploadRequest(request);

        File file = new File(getFilesDir(context), request.name);
        if(file.exists() && file.delete())
        {
            LogExt.d(getClass(), "Deleted file: " + file.getName());
        }
        else
        {
            LogExt.d(getClass(), "Could not delete file: " + request.name);
        }
    }

    // figure out what directory to save files in and where to put this method
    public static File getFilesDir(Context context)
    {
        return context.getFilesDir();
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
        @Headers("Content-Type: application/json")
        @POST("v3/auth/signUp")
        Observable<BridgeMessageResponse> signUp(@Body SignUpBody body);

        /**
         * @return One of the following responses
         * <ul>
         * <li><b>200</b> returns UserSessionInfo Object</li>
         * <li><b>404</b> error - "Credentials incorrect or missing"</li>
         * <li><b>412</b> error - "User has not consented to research"</li>
         * </ul>
         */
        @Headers("Content-Type: application/json")
        @POST("v3/auth/signIn")
        Observable<Response<UserSessionInfo>> signIn(@Body SignInBody body);

        @Headers("Content-Type: application/json")
        @POST("v3/subpopulations/{studyId}/consents/signature")
        Observable<Response<BridgeMessageResponse>> consentSignature(@Path("studyId") String studyId, @Body ConsentSignatureBody body);

        /**
         * @return Response code <b>200</b> w/ message explaining instructions on how the user should
         * proceed
         */
        @Headers("Content-Type: application/json")
        @POST("v3/auth/requestResetPassword")
        Observable<Response> requestResetPassword(@Body EmailBody body);


        @POST("v3/subpopulations/{studyId}/consents/signature/withdraw")
        Observable<Response<BridgeMessageResponse>> withdrawConsent(@Path("studyId") String studyId, @Body WithdrawalBody withdrawal);

        /**
         * @return Response code <b>200</b> w/ message explaining instructions on how the user should
         * proceed
         */
        @Headers("Content-Type: application/json")
        @POST("v3/auth/resendEmailVerification")
        Observable<DataResponse> resendEmailVerification(@Body EmailBody body);

        /**
         * @return Response code 200 w/ message telling user has been signed out
         */
        @POST("v3/auth/signOut")
        Observable<Response> signOut();

        @POST("v3/users/self/dataSharing")
        Observable<Response<BridgeMessageResponse>> dataSharing(@Body SharingOptionBody body);

        @Headers("Content-Type: application/json")
        @POST("v3/uploads")
        Observable<UploadSession> requestUploadSession(@Body UploadRequest body);

        @POST("v3/uploads/{id}/complete")
        Observable<BridgeMessageResponse> uploadComplete(@Path("id") String id);

        @GET("v3/uploadstatuses/{id}")
        Observable<UploadValidationStatus> uploadStatus(@Path("id") String id);
    }

}
