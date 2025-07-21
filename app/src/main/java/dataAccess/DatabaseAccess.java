package dataAccess;

import static dataAccess.FirebasePaths.getPath;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for accessing Firebase Realtime Database <br>
 * Note that this is not the same as Firebase Storage which is for user-uploaded files <br>
 * See Guides/DatabaseAccess.txt for a guide on using this class
 */
public class DatabaseAccess {
    protected static FirebaseUser user;
    protected static FirebaseDatabase db;
    protected static DatabaseReference ref;

    private static DataTree dataTree;

    private DatabaseAccess() {}

    /**
     * Checks if a user is signed in
     * @return true if a user is signed in, false otherwise
     */
    public static boolean isUserSignedIn() {
        return user != null;
    }

    /**
     * Checks if the dataTree has data
     * @return true if the dataTree has data, false otherwise
     */
    public static boolean isDataInitialized() {
        return dataTree != null;
    }

    /**
     * Creates a GenericTypeIndicator to tell Firebase db how to format data into objects
     * @param type the type of data
     * @return a GenericTypeIndicator to use when reading values fom the database
     */
    protected static GenericTypeIndicator<?> getGenericTypeIndicator(infoType type) {
        switch(type) {
            case ANSWER:
                return new GenericTypeIndicator<Map<String, String>>() {};
            case CONTACT:
                return new GenericTypeIndicator<ArrayList<EmergencyContact>>() {};
            case DOCUMENT:
                return new GenericTypeIndicator<ArrayList<Document>>() {};
            case LOCATION:
                return new GenericTypeIndicator<ArrayList<SafeLocation>>() {};
            case MEDICATION:
                return new GenericTypeIndicator<ArrayList<Medication>>() {};
            default:
                return null;
        }
    }

    /**
     * Creates an error message if the data is invalid for the infoType.
     * @param type the type of data
     * @param data the data to check
     * @return a String which is an error message if the data is invalid, null otherwise
     */
    private static String isDataValid(infoType type, Object data) {
        ArrayList<?> obj;
        switch (type) {
            case ANSWER:
                if((data instanceof Pair) && (((Pair<?>)data).x instanceof String)) {
                    return null;
                }
                return "Answer data must be of type Pair<String>";
            case CONTACT:
                if(!(data instanceof EmergencyContact)) {
                    return "Emergency Contact data must be of type EmergencyContact";
                }
                return null;
            case DOCUMENT:
                if(!(data instanceof Document)) {
                    return "Document data must be of type Document";
                }
                return null;
            case LOCATION:
                if(!(data instanceof SafeLocation)) {
                    return "Safe Location data must be of type SafeLocation";
                }
                return null;
            case MEDICATION:
                if(!(data instanceof Medication)) {
                    return "Medication data must be of type Medication";
                }
                return null;
        }

        return null;
    }

    /**
     * Creates an error message if the data is invalid for the infoType.
     * @param type the type of data
     * @param data the data to check
     * @param isNullAllowed is null considered valid
     * @return a String which is an error message if the data is invalid, null otherwise
     */
    private static String isDataValid(infoType type, Object data, boolean isNullAllowed) {
        if(isNullAllowed && data == null) {
            return null;
        }

        return isDataValid(type, data);
    }

    /**
     * Reads the user's data from the database and returns all data of the wanted type
     * @param type enum of DOCUMENT, CONTACT, LOCATION, MEDICATION representing the various types of
     *             user data determining the runtime type of the objects
     * @param obj A class that implements the DataListener interface so this function
     *            can call onDataReceived() after receiving the data from the database
     * @throws IllegalStateException if the current user is null
     * When using readData note that only the infoType and a generic Object is passed to onDataReceived()
     * It is your responsibility to cast the generic Object to the correct data type
     */
    public static void readData(infoType type, DataListener obj) throws IllegalStateException {
        if(!(isUserSignedIn())) {
            throw new IllegalStateException("Current user is null");
        }
        if(isDataInitialized()) {
            Object res = dataTree.getData(type);
            obj.onDataReceived(type, res);
            return;
        }
        Log.d("Read Test", "Reading from database");
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                dataTree = new DataTree(task.getResult());

                if(obj != null) {
                    obj.onDataReceived(type,  dataTree.getData(type));
                }
            }
        });
    }


    /**
     * Writes user data to the database
     * @param type the type of data
     * @param data the data (Note that it should be the correct type as seen in DatabaseAccess.txt)
     * @throws IllegalStateException if the current user is null
     * @throws IllegalArgumentException if data is not the correct data for the type of data
     */
    public static void writeData(infoType type, Object data) throws IllegalStateException {
        writeData(type, data, null);
    }

    /**
     * Writes user data to the database
     * @param type the type of data
     * @param data the data (Note that it should be the correct type as seen in DatabaseAccess.txt)
     * @param obj A class that implements the DataListener interface so this function can call
     *            onDataWritten() after data is written
     * @throws IllegalStateException if the current user is null
     * @throws IllegalArgumentException if data is not the correct data for the type of data
     */
    public static void writeData(infoType type, Object data, DataListener obj) throws IllegalStateException, IllegalArgumentException {
        if(!(isUserSignedIn())) {
            throw new IllegalStateException("Current user is null");
        }
        String err = isDataValid(type, data);
        if(err != null) {
            Log.e("Database", err);
            throw new IllegalArgumentException(err);
        }
        DatabaseReference writeRef = db.getReference(getPath(type, user));

        if(isDataInitialized()) {
            dataTree.addData(type, data);
            writeRef.setValue(dataTree.getData(type));
            if(obj != null) {
                obj.onDataWritten(type, dataTree.getData(type));
            }
            return;
        }

        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    dataTree = new DataTree(snapshot);
                    dataTree.addData(type, data);

                    if(obj == null)  {
                        writeRef.setValue(dataTree.getData(type));
                        return;
                    }

                    writeRef.setValue(dataTree.getData(type)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            obj.onDataWritten(type, dataTree.getData(type));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            obj.onDataWritten(type, null);
                            Log.e("DatabaseAccess.writeData", "Unable to write data", e);
                        }
                    });
                } else {
                    obj.onDataWritten(type, null);
                    Log.e("DatabaseAccess.writeData", "Error retrieving data", task.getException());
                }
            }
        });
    }

    /**
     * Deletes a single element of data by key for ANSWER data, otherwise by index.
     * @param type the type of data to remove
     * @param key the key to access the data (Map key for ANSWER data, String.valueOf(index) otherwise)
     * @throws IllegalStateException if the current user is null
     */
    public static void deleteData(infoType type, String key) throws IllegalStateException {
        editData(type, key, null, null);
    }

    /**
     * Deletes a single element of data by key for ANSWER data, otherwise by index.
     * @param type the type of data to remove
     * @param key the key to access the data (Map key for ANSWER data, String.valueOf(index) otherwise)
     * @param obj a class implementing the DataListener interface to call onDataModified()
     * @throws IllegalStateException if the current user is null
     */
    public static void deleteData(infoType type, String key, DataListener obj) throws IllegalStateException {
        editData(type, key, null, obj);
    }

    /**
     * Edits data of a specific type by an identifying key
     * @param type the type of data to modify
     * @param key the key to the exact piece of data: Map keys for ANSWER, String.valueOf(index) otherwise
     * @param newData the new data to replace
     * @param obj A class implementing the DataListener interface to call onDataModified()
     * @throws IllegalStateException if the current user is null
     * @throws IllegalArgumentException if newData is of an invalid type for the type of data
     */
    public static void editData(infoType type, String key, Object newData, DataListener obj) throws IllegalStateException, IllegalArgumentException {
        if(!(isUserSignedIn())) {
            throw new IllegalStateException("Current user is null");
        }

        String err = isDataValid(type, newData, true);
        if(err != null) {
            Log.e("Database", err);
            throw new IllegalArgumentException(err);
        }
        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put(key, newData);

        if(isDataInitialized()) {
            DatabaseReference writeRef = db.getReference(getPath(type, user));
            String storagePath = dataTree.getFilePath(type, key);

            if(type != infoType.ANSWER && storagePath != null) {
                if(newData == null) {
                    StorageAccess.deleteFiles(type, storagePath);
                } else if (!(storagePath.equals(((UserData) newData).getFilePath()))) {
                    StorageAccess.deleteFiles(type, storagePath);
                }
            }

            dataTree.updateData(type, key, newData);


            if(obj == null) {
                writeRef.updateChildren(updates);
                return;
            }

            writeRef.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        if(newData == null) {
                            StorageAccess.deleteFiles(type, key);
                            obj.onDataDeleted(type, dataTree.getData(type));
                        } else {
                            obj.onDataModified(type, dataTree.getData(type));
                        }
                    } else {
                        if(newData == null) {
                            obj.onDataDeleted(type, null);
                        } else {
                            obj.onDataModified(type, null);
                        }
                        Log.e("Database", "Error updating data");
                    }
                }
            });
            return;
        }

        //Log.d("Read Test", "Reading from database");
        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                dataTree = new DataTree(task.getResult());
                DatabaseReference writeRef = db.getReference(getPath(type, user));
                String storagePath = dataTree.getFilePath(type, key);

                if(type != infoType.ANSWER && storagePath != null) {
                    if(newData == null) {
                        StorageAccess.deleteFiles(type, storagePath);
                    } else if (!(storagePath.equals(((UserData) newData).getFilePath()))) {
                        StorageAccess.deleteFiles(type, storagePath);
                    }
                }
                dataTree.updateData(type, key, newData);
                if(obj == null) {
                    writeRef.updateChildren(updates);
                    return;
                }
                writeRef.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            if(newData == null) {
                                StorageAccess.deleteFiles(type, key);
                                obj.onDataDeleted(type, dataTree.getData(type));
                            } else {
                                obj.onDataModified(type, dataTree.getData(type));
                            }
                        } else {
                            if(newData == null) {
                                obj.onDataDeleted(type, null);
                            } else {
                                obj.onDataModified(type, null);
                            }
                            Log.e("Database", "Error updating data");
                        }
                    }
                });
            }
        });
    }

    /**
     * Edits data of a specific type by an identifying key
     * @param type the type of data to modify
     * @param key the key to the exact piece of data: Map keys for ANSWER, String.valueOf(index) otherwise
     * @param newData the new data to replace
     * @throws IllegalStateException if the current user is null
     * @throws IllegalArgumentException if newData is of an invalid type for the type of data
     */
    public static void editData(infoType type, String key, Object newData) {
        editData(type, key, newData,null);
    }
}
