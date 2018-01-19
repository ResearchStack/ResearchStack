package org.researchstack.backbone.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.print.HtmlToPdfPrinter;
import android.print.PrintAttributes;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

public class RSHTMLPDFWriter
{
    private static String SIGNED_PDF_FILENAME = "consent-signed.pdf";

    public static File getPDFFilePath(Context context)
    {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    public static String getPDFFileName(String taskId)
    {
        return taskId+"_"+SIGNED_PDF_FILENAME;
    }

    public static String getPDFPath(Context context, String taskId)
    {
        return getPDFFilePath(context)+"/"+getPDFFileName(taskId);
    }

    protected void printPdfFile(final Activity context, final String taskId, String htmlConsentDocument) {
        // Create a WebView object specifically for printing
        WebView webView = new WebView(context);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                createWebPrintJob(context, view, taskId);
            }
        });

        webView.loadDataWithBaseURL(null, htmlConsentDocument, "text/HTML", "UTF-8", null);
    }

    private void createWebPrintJob(Context context, WebView webView, String taskId) {
        String jobName = "JobDocument";
        PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();
        // write document content to file
        File path = getPDFFilePath(context);
        String ouput = getPDFFileName(taskId);
        HtmlToPdfPrinter pdfPrint = new HtmlToPdfPrinter(attributes);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            pdfPrint.print(webView.createPrintDocumentAdapter(jobName), path, ouput);
        }else
        {
            pdfPrint.print(webView.createPrintDocumentAdapter(), path, ouput);
        }
    }
}
