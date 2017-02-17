package org.researchstack.backbone.model;

import java.io.Serializable;
import java.util.Date;

/*
  Created by bradleymcdermott on 10/22/15.
 */
public class User implements Serializable
{
    private String name;

    private String email;

    private Date birthDate;

    private UserHealth userHealth;

    /**
     * See description above DataSharingScope inner enum below
     */
    private DataSharingScope dataSharingScope;

    /** Default constructor for Serializable */
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

    public Date getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate(Date birthDate)
    {
        this.birthDate = birthDate;
    }

    public DataSharingScope getDataSharingScope() {
        return dataSharingScope;
    }

    public void setDataSharingScope(DataSharingScope dataSharingScope) {
        this.dataSharingScope = dataSharingScope;
    }

    public UserHealth getUserHealth() {
        return userHealth;
    }

    public void setUserHealth(UserHealth userHealth) {
        this.userHealth = userHealth;
    }

    /*!
     * DataSharingScope is an enumeration of the choices for the scope of sharing collected data.
     * NONE  - The user has not consented to sharing their data.
     * STUDY - The user has consented only to sharing their de-identified data with the sponsors and partners of the current research study.
     * ALL   - The user has consented to sharing their de-identified data for current and future research, which may or may not involve the same institutions or investigators.
     */
    public enum DataSharingScope {
        NONE("no_sharing"),
        STUDY("sponsors_and_partners"),
        ALL("all_qualified_researchers");

        private String identifier;

        DataSharingScope(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }
    }
}
