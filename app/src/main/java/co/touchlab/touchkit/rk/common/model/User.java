package co.touchlab.touchkit.rk.common.model;

import java.util.Date;

/*
  Created by bradleymcdermott on 10/22/15.
 */
public class User
{
    private String  name;

    private String  email;
    private String  password;
    private String  sessionToken;

//    private APCUserConsentSharingScope sharingScope;      // NOT stored to CoreData, reflected in "sharedOptionSelection"
    private int sharedOptionSelection;
    private String profileImage;

    private boolean consented; //Confirmation that server is consented. Should be used in the app to test for user consent.
    private boolean userConsented; //User has consented though not communicated to the server.

    private Date taskCompletion;
    private int hasHeartDisease;
    private int dailyScalesCompletionCounter;
    private String customSurveyQuestion;
    private String phoneNumber;
    private boolean allowContact;
    private String  medicalConditions;
    private String  medications;
    private String ethnicity;

    private Date sleepTime;
    private Date wakeUpTime;

    private String glucoseLevels;

    private String homeLocationAddress;
    private Number homeLocationLat;
    private Number homeLocationLong;

    private String consentSignatureName;
    private Date consentSignatureDate;
    private String consentSignatureImage;

    private boolean secondaryInfoSaved;

    private Date  birthDate;

    // TODO make classes for these?
    private String biologicalSex;
    private String bloodType;

    private int height;
    private int weight;
    private int systolicBloodPressure;

    private boolean signedUp = false;
    private boolean signedIn = false;
    private boolean loggedOut = true;

    public User()
    {

    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getSessionToken()
    {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken)
    {
        this.sessionToken = sessionToken;
    }

    public int getSharedOptionSelection()
    {
        return sharedOptionSelection;
    }

    public void setSharedOptionSelection(int sharedOptionSelection)
    {
        this.sharedOptionSelection = sharedOptionSelection;
    }

    public String getProfileImage()
    {
        return profileImage;
    }

    public void setProfileImage(String profileImage)
    {
        this.profileImage = profileImage;
    }

    public boolean isConsented()
    {
        return consented;
    }

    public void setConsented(boolean consented)
    {
        this.consented = consented;
    }

    public boolean isUserConsented()
    {
        return userConsented;
    }

    public void setUserConsented(boolean userConsented)
    {
        this.userConsented = userConsented;
    }

    public Date getTaskCompletion()
    {
        return taskCompletion;
    }

    public void setTaskCompletion(Date taskCompletion)
    {
        this.taskCompletion = taskCompletion;
    }

    public int getHasHeartDisease()
    {
        return hasHeartDisease;
    }

    public void setHasHeartDisease(int hasHeartDisease)
    {
        this.hasHeartDisease = hasHeartDisease;
    }

    public int getDailyScalesCompletionCounter()
    {
        return dailyScalesCompletionCounter;
    }

    public void setDailyScalesCompletionCounter(int dailyScalesCompletionCounter)
    {
        this.dailyScalesCompletionCounter = dailyScalesCompletionCounter;
    }

    public String getCustomSurveyQuestion()
    {
        return customSurveyQuestion;
    }

    public void setCustomSurveyQuestion(String customSurveyQuestion)
    {
        this.customSurveyQuestion = customSurveyQuestion;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public boolean isAllowContact()
    {
        return allowContact;
    }

    public void setAllowContact(boolean allowContact)
    {
        this.allowContact = allowContact;
    }

    public String getMedicalConditions()
    {
        return medicalConditions;
    }

    public void setMedicalConditions(String medicalConditions)
    {
        this.medicalConditions = medicalConditions;
    }

    public String getMedications()
    {
        return medications;
    }

    public void setMedications(String medications)
    {
        this.medications = medications;
    }

    public String getEthnicity()
    {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity)
    {
        this.ethnicity = ethnicity;
    }

    public Date getSleepTime()
    {
        return sleepTime;
    }

    public void setSleepTime(Date sleepTime)
    {
        this.sleepTime = sleepTime;
    }

    public Date getWakeUpTime()
    {
        return wakeUpTime;
    }

    public void setWakeUpTime(Date wakeUpTime)
    {
        this.wakeUpTime = wakeUpTime;
    }

    public String getGlucoseLevels()
    {
        return glucoseLevels;
    }

    public void setGlucoseLevels(String glucoseLevels)
    {
        this.glucoseLevels = glucoseLevels;
    }

    public String getHomeLocationAddress()
    {
        return homeLocationAddress;
    }

    public void setHomeLocationAddress(String homeLocationAddress)
    {
        this.homeLocationAddress = homeLocationAddress;
    }

    public Number getHomeLocationLat()
    {
        return homeLocationLat;
    }

    public void setHomeLocationLat(Number homeLocationLat)
    {
        this.homeLocationLat = homeLocationLat;
    }

    public Number getHomeLocationLong()
    {
        return homeLocationLong;
    }

    public void setHomeLocationLong(Number homeLocationLong)
    {
        this.homeLocationLong = homeLocationLong;
    }

    public String getConsentSignatureName()
    {
        return consentSignatureName;
    }

    public void setConsentSignatureName(String consentSignatureName)
    {
        this.consentSignatureName = consentSignatureName;
    }

    public Date getConsentSignatureDate()
    {
        return consentSignatureDate;
    }

    public void setConsentSignatureDate(Date consentSignatureDate)
    {
        this.consentSignatureDate = consentSignatureDate;
    }

    public String getConsentSignatureImage()
    {
        return consentSignatureImage;
    }

    public void setConsentSignatureImage(String consentSignatureImage)
    {
        this.consentSignatureImage = consentSignatureImage;
    }

    public boolean isSecondaryInfoSaved()
    {
        return secondaryInfoSaved;
    }

    public void setSecondaryInfoSaved(boolean secondaryInfoSaved)
    {
        this.secondaryInfoSaved = secondaryInfoSaved;
    }

    public Date getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate(Date birthDate)
    {
        this.birthDate = birthDate;
    }

    public String getBiologicalSex()
    {
        return biologicalSex;
    }

    public void setBiologicalSex(String biologicalSex)
    {
        this.biologicalSex = biologicalSex;
    }

    public String getBloodType()
    {
        return bloodType;
    }

    public void setBloodType(String bloodType)
    {
        this.bloodType = bloodType;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public int getWeight()
    {
        return weight;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    public int getSystolicBloodPressure()
    {
        return systolicBloodPressure;
    }

    public void setSystolicBloodPressure(int systolicBloodPressure)
    {
        this.systolicBloodPressure = systolicBloodPressure;
    }

    public boolean isSignedUp()
    {
        return signedUp;
    }

    public void setSignedUp(boolean signedUp)
    {
        this.signedUp = signedUp;
    }

    public boolean isSignedIn()
    {
        return signedIn;
    }

    public void setSignedIn(boolean signedIn)
    {
        this.signedIn = signedIn;
    }

    public boolean isLoggedOut()
    {
        return loggedOut;
    }

    public void setLoggedOut(boolean loggedOut)
    {
        this.loggedOut = loggedOut;
    }
}
