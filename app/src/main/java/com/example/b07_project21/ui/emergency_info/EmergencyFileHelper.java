package com.example.b07_project21.ui.emergency_info;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import dataAccess.StorageAccess;
import dataAccess.infoType;

public class EmergencyFileHelper {
    public static void openFile(Context context, File file) {
        if (file == null || !file.exists()) {
            Toast.makeText(context, "File does not exist", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = EmergencyFileHelper.fileToUri(context, file);

        // Get type
        String type = context.getContentResolver().getType(uri);
        if (type == null) type = "*/*";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(context.getPackageManager()) != null)
            context.startActivity(intent);
        else
            Toast.makeText(context, "Cannot open file - no app found", Toast.LENGTH_SHORT).show();
    }

    /** Converts Uri to File. Note Uri has no file name. */
    public static File uriToFile(Context context, Uri uri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);) {
            if (inputStream == null) return null;

            File temp = File.createTempFile("temp_", ".tmp");
            temp.deleteOnExit();

            try (OutputStream outputStream = new FileOutputStream(temp)) {
                byte[] buffer = new byte[1024];
                int len = inputStream.read(buffer);
                while (len != -1) {
                    outputStream.write(buffer, 0, len);
                    len = inputStream.read(buffer);
                }
            }
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Uri fileToUri(Context context, File file) {
        if (file == null || !file.exists()) return null;
        return FileProvider.getUriForFile(context, "com.example.b07_project21.provider", file);
    }

    public static String getUriName(Context context, Uri uri) {
        if (uri == null) return null;

        String name = null;
        if (uri.getScheme().equals("content")) { // content://
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    name = cursor.getString(index);
                }
            }
        } else if (uri.getScheme().equals("file")) { // file://
            name = new File(uri.getPath()).getName();
        }

        return name;
    }

//    public static File byteToFile(Context context, byte[] byteFile, String name) {
//        File file = new File(context.getCacheDir(), name);
//        try (FileOutputStream fos = new FileOutputStream(file)) {
//            fos.write(byteFile);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        return file;
//    }

//    public static byte[] pathToBytes(String path, EmergencyFileManager fileManager) {
//        StorageAccess.readFiles(infoType.DOCUMENT, path, fileManager);
//    }
}
