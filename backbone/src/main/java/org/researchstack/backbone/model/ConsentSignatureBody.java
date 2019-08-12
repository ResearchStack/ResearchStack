package org.researchstack.backbone.model;

import java.util.Date;

public class ConsentSignatureBody {

  public ConsentSignatureBody() {
    // Default constructor
  }

  /**
   * The identifier for the study under which the user is signing in
   */
  public String study;

  /**
   * User's name
   */
  public String name;

  /**
   * User's birthdate
   */
  public Date birthdate;

  /**
   * User's signature image data
   */
  public String imageData;

  /**
   * User's signature image mime type
   */
  public String imageMimeType;

  /**
   * User's sharing scope choice
   */
  public String scope;

  public ConsentSignatureBody(String study, String name, Date birthdate, String imageData,
      String imageMimeType, String scope) {
    this.study = study;
    this.name = name;
    this.birthdate = birthdate;
    this.imageData = imageData;
    this.imageMimeType = imageMimeType;
    this.scope = scope;
  }
}
