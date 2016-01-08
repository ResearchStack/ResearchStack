package co.touchlab.researchstack.sampleapp;
import android.content.Context;

import com.google.gson.Gson;

import java.util.Date;

import co.touchlab.researchstack.core.StorageManager;
import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.storage.file.FileAccessException;
import co.touchlab.researchstack.glue.DataProvider;
import co.touchlab.researchstack.glue.DataResponse;
import co.touchlab.researchstack.glue.ui.scene.SignInStepLayout;
import co.touchlab.researchstack.sampleapp.bridge.BridgeMessageResponse;
import co.touchlab.researchstack.sampleapp.bridge.ConsentSignature;
import co.touchlab.researchstack.sampleapp.network.UserSessionInfo;
import co.touchlab.researchstack.sampleapp.network.body.EmailBody;
import co.touchlab.researchstack.sampleapp.network.body.SignInBody;
import co.touchlab.researchstack.sampleapp.network.body.SignUpBody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

public class SampleDataProvider implements DataProvider
{
    public static final String TEMP_CONSENT_JSON_FILE_NAME = "/consent_sig";
    public static final String TEMP_USER_EMAIL = "/user_email";

    //TODO Add build flavors, add var to BuildConfig for STUDY_ID
    String STUDY_ID = "ohsu-molemapper";

    //TODO Add build flavors, add var to BuildConfig for BASE_URL
    String BASE_URL = "https://webservices-staging.sagebridge.org/v3/";

    private BridgeService   service;
    private UserSessionInfo userSessionInfo;

    public SampleDataProvider()
    {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> LogExt.i(
                SignInStepLayout.class,
                message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(client)
                .build();

        service = retrofit.create(BridgeService.class);
    }

    @Override
    public Observable<DataResponse> signUp(Context context, String email, String username, String password)
    {
        //TODO Pass in a username, pass in data groups
        SignUpBody body = new SignUpBody(STUDY_ID, email, username, password, null, null);
        saveUserEmail(context, email);
        return service.signUp(body).map(message -> {
            DataResponse response = new DataResponse();
            response.success = true;
            return response;
        });
    }

    @Override
    public Observable<DataResponse> signIn(String username, String password)
    {
        SignInBody body = new SignInBody(STUDY_ID, username, password);
        return service.signIn(body).map(userSessionInfo -> {
            this.userSessionInfo = userSessionInfo;

            DataResponse response = new DataResponse();
            response.success = true;
            return response;
        });
    }

    @Override
    public Observable<DataResponse> signOut()
    {
        return null;
    }

    @Override
    public boolean isSignedUp()
    {
        return false;
    }

    @Override
    public boolean isSignedIn()
    {
        return false;
    }

    @Override
    public boolean isConsented()
    {
        return false;
    }

    @Override
    public void saveConsent(Context context, String name, Date birthDate, String imageData, String signatureDate, String scope)
    {
        ConsentSignature signature = new ConsentSignature(name,
                birthDate,
                imageData,
                "image/png",
                scope);

        Gson gson = new Gson();
        String jsonString = gson.toJson(signature);

        LogExt.d(getClass(), "Writing user json:\n" + signature);

        StorageManager.getFileAccess()
                .writeString(context, TEMP_CONSENT_JSON_FILE_NAME, jsonString);
    }

    @Override
    public String getUserEmail(Context context)
    {
        try
        {
            return StorageManager.getFileAccess().readString(context, TEMP_USER_EMAIL);
        }
        catch(FileAccessException e)
        {
            LogExt.w(getClass(), "TEMP USER EMAIL not readable");
            return null;
        }
    }

    // TODO this is a temporary solution
    private void saveUserEmail(Context context, String email)
    {
        StorageManager.getFileAccess()
                .writeString(context, TEMP_USER_EMAIL, email);
    }


    public interface BridgeService
    {

        /**
         * @return One of the following responses
         * <ul>
         *     <li><b>201</b> returns message that user has been signed up</li>
         *     <li><b>473</b> error - returns message that study is full</li>
         * </ul>
         */
        @POST("auth/signUp")
        Observable<BridgeMessageResponse> signUp(@Body SignUpBody body);

        /**
         * @return One of the following responses
         * <ul>
         *     <li><b>200</b> returns UserSessionInfo Object</li>
         *     <li><b>404</b> error - "Credentials incorrect or missing"</li>
         *     <li><b>412</b> error - "User has not consented to research"</li>
         * </ul>
         */
        @Headers({
                "Content-Type: application/json",
                "User-Agent: Mole Mapper/1"
        })
        @POST("auth/signIn")
        Observable<UserSessionInfo> signIn(@Body SignInBody body);

        /**
         * @return Response code <b>200</b> w/ message explaining instructions on how the user should
         * proceed
         */
        @POST("auth/requestResetPassword")
        Observable<Response> requestResetPassword(@Body EmailBody body);

        /**
         *
         * @return Response code 200 w/ message telling user has been signed out
         */
        @POST("auth/signOut")
        Observable<Response> signOut();
    }

}
