package co.touchlab.researchstack.sampleapp.network.body;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsentSignatureBody
{
    /**
     * The identifier for the study under which the user is signing in
     */
    private String study;

    /**
     * User's name
     */
    private String name;

    /**
     * User's birthdate
     */
    private String birthdate;

    /**
     * User's signature image data
     */
    private String imageData;

    /**
     * User's signature image mime type
     */
    private String imageMimeType;

    /**
     * User's sharing scope choice
     */
    private String scope;

    public ConsentSignatureBody(String study, String name, Date birthdate, String imageData, String imageMimeType, String scope)
    {
        this.study = study;
        this.name = name;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        this.birthdate = format.format(birthdate);
        this.imageData = imageData;
        this.imageMimeType = imageMimeType;
        this.scope = scope;
    }
}
