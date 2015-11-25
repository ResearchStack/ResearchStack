package co.touchlab.researchstack.common;

/**
 * Created by bradleymcdermott on 10/27/15.
 */
public class Constants
{
    public static final String KEY_SETTINGS_AUTO_LOCK = "auto_lock_time";

    public enum UserInfoType
    {
        Name,
        Email,
        Password,
        DateOfBirth,
        MedicalCondition,
        Medication,
        BloodType,
        Weight,
        Height,
        BiologicalSex,
        SleepTime,
        WakeUpTime,
        GlucoseLevel,
        CustomSurvey,
        AutoLock,
        Passcode,
        ReminderOnOff,
        ReminderTime,
        Permissions,
        ReviewConsent,
        PrivacyPolicy,
        LicenseInformation,
        SharingOptions
    }


    public enum StudyItemType
    {
        StudyDetails,
        Share,
        ReviewConsent
    }


    public enum LearnItemType
    {
        StudyDetails,
        OtherDetails,
        ReviewConsent,
        Share
    }

    public enum Consented
    {
        NotConsented,
        Consented
    }

    public static final float SignUpProgressBarHeight = 14.0f;

    public static final int PasswordMinimumLength = 2;


    public static final String UserInfoFieldNameRegEx = "[A-Za-z\\ ]+";

    public static final String GeneralInfoItemUserNameRegEx = "[A-Za-z0-9_.]+";

    public static final String GeneralInfoItemEmailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";

    public static final String MedicalInfoItemWeightRegEx = "[0-9]{1,4}";

    public static final String MedicalInfoItemSleepTimeFormat = "hh:mm a";


    public static final String AppStateKey = "APCAppState";

    public static final String PasscodeKey = "APCPasscode";
}
