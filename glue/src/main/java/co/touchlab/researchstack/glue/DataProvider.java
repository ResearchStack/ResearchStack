package co.touchlab.researchstack.glue;
import rx.Observable;

public interface DataProvider
{
    Observable<DataResponse> signUp(String username, String password);

    Observable<DataResponse> signIn(String username, String password);

    Observable<DataResponse> signOut();

    boolean isSignedUp();

    boolean isSignedIn();

    boolean isConsented();

//  TODO  void saveConsent(TODO);

//  TODO  void getUserProfile(TODO);

//  TODO  void updateUserProfile(TODO);

//  TODO  void saveSurveyResult(TODO);
}
