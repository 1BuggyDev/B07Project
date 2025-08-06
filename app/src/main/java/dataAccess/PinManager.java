package dataAccess;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.MissingResourceException;

/**
 * A class to validate and save pins.
 */
public class PinManager {

    /**
     * Private constructor to prevent instantiation
     */
    private PinManager() {}

    /**
     * Given the current context and a pin entered, determines if the pin is correct
     * @param pin the pin to check
     * @param context the current context
     * @return true if the pin is correct, false otherwise
     * @throws MissingResourceException if there is no pin/salt initialized
     * @throws Exception if an error occurred while attempting to hash the pin
     */
    public static boolean validatePin(String pin, Context context) throws MissingResourceException, Exception {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName() + "_encrypted_pin", Context.MODE_PRIVATE);
        String encryptedPin = sharedPreferences.getString("pin", null);
        if(encryptedPin == null) {
            throw new MissingResourceException("Cannot find encrypted pin", "String", "pin");
        }

        String strSalt = sharedPreferences.getString("salt", null);
        if(strSalt == null) {
            throw new MissingResourceException("Cannot find salt", "String", "salt");
        }

        byte[] salt = Base64.getDecoder().decode(strSalt);

        String encryptedInput = applyEncryption(pin, salt);
        if(encryptedInput == null) {
            throw new Exception("Unable to run encryption algorithm");
        }

        return encryptedPin.equals(encryptedInput);
    }

    /**
     * Determines if a pin has been created so far
     * @param context the current context
     * @return true if a pin exists, false otherwise
     */
    public static boolean pinExists(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName() + "_encrypted_pin", Context.MODE_PRIVATE);
        String encryptedPin = sharedPreferences.getString("pin", null);

        return encryptedPin != null;
    }

    /**
     * Sets the pin
     * @param pin the pin to store
     * @param context the current context
     */
    public static void setPin(String pin, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName() + "_encrypted_pin", Context.MODE_PRIVATE);
        byte[] salt = getSalt();
        String encryptedPin = applyEncryption(pin, salt);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("salt", Base64.getEncoder().encodeToString(salt));
        editor.putString("pin", encryptedPin);
        editor.apply();
    }

    /**
     * Generates a random salt
     * @return the random salt
     */
    private static byte[] getSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Applies PBKDF2 hashing to the given pin using a given salt
     * @param pin the pin to hash
     * @param salt the salt to hash with
     * @return the hashed result
     */
    private static String applyEncryption(String pin, byte[] salt) {
        KeySpec spec = new PBEKeySpec(pin.toCharArray(), salt, 32768, 256);
        SecretKeyFactory factory;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch(NoSuchAlgorithmException e) {
            Log.e("PinManager.applyEncryption", "algorithm instance does not exist", e);
            return null;
        }

        byte[] hash;
        try {
            hash = factory.generateSecret(spec).getEncoded();
        } catch(InvalidKeySpecException e) {
            Log.e("PinManager.applyEncryption", "KeySpec is invalid", e);
            return null;
        }

        return Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Removes the pin data
     * @param context the current context
     */
    public static void removePin(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName() + "_encrypted_pin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("pin");
        editor.remove("salt");
        editor.commit();
    }
}