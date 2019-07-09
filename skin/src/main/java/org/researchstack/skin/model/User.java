package org.researchstack.skin.model;

import java.io.Serializable;

/*
  Created by bradleymcdermott on 10/22/15.
 */
public class User implements Serializable {
    private String name;

    private String email;

    private String birthDate;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

}
