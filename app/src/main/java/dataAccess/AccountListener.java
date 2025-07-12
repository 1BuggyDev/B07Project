package dataAccess;

import com.google.firebase.auth.FirebaseUser;

/**
 * Interface for processing after account interactions<br>
 * Functions: <br>
 * public void onEmailLogin(FirebaseUser user); <br>
 * public void onAccountCreation(boolean success);
 */
public interface AccountListener {
    /**
     * The result of an attempted login
     * @param user the FirebaseUser, or null if the login failed
     */
    default public void onEmailLogin(FirebaseUser user) {};

    /**
     * The result of attempted account creation
     * @param success if the account creation was successful
     */
    default public void onAccountCreation(boolean success) {};

}
