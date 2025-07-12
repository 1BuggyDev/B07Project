package dataAccess;

import static dataAccess.FirebasePaths.getPath;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.MissingResourceException;

/**
 * A class for managing user logins <br>
 * See Guides/LoginManagement.txt for a guide on usage
 */
public class LoginManager {
    private LoginManager(){};

    /**
     * Given a pin, tests if it is the correct pin for the account
     * @param pin the pin to test
     * @return true if the pin is correct, false otherwise
     * @throws MissingResourceException if there is a missing pin or salt
     * @throws Exception if an error occurred while attempting to hash the pin
     */
    public static FirebaseUser attemptLogin(String pin, Context context) throws Exception {
        if(PinManager.validatePin(pin, context)) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user == null) {
                throw new Exception("User null!");
            }
            DatabaseAccess.user = user;
            DatabaseAccess.db = FirebaseDatabase.getInstance("https://cscb07project-8e5e3-default-rtdb.firebaseio.com/");
            DatabaseAccess.ref = DatabaseAccess.db.getReference(getPath(user));
            StorageAccess.user = user;
            StorageAccess.storage = FirebaseStorage.getInstance();
            return user;
        }

        return null;
    }

    /**
     * Given an email and password, login to Firebase db
     * @param email the user's email
     * @param password the user's password
     * @param obj a class implementing the AccountListener interface to call onEmailLogin()
     */
    public static void attemptLogin(String email, String password, AccountListener obj) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseAccess.user = user;
                    DatabaseAccess.ref = DatabaseAccess.db.getReference(getPath(user));
                    StorageAccess.user = user;
                    StorageAccess.storage = FirebaseStorage.getInstance();
                    obj.onEmailLogin(user);
                    Log.d("Login", "Success");
                } else {
                    obj.onEmailLogin(null);
                    Log.d("Login", "Failure", task.getException());
                }
            }
        });
    }

    /**
     * Creates an account with the given email and password
     * @param email the user's email
     * @param password the user's password
     * @param obj a class implementing the AccountListener interface to call onAccountCreation()
     */
    public static void createAccount(String email, String password, AccountListener obj) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(obj == null) {
            auth.createUserWithEmailAndPassword(email, password);
            return;
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                obj.onAccountCreation(task.isSuccessful());
            }
        });
    }
}
