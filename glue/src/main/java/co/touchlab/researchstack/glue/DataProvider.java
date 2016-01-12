package co.touchlab.researchstack.glue;
import android.content.Context;

import java.util.Date;

import rx.Observable;

public interface DataProvider
{
    Observable<DataResponse> signUp(Context context, String email, String username, String password);

    Observable<DataResponse> signIn(Context context, String username, String password);

    Observable<DataResponse> signOut(Context context);

    Observable<DataResponse> resendEmailVerification(Context context, String email);

    boolean isSignedUp(Context context);

    boolean isSignedIn(Context context);

    void saveConsent(Context context, String name, Date birthDate, String imageData, String signatureDate, String scope);

    String getUserEmail(Context context);

    Observable<DataResponse> initialize(Context context);

    //  TODO  void getUserProfile(TODO);

    //  TODO  void updateUserProfile(TODO);

    //  TODO  void saveSurveyResult(TODO);
}
