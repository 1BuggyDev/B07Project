package com.example.b07_project21;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.MissingResourceException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import dataAccess.PinManager;

public class PinManagerUnitTests {
    @Test
    public void pinExistsTestTrue() {
        Context mockContext = mock(Context.class);
        SharedPreferences mockSharedPreferences = mock(SharedPreferences.class);

        when(mockContext.getSharedPreferences(anyString(), eq(Context.MODE_PRIVATE))).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.getString("pin", null)).thenReturn("");

        boolean res = PinManager.pinExists(mockContext);
        assertTrue(res);
    }

    @Test
    public void pinExistsTestFalse() {
        Context mockContext = mock(Context.class);
        SharedPreferences mockSharedPreferences = mock(SharedPreferences.class);

        when(mockContext.getSharedPreferences(anyString(), eq(Context.MODE_PRIVATE))).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.getString("pin", null)).thenReturn(null);

        boolean res = PinManager.pinExists(mockContext);
        assertFalse(res);
    }

    @Test
    public void setPinTest() {
        Context mockContext = mock(Context.class);
        SharedPreferences mockSharedPreferences = mock(SharedPreferences.class);
        SharedPreferences.Editor mockEditor = mock(SharedPreferences.Editor.class);
        when(mockContext.getSharedPreferences(anyString(), eq(Context.MODE_PRIVATE))).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);

        String pin = "1024";
        PinManager.setPin(pin, mockContext);

        verify(mockEditor, times(1)).putString(eq("pin"), anyString());
        verify(mockEditor, times(1)).putString(eq("salt"), anyString());
        verify(mockEditor, times(1)).apply();
    }

    @Test
    public void validatePinTestTrue() throws Exception {
        String pin = "testpin";
        String salt = "testsalt";
        String encrypted = applyEncryption(pin, Base64.getDecoder().decode(salt));
        Context mockContext = mock(Context.class);
        SharedPreferences mockSharedPreferences = mock(SharedPreferences.class);
        when(mockContext.getSharedPreferences(anyString(), eq(Context.MODE_PRIVATE))).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.getString("pin", null)).thenReturn(encrypted);
        when(mockSharedPreferences.getString("salt", null)).thenReturn(salt);

        boolean res = PinManager.validatePin(pin, mockContext);
        assertTrue(res);
    }

    @Test
    public void validatePinTestFalse() throws Exception {
        String pin = "testpin";
        String salt = "testsalt";
        String encrypted = applyEncryption(pin, Base64.getDecoder().decode(salt)) + "this will make it false";
        Context mockContext = mock(Context.class);
        SharedPreferences mockSharedPreferences = mock(SharedPreferences.class);
        when(mockContext.getSharedPreferences(anyString(), eq(Context.MODE_PRIVATE))).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.getString("pin", null)).thenReturn(encrypted);
        when(mockSharedPreferences.getString("salt", null)).thenReturn(salt);

        boolean res = PinManager.validatePin(pin, mockContext);
        assertFalse(res);
    }

    @Test
    public void validatePinTestExceptionMissingPin() {
        Context mockContext = mock(Context.class);
        SharedPreferences mockSharedPreferences = mock(SharedPreferences.class);
        when(mockContext.getSharedPreferences(anyString(), eq(Context.MODE_PRIVATE))).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.getString("pin", null)).thenReturn(null);
        when(mockSharedPreferences.getString("salt", null)).thenReturn("testsalt");

        MissingResourceException e = assertThrows(MissingResourceException.class, () -> PinManager.validatePin("testpin", mockContext));
        assertEquals("Cannot find encrypted pin", e.getMessage());
    }

    @Test
    public void validatePinTestExceptionMissingSalt() {
        Context mockContext = mock(Context.class);
        SharedPreferences mockSharedPreferences = mock(SharedPreferences.class);
        when(mockContext.getSharedPreferences(anyString(), eq(Context.MODE_PRIVATE))).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.getString("pin", null)).thenReturn("testpin");
        when(mockSharedPreferences.getString("salt", null)).thenReturn(null);

        MissingResourceException e = assertThrows(MissingResourceException.class, () -> PinManager.validatePin("testpin", mockContext));
        assertEquals("Cannot find salt", e.getMessage());
    }


    /**
     * Grabbing from PinManager.java for testing
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
}