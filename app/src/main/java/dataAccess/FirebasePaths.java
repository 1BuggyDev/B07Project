package dataAccess;

import com.google.firebase.auth.FirebaseUser;

import java.io.File;

/**
 * Class to create paths for Firebase reference access
 */
class FirebasePaths {
    /**
     * Private constructor to prevent instantiation
     */
    private FirebasePaths() {}

    /**
     * Gets the database reference path for the root of the user data
     * @param user the current user
     * @return the reference path to access the user's data
     */
    protected static String getPath(FirebaseUser user) {
        return getPath(null, user);
    }

    protected static String getPath(FirebaseUser user, String path) {
        return getPath(user) + "/" + path;
    }
    protected static String getPath(FirebaseUser user, String path, int count, File file) {
        //return getPath(user, path) + "/" + String.valueOf(count) + "_" + file.getName();
        return getPath(user, path) + "/" + file.getName();
    }

    /**
     * Gets the database reference path for the given type of data
     * @param type the type of data
     * @param user the current user logged in
     * @return the reference path to access the user's data of the specific type
     */
    protected static String getPath(infoType type, FirebaseUser user) {
        String path = "users/" + user.getUid();
        if(type == null) {
            return path;
        }

        switch (type) {
            case ANSWER:
                path += "/answers";
                break;
            case DOCUMENT:
                path += "/documents";
                break;
            case CONTACT:
                path += "/emergency_contacts";
                break;
            case LOCATION:
                path += "/safe_locations";
                break;
            case MEDICATION:
                path += "/medications";
                break;
        }

        return path;
    }

    /**
     * Gets the reference path for the given type of data and the key of the specific object
     * @param type the type of data
     * @param user the current user
     * @param key the key to access the specific object
     *            For ANSWER: String key of the Map
     *            Otherwise: String.valueOf(index)
     * @return the reference path to access the data of the specified item
     */
    protected static String getPath(infoType type, FirebaseUser user, String key) {
        return getPath(type, user, key, null);
    }

    /**
     * Gets the reference path for the given type of data, the key of the specific object, and the file name
     * @param type the type of data
     * @param user the current user
     * @param key the key to access the specific object
     *            For ANSWER: String key of the Map
     *            Otherwise: String.valueOf(index)
     * @param file the file
     * @return the reference path to access the data of the specified file
     * @throws IllegalArgumentException if file is null
     */
    protected static String getPath(infoType type, FirebaseUser user, String key, File file) throws IllegalArgumentException {
        String path = getPath(type, user) + "/" + key;
        if(file == null) {
            return path;
        }

        path += "/" + file.getName();

        return path;
    }
}
