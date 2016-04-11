package org.researchstack.sampleapp.bridge.body;
public class SignInBody
{

    /**
     * The identifier for the study under which the user is signing in
     */
    private String study;

    /**
     * User's username or email address
     */
    private String username;

    /**
     * User's password
     */
    private String password;

    public SignInBody(String study, String username, String password)
    {
        this.study = study;
        this.username = username;
        this.password = password;
    }
}
