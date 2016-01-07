package co.touchlab.researchstack.sampleapp.bridge;
import java.util.Date;

public class ConsentSignature
{

    private final String name;
    private final Date   birthdate;
    private final String imageData;
    private final String imageMimeType;
    private final String sharingScope;

    /**
     * <p>
     * Simple constructor.
     * </p>
     * <p>
     * imageData and imageMimeType are optional. However, if one of them is specified, both of them must be specified.
     * If they are specified, they must be non-empty.
     * </p>
     *
     * @param name
     *         name of the user giving consent, must be non-null and non-empty
     * @param birthdate
     *         user's birth date, must be non-null
     * @param imageData
     *         signature image data as a Base64 encoded string, optional
     * @param imageMimeType
     *         signature image MIME type (ex: image/png), optional
     * @param sharingScope TODO
     */
    // We use the standard LocalDateDeserializer from jackson-datatype-joda. However, we still need to use a
    // @JsonDeserialize annotation anyway or Jackson won't know what to do with it.
    public ConsentSignature(String name, Date birthdate, String imageData, String imageMimeType, String sharingScope) {
        this.name = name;
        this.birthdate = birthdate;
        this.imageData = imageData;
        this.imageMimeType = imageMimeType;
        this.sharingScope = sharingScope;
    }

    /** Name of the user giving consent. */
    public String getName() {
        return name;
    }

    /** User's birth date. */
    // We use a custom serializer, because the standard LocalDateSerializer serializes into a very unusual format.
    public Date getBirthdate() {
        return birthdate;
    }

    /** Signature image data as a Base64 encoded string. */
    public String getImageData() {
        return imageData;
    }

    /** Signature image MIME type (ex: image/png). */
    public String getImageMimeType() {
        return imageMimeType;
    }
}
