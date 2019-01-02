package org.researchstack.backbone.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.UUID;

public class ConsentSignature implements Serializable, Cloneable {

    /**
     * A Boolean value indicating whether the user needs to enter their name during consent review.
     * <p>
     * The default value of this property is <code>true</code>. In a consent review step, the name entry screen is not displayed when the value of this property is <code>false</code>.
     */
    private boolean requiresName;

    /**
     * A Boolean value indicating whether the user needs to draw a signature during consent review.
     * <p>
     * The default value of this property is <code>true</code>. In a consent review step, the signature entry
     * screen is not shown when this property is <code>false</code>.
     */
    private boolean requiresSignatureImage;

    /**
     * A Boolean value indicating whether the user needs to enter their birth date during consent
     * review.
     * <p>
     * The default value of this property is <code>true</code>. In a consent review step, the name entry screen
     * is not displayed when the value of this property is <code>false</code>.
     */
    private boolean requiresBirthDate;

    /**
     * The identifier for this signature.
     * <p>
     * The identifier should be unique in the document. It can be used to find or
     * replace a specific signature in an {@link ConsentDocument} object.
     */
    private String identifier;

    /**
     * The title of the signatory.
     */
    private String title;

    /**
     * Full name
     */
    private String fullName;

    /**
     * The base64-encoded image of the signature, if any.
     */
    private String signatureImage;

    /**
     * The date associated with the signature.
     */
    private String signatureDate;

    /**
     * The date format string to be used when producing a date string for the PDF
     * or consent review.
     * <p>
     * For example, @"yyyy-MM-dd 'at' HH:mm". When the value of this property is `nil`,
     * the current date and time for the current locale is used.
     */
    private String signatureDateFormatString;

    public ConsentSignature() {
        this.requiresName = true;
        this.requiresSignatureImage = true;
        this.identifier = UUID.randomUUID().toString();
    }

    /**
     * @param identifier     The identifier of the signature, unique within this document.
     * @param title          The title of the signatory.
     * @param dateFormat     The format string to use when formatting the date of signature.
     * @param fullName       The given name of the signatory.
     * @param signatureImage An image of the signature.
     * @param signatureDate  The date on which the signature was obtained, represented as a string.
     */
    public ConsentSignature(@NonNull String identifier, String title, String dateFormat, String fullName, String signatureImage, String signatureDate) {
        this();
        this.identifier = identifier;
        this.title = title;
        this.signatureDateFormatString = dateFormat;
        this.fullName = fullName;
        this.signatureImage = signatureImage;
        this.signatureDate = signatureDate;
    }

    /**
     * @param title      The title of the signatory.
     * @param dateFormat The format string to use when formatting the date of signature.
     * @param identifier The identifier of the signature, unique within this document.
     */
    public ConsentSignature(@NonNull String identifier, String title, String dateFormat) {
        this(identifier, title, dateFormat, null, null, null);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(@NonNull String identifier) {
        this.identifier = identifier;
    }

    public boolean requiresName() {
        return requiresName;
    }

    public void setRequiresName(boolean requiresName) {
        this.requiresName = requiresName;
    }

    public boolean requiresSignatureImage() {
        return requiresSignatureImage;
    }

    public void setRequiresSignatureImage(boolean requiresSignatureImage) {
        this.requiresSignatureImage = requiresSignatureImage;
    }

    public boolean requiresBirthDate() {
        return requiresBirthDate;
    }

    public void setRequiresBirthDate(boolean requiresBirthDate) {
        this.requiresBirthDate = requiresBirthDate;
    }

    public String getTitle() {
        return title;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSignatureImage() {
        return signatureImage;
    }

    public void setSignatureImage(String signatureImage) {
        this.signatureImage = signatureImage;
    }

    public String getSignatureDate() {
        return signatureDate;
    }

    public String getSignatureDateFormatString() {
        return signatureDateFormatString;
    }

}
