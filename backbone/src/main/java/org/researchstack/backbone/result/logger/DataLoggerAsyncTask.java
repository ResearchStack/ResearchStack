package org.researchstack.backbone.result.logger;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by TheMDP on 2/7/17.
 */

class DataLoggerAsyncTask extends AsyncTask<Void, Void, Throwable> {

    /**
     * The file outputstream that writes data to the stream
     */
    private FileOutputStream  fileOutputStream;

    /**
     * Piped streams can send byte data between threads
     */
    private PipedOutputStream pipedOutputStream;
    private PipedInputStream  pipedInputStream;

    /**
     * The listener for this class, will only be called from the main thread
     */
    private DataLogger.DataWriteListener writeListener;

    private File              file;
    private long              writeSleepTime;

    private AtomicBoolean stopSignal;

    private String fileHeader;
    private String fileFooter;

    protected DataLoggerAsyncTask(
            File file,
            String fileHeader,
            String fileFooter,
            double estimatedDataWriteFrequency,
            DataLogger.DataWriteListener listener)
    {
        this.file = file;
        this.fileHeader = fileHeader;
        this.fileFooter = fileFooter;
        this.writeSleepTime = (long)(1000.0f / (float)estimatedDataWriteFrequency);
        this.writeListener = listener;
    }

    /**
     * Will stop the thread which will soon after call the listener's complete callback
     */
    protected void stop() {
        stopSignal = new AtomicBoolean(true);
    }

    /**
     * @param data append data to the file, which will be sent to the thread for writing
     */
    protected void appendData(byte[] data) {
        if (pipedOutputStream != null) {
            try {
                pipedOutputStream.write(data);
            } catch (IOException e) {
                writeListener.onWriteError(e);
                cancel(true);
            }
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        stopSignal = new AtomicBoolean(false);
        pipedOutputStream = new PipedOutputStream();
    }

    @Override
    protected Throwable doInBackground(Void... voids) {

        try {
            pipedInputStream = new PipedInputStream(pipedOutputStream);
            fileOutputStream = new FileOutputStream(file, true);

            // Write fileHeader if there is one
            if (fileHeader != null) {
                fileOutputStream.write(fileHeader.getBytes(DataLogger.UTF_8));
            }

            while (!isCancelled() && !stopSignal.get()) {
                while (pipedInputStream.available() > 0) {
                    byte[] readBytes = new byte[pipedInputStream.available()];
                    int lengthRead = pipedInputStream.read(readBytes, 0, readBytes.length);
                    fileOutputStream.write(readBytes, 0, lengthRead);
                }
                Thread.sleep(writeSleepTime);
            }

            // write the remaining bytes if we stopped on purpose
            if (stopSignal.get()) {
                while (pipedInputStream.available() > 0) {
                    byte[] readBytes = new byte[pipedInputStream.available()];
                    int lengthRead = pipedInputStream.read(readBytes, 0, readBytes.length);
                    fileOutputStream.write(readBytes, 0, lengthRead);
                }

                // write file footer if one exists
                if (fileFooter != null) {
                    fileOutputStream.write(fileFooter.getBytes(DataLogger.UTF_8));
                }
            }

        } catch (IOException | InterruptedException e) {
            return e;
        }

        if (isCancelled()) {
            return new CancellationException("Thread was cancelled, do not do any callbacks");
        }

        return null; // success
    }

    @Override
    public void onPostExecute(Throwable throwable) {

        if (fileOutputStream != null) {

            try {
                fileOutputStream.close();
                fileOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (pipedInputStream != null) {
            try {
                pipedInputStream.close();
                pipedInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (pipedOutputStream != null) {
            try {
                pipedOutputStream.close();
                pipedOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (throwable == null) {
            writeListener.onWriteComplete();
        } else if (!(throwable instanceof CancellationException)) {
            writeListener.onWriteError(throwable);
        }
    }
}
