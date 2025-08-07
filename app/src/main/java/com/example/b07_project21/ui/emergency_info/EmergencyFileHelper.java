package com.example.b07_project21.ui.emergency_info;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import dataAccess.FileData;
import dataAccess.StorageAccess;
import dataAccess.infoType;

public class EmergencyFileHelper {
    /** Opens specified file. */
    public static void openFile(Context context, FileData fileData) {
        File file = byteToFile(context, fileData.getData(), fileData.getName()); // /data/user/0/com.example.b07_project21/cache/6.jpg
        Uri uri = EmergencyFileHelper.fileToUri(context, file); // content://com.example.b07_project21.provider/cache/6.jpg

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, fileData.getType());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    /** Downloads specified file. */
    public static void downloadFile(Context context, FileData fileData) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileData.getName());
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, fileData.getType());

        Uri uri = contentResolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), contentValues);
        if (uri == null) {
            Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show();
            return;
        }

        try (OutputStream out = contentResolver.openOutputStream(uri)) {
            InputStream in = new ByteArrayInputStream(fileData.getData());
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            Toast.makeText(context, "Download Successful", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show();
        }
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

    public static File byteToFile(Context context, byte[] byteFile, String name) {
        File file = new File(context.getCacheDir(), name);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(byteFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//        System.out.println(file);
        return file;
    }

    public static byte[] fileToBytes(File file) throws IOException {
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            fis.close();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
