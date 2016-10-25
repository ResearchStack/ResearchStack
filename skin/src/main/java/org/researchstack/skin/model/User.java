package org.researchstack.skin.model;

import java.io.Serializable;

/*
  Created by bradleymcdermott on 10/22/15.
 */
public class User implements Serializable
{
    private String fullname;

    private String name;

    private String password;

    private String email;

    private String birthDate;

    public User() {
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    /**
     * Retrieves the username, as registered on the server
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the username, as registered on the server.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Gets the password of the user, as registered on the server.
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Sets the password of the user.
     * This function should be called once the sign-up phase is completed.
     */
    public void setPassword(String pass)
    {
        this.password = pass;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate(String birthDate)
    {
        this.birthDate = birthDate;
    }

}
