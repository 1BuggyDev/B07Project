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
    default public void onDataReceived(infoType type, Object data) {};
    default public void onDataWritten(infoType type, Object data) {};

    default public void onDataModified(infoType type, Object data) {};

    default public void onDataDeleted(infoType type, Object data) {};
}
