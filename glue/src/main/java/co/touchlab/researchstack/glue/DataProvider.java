package co.touchlab.researchstack.glue;
import android.content.Context;

import java.util.Date;

import rx.Observable;

public interface DataProvider
{
    Observable<DataResponse> signUp(String email, String username, String password);

    Observable<DataResponse> signIn(String username, String password);

    Observable<DataResponse> signOut();

    boolean isSignedUp();

    boolean isSignedIn();

    boolean isConsented();

    void saveConsent(Context context, String name, Date birthDate, String imageData, String signatureDate, String scope);

    String getUserEmail();

//  TODO  void getUserProfile(TODO);

//  TODO  void updateUserProfile(TODO);

//  TODO  void saveSurveyResult(TODO);
}
