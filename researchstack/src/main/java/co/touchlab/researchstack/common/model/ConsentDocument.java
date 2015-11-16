package co.touchlab.researchstack.common.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConsentDocument implements Serializable
{

    /**
     * The document's title in a localized string.
     *
     * The title appears only in the generated PDF for review; it is not used in the
     * visual consent process.
     */
    private String title;

    /**
     * The sections to be in printed in the PDF file and or presented in the
     * visual consent sequence.
     *
     * All sections appear in the animated process, except for those sections of the
     * type `ORKConsentSectionTypeOnlyInDocument`.
     *
     * If the `htmlReviewContent` property is not set, this content is also used to
     * populate the document for review in the consent review step.
     *
     * The PDF file contains all sections.
     */
    private List <ConsentSection> sections;

    /**
     * The title to be rendered on the signature page of the generated PDF in a localized string.
     *
     * The title is ignored for visual consent. The title is also ignored if you supply a value for the `htmlReviewContent` property.
     */
    private String signaturePageTitle;

    /**
     * The content to be rendered below the title on the signature page of the generated PDF in a localized string.
     *
     * The content is ignored for visual consent. The content is also ignored if you supply a value for the `htmlReviewContent` property.
     */
    private String signaturePageContent;

    /**
     * The set of signatures that are required or prepopulated in the document.
     *
     * To add a signature to the document after consent review, the `signatures` array
     * needs to be modified to incorporate the new signature content prior to PDF
     * generation. For more information, see `[ORKConsentSignatureResult applyToDocument:]`.
     */
    private List<ConsentSignature> signatures = new ArrayList<>(1);

    /**
     * Override HTML content for review.
     *
     * Typically, the review content is generated from the values of the `sections` and `signatures`
     * properties.
     *
     * When this property is set, the review content is reproduced exactly as provided in the property
     * in the consent review step, and the `sections` and `signatures` properties
     * are ignored.
     */
    private String htmlReviewContent;


    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setSignaturePageTitle(String signaturePageTitle)
    {
        this.signaturePageTitle = signaturePageTitle;
    }

    public void setSignaturePageContent(String signaturePageContent)
    {
        this.signaturePageTitle = signaturePageContent;
    }

    public void setSections(List<ConsentSection> sections)
    {
        this.sections = sections;
    }

    /**
     * Adds a signature to the array of signatures.
     *
     * @param signature    The signature object to add to the document.
     */
    public void addSignature(ConsentSignature signature)
    {
        signatures.add(signature);
    }

    public List<ConsentSection> getSections()
    {
        return sections;
    }


    //     TODO PDF generation

//    /**
//     * Initializer with ORKHTMLPDFWriter parameter. Allows for injecting mock dependency for the
//     * purposes of isolated unit testing.
//     *
//     * @param writer              The instance of the ORKHTMLPDFWriter upon which the class depends.
//     * @param sectionFormatter    An instance of ORKConsentSectionFormatter
//     * @param signatureFormatter  An instance of ORKConsentSignatureFormatter
//     */
//    - (instancetype)initWithHTMLPDFWriter:(ORKHTMLPDFWriter *)writer
//    consentSectionFormatter:(ORKConsentSectionFormatter *)sectionFormatter
//    consentSignatureFormatter:(ORKConsentSignatureFormatter *)signatureFormatter;
//
//    /**
//     * Writes the document's content into a PDF file.
//     *
//     * The PDF is generated in a form suitable for printing. This is done asynchronously,
//     * so the PDF data is returned through a completion block.
//     *
//     * @param handler     The handler block for generated PDF data. When successful, the returned
//     * data represents a complete PDF document that represents the consent.
//     */
//    - (void)makePDFWithCompletionHandler:(void (^)(NSData * __nullable PDFData, NSError * __nullable error))handler;
}
