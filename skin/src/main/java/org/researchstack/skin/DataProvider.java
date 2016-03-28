package org.researchstack.skin;
import android.content.Context;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.Task;
import org.researchstack.skin.model.SchedulesAndTasksModel;
import org.researchstack.skin.model.User;

import java.util.Date;

import rx.Observable;

public abstract class DataProvider
{
    private static DataProvider instance;

    public DataProvider()
    {
    }

    public static DataProvider getInstance()
    {
        if(instance == null)
        {
            throw new RuntimeException(
                    "DataProvider instance is null. Make sure to init a concrete implementation of ResearchStack in Application.onCreate()");
        }

        return instance;
    }

    public static void init(DataProvider manager)
    {
        DataProvider.instance = manager;
    }

    public abstract Observable<DataResponse> initialize(Context context);

    public abstract Observable<DataResponse> signUp(Context context, String email, String username, String password);

    public abstract Observable<DataResponse> signIn(Context context, String username, String password);

    public abstract Observable<DataResponse> signOut(Context context);

    public abstract Observable<DataResponse> resendEmailVerification(Context context, String email);

    public abstract boolean isSignedUp(Context context);

    public abstract boolean isSignedIn(Context context);

    public abstract boolean isConsented(Context context);

    public abstract Observable<DataResponse> withdrawConsent(Context context, String reason);

    public abstract void saveConsent(Context context, String name, Date birthDate, String imageData, String signatureDate, String scope);

    public abstract User getUser(Context context);

    public abstract String getUserSharingScope(Context context);

    public abstract void setUserSharingScope(Context context, String scope);

    public abstract String getUserEmail(Context context);

    public abstract void uploadTaskResult(Context context, TaskResult taskResult);

    public abstract SchedulesAndTasksModel loadTasksAndSchedules(Context context);

    public abstract Task loadTask(Context context, SchedulesAndTasksModel.TaskScheduleModel task);

    // This initial task may include profile items such as height and weight that may need to be
    // processed differently than a normal task result
    public abstract void processInitialTaskResult(Context context, TaskResult taskResult);
}
