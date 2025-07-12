package dataAccess;

/**
 * Interface for processing after database operations<br>
 * Functions: <br>
 * public void onDataReceived(infoType type, Object data); <br>
 * public void onDataWritten(infoType type, Object data); <br>
 * public void onDataModified(infoType type, Object data); <br>
 * public void onDataDeleted(infoType type, Object data);
 */
public interface DataListener {
    /**
     * The result of data being read from the database
     * @param type the type of data read
     * @param data a container for the data read. null if an error occurred
     */
    default public void onDataReceived(infoType type, Object data) {};

    /**
     * The result after data was written to the database
     * @param type the type of data read/written
     * @param data a container for the data after the write. null if an error occurred
     */
    default public void onDataWritten(infoType type, Object data) {};

    /**
     * The result after data in the database was modified
     * @param type the type of data read/modified
     * @param data a container for the data after it was modified. null if an error occurred
     */
    default public void onDataModified(infoType type, Object data) {};

    /**
     * The result after data was deleted from the database
     * @param type the type of data read/modified
     * @param data a container for the data after data was deleted. null if an error occurred
     */
    default public void onDataDeleted(infoType type, Object data) {};
}
