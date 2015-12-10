package co.touchlab.researchstack.core.model;
import android.content.res.Resources;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import co.touchlab.researchstack.core.R;

/**
 * Consent document that stores Signature images, titles, etc for rendering a consent document.
 *
 * The following methods are marked as deprecated due to be unfinished and not-fully implemented.
 * This {@link #createHTMLWithTitle} method is meant to take the data (e.g. {@link ConsentSection}s'
 * and generate a consent document for you. This method is available on iOS and means that the
 * developer does not have to convert their consent-pdf file
 * (e.g. assets/study_overview_consent_form.pdf) and generate a HTML document from it. We, however,
 * will be requiring the researcher to do just.
 *
 * TODO Remove following methods
 * {@link #createHTMLWithTitle}
 * {@link #createHTML}
 * {@link #wrapHTMLBody}
 * {@link #getHTMLForSection}
 * {@link #getHTMLForSignature}
 * {@link #getCSSStyleSheet}
 */
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
    private int signaturePageTitle;

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

    public void setSignaturePageTitle(@StringRes int signaturePageTitle)
    {
        this.signaturePageTitle = signaturePageTitle;
    }

    @StringRes
    public int getSignaturePageTitle()
    {
        return signaturePageTitle;
    }

    public void setSignaturePageContent(String signaturePageContent)
    {
        this.signaturePageContent = signaturePageContent;
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

    @Deprecated
    public String createHTMLWithTitle(Resources r, String title, String detail)
    {
        return createHTML(r, true, title, detail).toString();
    }

    @Deprecated
    public StringBuilder createHTML(Resources r, boolean isMobile,  String title, String detail)
    {
        StringBuilder body = new StringBuilder();

        // header
        body.append("<div class='header'>");

        if (! TextUtils.isEmpty(title)) {
            body.append(String.format("<h1>%1$s</h1>", title));
        }

        if (!TextUtils.isEmpty(detail)) {
            body.append(String.format("<p>%1$s</p>", detail));
        }

        body.append("</div>");

        if (!TextUtils.isEmpty(htmlReviewContent)) {
            body.append(htmlReviewContent);
        } else {

            // title
            body.append(String.format("<h3>%1$s</h3>", !TextUtils.isEmpty(title) ? title : ""));

            // scenes
            for (ConsentSection section : sections)
            {
                body.append(getHTMLForSection(section));
            }

            if (! isMobile) {
                // page break
                String sigPageTitle = r.getString(signaturePageTitle);
                body.append(String.format("<h4 class=\"pagebreak\" >%1$s</h4>", !TextUtils.isEmpty(sigPageTitle) ? sigPageTitle : ""));
                body.append(String.format("<p>%1$s</p>", !TextUtils.isEmpty(signaturePageContent) ? signaturePageContent : ""));

                for (ConsentSignature signature : signatures)
                {
                    body.append(getHTMLForSignature(r, signature));
                }
            }
        }

        return wrapHTMLBody(r, isMobile, body);
    }

    @Deprecated
    public StringBuilder wrapHTMLBody(Resources r, boolean isMobile, StringBuilder body)
    {
        return new StringBuilder("<html><head><style>")
                .append(getCSSStyleSheet(r, isMobile))
                .append("</style></head><body>")
                .append(body)
                .append("</body></html>");
    }

    @Deprecated
    private StringBuilder getHTMLForSection(ConsentSection section)
    {
        StringBuilder consent = new StringBuilder();
        String title = !TextUtils.isEmpty(section.getFormalTitle()) ? section.getFormalTitle() : !TextUtils.isEmpty(section.getTitle()) ? section.getTitle(): "";
        consent.append(String.format("<h4>%1$s</h4>", title));
        String body = !TextUtils.isEmpty(section.getHtmlContent()) ? section.getHtmlContent() : !TextUtils.isEmpty(section.getEscapedContent()) ? section.getEscapedContent(): "";
        consent.append(String.format("<p>%1$s</p>", body));
        return consent;
    }

    @Deprecated
    private StringBuilder getHTMLForSignature(Resources r, ConsentSignature signature)
    {
        StringBuilder body = new StringBuilder();

        String hr = "<hr align='left' width='100%' style='height:1px; border:none; color:#000; background-color:#000; margin-top: -10px; margin-bottom: 0px;' />";

        String signatureElementWrapper = "<p><br/><div class='sigbox'><div class='inbox'>%1$s</div></div>%2$s%3$s</p>";

        boolean addedSig = false;

        List<String> signatureElements = new ArrayList<>();

        // Signature
        if (signature.isRequiresName() || !TextUtils.isEmpty(signature.getFullName())) {
            addedSig = true;
            String nameStr = "&nbsp;";

            if (!TextUtils.isEmpty(signature.getFullName())) {
                nameStr = signature.getFullName();
            }

            String formattedTitle = r.getString(R.string.consent_doc_line_printed_name, signature.getTitle());
            signatureElements.add(String.format(signatureElementWrapper, nameStr, hr, formattedTitle));
        }

        boolean hasSigImage = !TextUtils.isEmpty(signature.getSignatureImage());
        if (signature.isRequiresSignatureImage() || hasSigImage) {
            addedSig = true;
            String imageTag = "&nbsp;";

            if (hasSigImage) {
                imageTag = String.format("<img width='100%%' alt='star' src='data:image/png;base64,%1$s' />", signature.getSignatureImage());
            } else {
                body.append("<br/>");
            }
            String formattedTitle = r.getString(R.string.consent_doc_line_signature, signature.getTitle());
            signatureElements.add(String.format(signatureElementWrapper, imageTag, hr, formattedTitle));
        }

        if (addedSig) {
            String sigDate = !TextUtils.isEmpty(signature.getSignatureDate()) ? signature.getSignatureDate() : "&nbsp;";
            signatureElements.add(String.format(signatureElementWrapper, sigDate, hr,
                                                r.getString(R.string.consent_doc_line_date)));
        }

        int numElements = signatureElements.size();
        if (numElements > 1) {
            body.append("<div class='grid border'>");
            for (String element : signatureElements) {
                body.append(String.format("<div class='col-1-3 border'>%1$s</div>", element));
            }
            body.append("</div>");
        } else if (numElements == 1) {
            body.append(String.format("<div width='200'>%1$s</div>", signatureElements.get(0)));
        }
        return body;
    }

    @Deprecated
    private StringBuilder getCSSStyleSheet(Resources r, boolean isMobile)
    {
        StringBuilder css = new StringBuilder("@media print { .pagebreak { page-break-before: always; } }\n");

        if (isMobile) {
            css.append(".header { margin-top: 36px ; margin-bottom: 30px; text-align: center; }\n");
            css.append("body { margin-left: 0px; margin-right: 0px; }\n");

            float [] pointSizes = new float[]
                    {
                            r.getDimension(R.dimen.text_size_xlarge), // H1
                            r.getDimension(R.dimen.text_size_large), // H2
                            r.getDimension(R.dimen.text_size_default), // H3, Body, P
                            r.getDimension(R.dimen.text_size_small), // H4
                            r.getDimension(R.dimen.text_size_xsmall), // H5
                            r.getDimension(R.dimen.text_size_xxsmall), // H6
                    };

            css.append(String.format("h1 { font-family: sans-serif ; font-weight: 300; font-size: %1$s pt; }\n", pointSizes[0]));
            css.append(String.format("h2 { font-family: sans-serif ; font-weight: 300; font-size: %1$s pt; text-align: left; margin-top: 2em; }\n", pointSizes[1]));
            css.append(String.format("h3 { font-family: sans-serif ; font-size: %1$s pt; margin-top: 2em; }\n", pointSizes[2]));
            css.append(String.format("h4 { font-family: sans-serif ; font-size: %1$s pt; margin-top: 2em; }\n", pointSizes[3]));
            css.append(String.format("h5 { font-family: sans-serif ; font-size: %1$s pt; margin-top: 2em; }\n", pointSizes[4]));
            css.append(String.format("h6 { font-family: sans-serif ; font-size: %1$s pt; margin-top: 2em; }\n", pointSizes[5]));
            css.append(String.format("body { font-family: sans-serif; font-size: %1$s pt; }\n", pointSizes[3]));
            css.append(String.format("p, blockquote, ul, fieldset, form, ol, dl, dir, { font-family: sans-serif; font-size: %1$s pt; margin-top: -.5em; }\n", pointSizes[3]));
        } else {
            css.append("h1, h2 { text-align: center; }\n");
            css.append("h2, h3 { margin-top: 3em; }\n");
            css.append("body, p, h1, h2, h3 { font-family: sans-serif; }\n");
        }

        css.append(String.format(".col-1-3 { width: %1$s; float: left; padding-right: 20px; }\n", isMobile ? "66.6%" : "33.3%"));
        css.append(".sigbox { position: relative; height: 100px; max-height:100px; display: inline-block; bottom: 10px }\n");
        css.append(".inbox { position: relative; top: 100%%; transform: translateY(-100%%); -webkit-transform: translateY(-100%%);  }\n");
        css.append(".grid:after { content: \"\"; display: table; clear: both; }\n");
        css.append(".border { -webkit-box-sizing: border-box; box-sizing: border-box; }\n");

        return css;
    }

    public void setHtmlReviewContent(String htmlReviewContent)
    {
        this.htmlReviewContent = htmlReviewContent;
    }

    public String getHtmlReviewContent()
    {
        return htmlReviewContent;
    }


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
