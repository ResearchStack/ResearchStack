package co.touchlab.researchstack.sampleapp;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;

import co.touchlab.researchstack.core.StorageAccess;
import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.storage.file.FileAccessException;
import co.touchlab.researchstack.glue.DataProvider;
import co.touchlab.researchstack.glue.DataResponse;
import co.touchlab.researchstack.glue.model.User;
import co.touchlab.researchstack.glue.ui.scene.SignInStepLayout;
import co.touchlab.researchstack.sampleapp.bridge.BridgeMessageResponse;
import co.touchlab.researchstack.sampleapp.network.UserSessionInfo;
import co.touchlab.researchstack.sampleapp.network.body.ConsentSignatureBody;
import co.touchlab.researchstack.sampleapp.network.body.EmailBody;
import co.touchlab.researchstack.sampleapp.network.body.SignInBody;
import co.touchlab.researchstack.sampleapp.network.body.SignUpBody;
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
    public static final String TEMP_USER_JSON_FILE_NAME = "/temp_user";
    public static final String TEMP_CONSENT_JSON_FILE_NAME = "/consent_sig";
    public static final String TEMP_USER_EMAIL             = "/user_email";
    public static final String USER_SESSION_PATH           = "/user_session";

    //TODO Add build flavors, add var to BuildConfig for STUDY_ID
    public static final String STUDY_ID = "ohsu-molemapper";

    //TODO Add build flavors, add var to BuildConfig for BASE_URL
    String BASE_URL = "https://webservices-staging.sagebridge.org/v3/";

    private BridgeService   service;
    private UserSessionInfo userSessionInfo;
    private Gson    gson     = new Gson();
    private boolean signedIn = false;
    private String userEmail;

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
            UserSessionInfo userSessionInfo = loadUserSession(context);
            signedIn = userSessionInfo != null;
            userEmail = loadUserEmail(context);

            buildRetrofitService(userSessionInfo);
            subscriber.onNext(new DataResponse(true, null));

            if(userSessionInfo != null && ! userSessionInfo.isConsented())
            {
                try
                {
                    ConsentSignatureBody consent = loadConsentSignatureBody(context);
                    uploadConsent(consent);
                }
                catch(Exception e)
                {
                    throw new RuntimeException("Error loading consent", e);
                }
            }
        });
    }

    @Override
    public Observable<DataResponse> signUp(Context context, String email, String username, String password)
    {
        //TODO pass in data groups, remove roles
        SignUpBody body = new SignUpBody(STUDY_ID, email, username, password, null, null);
        saveUserEmail(context, email);
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
                saveUserSession(context, userSessionInfo);
                buildRetrofitService(userSessionInfo);
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
        return userEmail != null;
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

        String jsonString = gson.toJson(signature);

        LogExt.d(getClass(), "Writing user json:\n" + signature);

        writeJsonString(context, jsonString, TEMP_CONSENT_JSON_FILE_NAME);
    }

    private ConsentSignatureBody loadConsentSignatureBody(Context context)
    {
        String consentJson = loadJsonString(context, TEMP_CONSENT_JSON_FILE_NAME);
        return gson.fromJson(consentJson, ConsentSignatureBody.class);
    }

    private void uploadConsent(ConsentSignatureBody consent)
    {
        service.consentSignature(consent).subscribe(response -> {
            LogExt.d(getClass(),
                    "Response: " + response.code() + ", message: " + response.body().getMessage());
        });
    }

    @Override
    public String getUserEmail(Context context)
    {
        return userEmail;
    }

    // TODO this is a temporary solution
    private void saveUserEmail(Context context, String email)
    {
        writeJsonString(context, email, TEMP_USER_EMAIL);
    }

    @Nullable
    private String loadUserEmail(Context context)
    {
        String email = null;
        try
        {
            email = loadJsonString(context, TEMP_USER_EMAIL);
        }
        catch(FileAccessException e)
        {
            LogExt.w(getClass(), "TEMP USER EMAIL not readable");
        }
        return email;
    }

    private void saveUserSession(Context context, UserSessionInfo userInfo)
    {
        String userSessionJson = gson.toJson(userInfo);
        writeJsonString(context, userSessionJson, USER_SESSION_PATH);
    }

    private void writeJsonString(Context context, String userSessionJson, String userSessionPath)
    {
        StorageAccess.getInstance().saveFile(context, userSessionPath, userSessionJson.getBytes());
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
        return new String(StorageAccess.getInstance().loadFile(context, path));
    }

    /**
     * TODO use this for deciding what info to collect during signup, hardcoded in layouts for now
     */
    @Override
    public User.UserInfoType[] getUserInfoTypes()
    {
        return new User.UserInfoType[] {
                User.UserInfoType.Name,
                User.UserInfoType.Email,
                User.UserInfoType.BiologicalSex,
                User.UserInfoType.DateOfBirth,
                User.UserInfoType.Height,
                User.UserInfoType.Weight
        };
    }

    @Deprecated
    public void clearUserData(Context context)
    {
        // TODO make this work again
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
    }

}
