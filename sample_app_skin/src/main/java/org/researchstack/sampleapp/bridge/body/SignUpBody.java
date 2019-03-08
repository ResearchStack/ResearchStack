package org.researchstack.sampleapp.bridge.body;
public class SignUpBody
{

    /**
     * The identifier for the study under which the user is signing in
     */
    private String study;

    /**
     * User's email address, cannot be change once created
     */
    private String email;

    /**
     * User's username
     */
    private String username;

    /**
     * User's password. Constraints for an acceptable password can be set per study.
     */
    private String password;

    /**
     * An array of roles to assign to this user (admins only)
     */
    private String[] roles;

    /**
     * An array of data group tags to assign to this user. Client applications can set this up
     * during sign up, it's not required to be an admin to add these.
     */
    private String[] dataGroups;

    private String type = "SignUp";

    public SignUpBody(String study, String email, String username, String password, String[] roles, String[] dataGroups)
    {
        this.study = study;
        this.email = email;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.dataGroups = dataGroups;
    }
}
