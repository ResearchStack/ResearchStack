package org.researchstack.backbone.result.logger;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;
import android.util.Log;

import org.researchstack.backbone.utils.LogExt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by TheMDP on 2/9/17.
 *
 * The DataLoggerFileWriterThread uses a HandlerThread to send byte[] data to another thread
 * which writes it to a file asynchronously
 */

public class DataLoggerFileWriterThread {

    private static final String BUNDLE_KEY_BYTE_DATA  = "bytedata";

    private static final int MSG_WRITE_REQUEST  = 1;
    private static final int MSG_STOP           = 2;
    private static final int MSG_CANCEL         = 3;

    /**
     * File output stream to write to the file
     * this object SHOULD NOT be touched on the main thread, and only from within the Handler
     */
    private FileOutputStream fileOutputStream;

    /**
     * The listener for this class, will only be called from the main thread
     */
    private final DataLogger.DataWriteListener writeListener;

    private final File file;
    private final String fileHeader;
    private final String fileFooter;

    private HandlerThread thread;
    private FileWriterHandler threadHandler;
    private Handler mainHandler;

    protected DataLoggerFileWriterThread(
            File file,
            String fileHeader,
            String fileFooter,
            DataLogger.DataWriteListener listener)
    {
        this.file = file;
        this.fileHeader = fileHeader;
        this.fileFooter = fileFooter;
        this.writeListener = listener;
    }

    private class FileWriterHandler extends Handler {

        private FileWriterHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case MSG_WRITE_REQUEST:

                        openFileStreamIfNull();
                        Bundle bundle = msg.getData();
                        if (bundle != null) {
                            byte[] bytesToWrite = bundle.getByteArray(BUNDLE_KEY_BYTE_DATA);
                            if (bytesToWrite != null) {
                                fileOutputStream.write(bytesToWrite);
                            }
                        }

                        break;
                    case MSG_STOP:

                        // call openFileStreamIfNull to combat an edge case where no write requests were processed
                        openFileStreamIfNull();
                        closeFileStream();
                        writeCompleteFromThreadToMainThread();

                        break;
                    case MSG_CANCEL:

                        closeFileStream();
                        writeCanceledFromThreadToMainThread();

                        break;
                }
            } catch (final IOException e) {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException closingException) {
                        closingException.printStackTrace();
                    }
                }
                writeFailedFromThreadToMainThread(e);
            }
        }

        private void openFileStreamIfNull() throws IOException {
            if (fileOutputStream == null) {
                fileOutputStream = new FileOutputStream(file, true);
                // Write fileHeader if there is one
                if (fileHeader != null) {
                    fileOutputStream.write(fileHeader.getBytes(DataLogger.UTF_8));
                }
            }
        }

        private void closeFileStream() throws IOException {
            if (fileOutputStream != null) {
                if (fileFooter != null) {
                    fileOutputStream.write(fileFooter.getBytes(DataLogger.UTF_8));
                }
                fileOutputStream.close();
            }
        }
    }

    @MainThread
    protected void start() {
        if (thread != null) {
            throw new IllegalStateException("Cannot call start while thread is running");
        }


        thread = new HandlerThread(file.getName());
        mainHandler = new Handler();
        thread.start();
        threadHandler = new FileWriterHandler(thread.getLooper());
    }

    /**
     * Will stop the thread which will soon after call the listener's complete callback
     */
    @MainThread
    protected void stop() {
        threadHandler.sendEmptyMessage(MSG_STOP);
    }

    @MainThread
    protected void cancel() {
        threadHandler.sendEmptyMessage(MSG_CANCEL);
    }

    // Only should be called from thread handler
    private void writeFailedFromThreadToMainThread(final Throwable throwable) {
        thread.quit();
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                writeListener.onWriteError(throwable);
            }
        });
    }

    // Only should be called from thread handler
    private void writeCompleteFromThreadToMainThread() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                thread.quit();
                writeListener.onWriteComplete(file);
            }
        });
    }

    // Only should be called from thread handler
    private void writeCanceledFromThreadToMainThread() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                thread.quit();
                boolean success = file.delete();
                if (!success) {
                    LogExt.d(getClass(), "Failed to delete file " + file.toString());
                }
            }
        });
    }

    /**
     * @param data append data to the file, which will be sent to the thread for writing
     */
    protected void appendData(byte[] data) {
        final Bundle bundle = new Bundle();
        bundle.putByteArray(BUNDLE_KEY_BYTE_DATA, data);
        Message message = Message.obtain(threadHandler, MSG_WRITE_REQUEST);
        message.setData(bundle);
        message.sendToTarget();
    }
}
