package dataAccess;

import java.util.ArrayList;

public interface FileListener {
    /**
     * The result of a file read for a specific piece of UserData
     * Files are sorted by age (oldest to newest)
     * @param type the type of data this file belongs to
     * @param data the data read (null if an error occurred)
     */
    default public void onFilesReceived(infoType type, ArrayList<byte[]> data) {}

    /**
     * The result of a file upload for a specific piece of UserData
     * @param wasSuccessful whether the file upload was successful or not
     */
    default public void onFileWritten(boolean wasSuccessful) {}

    /**
     * The result of a file deletion for a specific piece of UserData
     * @param wasSuccessful whether the file upload was successful or not
     */
    default public void onFileDeleted(boolean wasSuccessful) {}
}
