package dataAccess;

import static dataAccess.FirebasePaths.getPath;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for accessing Firebase Storage <br>
 * Note that this is not the same as Firebase Database which is for non-file data <br>
 * See Guides/StorageAccess.txt for a guide on using this class
 */
public final class StorageAccess {
    protected static FirebaseStorage storage;
    protected static FirebaseUser user;

    private StorageAccess() {}

    public static String getUserID() {
        return user.getUid();
    }

    /**
     * Checks if a user is signed in
     * @return true if a user is signed in, false otherwise
     */
    public static boolean isUserSignedIn() {
        return user != null;
    }


    /**
     * Uploads the given file to firebase storage
     * @param type the type of data the file is linked to
     * @param path the filePath in the object the file is linked to
     * @param file the file to upload
     * @param obj a class implementing the FireListener interface to call onFileWritten()
     * @throws IllegalStateException if the current user is null
     * @throws IllegalArgumentException if the file is null or the file is too large (>5mb)
     */
    public static void uploadFile(infoType type, String path, File file, FileListener obj) throws IllegalStateException, IllegalArgumentException {
        if(user == null) {
            throw new IllegalStateException("Current user is null");
        }

        if(file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        if(file.length() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size too large");
        }

        StorageReference ref = storage.getReference(getPath(user, path));
        ref.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if(task.isSuccessful()) {
                    Uri uploadFile = Uri.fromFile(file);
                    StorageReference writeRef = storage.getReference(getPath(user, path, file));
                    UploadTask uploadTask = writeRef.putFile(uploadFile);
                    if(obj == null) {
                        return;
                    }

                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            obj.onFileWritten(task.isSuccessful());
                        }
                    });
                } else {
                    if(obj == null) {
                        return;
                    }
                    obj.onFileWritten(false);
                }
            }
        });
    }

    /**
     * Uploads the given file to firebase storage
     * @param type the type of data the file is linked to
     * @param path the filePath in the object the file is linked to
     * @param file the file to upload
     * @throws IllegalStateException if the current user is null
     * @throws IllegalArgumentException if the file is null or the file is too large (>5mb)
     */
    public static void uploadFile(infoType type, String path, File file) throws IllegalStateException, IllegalArgumentException {
        uploadFile(type, path, file, null);
    }

    /**
     * Reads all files associated with a piece of user data in order of upload
     * @param type the type of data
     * @param path the filePath in the object the file is linked to
     * @param obj a class implementing the FileListener interface to call onFilesReceived()
     * @throws IllegalStateException if the user is null
     * @throws IllegalArgumentException if obj is null
     */
    public static void readFiles(infoType type, String path, FileListener obj) throws IllegalStateException, IllegalArgumentException {
        if (user == null) {
            throw new IllegalStateException("Current user is null");
        }

        if(obj == null) {
            throw new IllegalArgumentException("Must pass an object to call");
        }

        final long fiveMB = 5 * 1024 * 1024;

        StorageReference ref = storage.getReference(getPath(user, path));
        ref.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if(task.isSuccessful()) {
                    List<StorageReference> items = task.getResult().getItems();

                    ArrayList<Task<byte[]>> processes = new ArrayList<>();

                    ArrayList<Task<StorageMetadata>> metadataProcesses = new ArrayList<>();

                    ArrayList<String> names = new ArrayList<>();

                    //Queue both metadata and file data
                    for(StorageReference fileRef: items) {
                        Log.d("FileTest", "file: " + fileRef.getName());
                        names.add(fileRef.getName());
                        Task<byte[]> readProcess = fileRef.getBytes(fiveMB);
                        Task<StorageMetadata> metadataProcess = fileRef.getMetadata();
                        processes.add(readProcess);
                        metadataProcesses.add(metadataProcess);
                    }
                    //Wait for metadata to get file creation times
                    ArrayList<Long> times = new ArrayList<Long>();
                    ArrayList<String> fileTypes = new ArrayList<>();
                    Tasks.whenAllSuccess(metadataProcesses).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> objects) {
                            //store file creation times
                            for(Object o: objects) {
                                StorageMetadata object = (StorageMetadata) o;
                                times.add(object.getCreationTimeMillis());
                                fileTypes.add(object.getContentType());
                            }

                            Tasks.whenAllSuccess(processes).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                                @Override
                                public void onSuccess(List<Object> objects) {
                                    int idx = 0;
                                    //store file data
                                    ArrayList<FileData> data = new ArrayList<>();
                                    for(Object o: objects) {
                                        FileData object = new FileData((byte[]) o, names.get(idx), fileTypes.get(idx));
                                        data.add(object);
                                        idx += 1;
                                    }

                                    //sort based on metadata times
                                    sortByAge(data, times);


                                    obj.onFilesReceived(type, data);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    obj.onFilesReceived(type, null);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            obj.onFilesReceived(type, null);
                        }
                    });
                } else {
                    obj.onFilesReceived(type, null);
                }
            }
        });

    }

    /**
     * Bubble sort
     * @param data the data to sort
     * @param times an ArrayList containing the metadata creation times
     * @return data sorted using bubble sort
     */
    protected static <T> void sortByAge(List<T> data, ArrayList<Long> times) {
        for(int i = 0; i < times.size(); i++) {
            boolean swapPerformed = false;
            for(int j = 0; j < times.size() - 1 - i; j++) {
                //out of order, swap
                if(times.get(j) > times.get(j + 1)) {
                    swapPerformed = true;
                    Long tempLong = times.get(j);
                    times.set(j, times.get(j + 1));
                    times.set(j + 1, tempLong);

                    T tempArr = data.get(j);
                    data.set(j, data.get(j + 1));
                    data.set(j + 1, tempArr);
                }
            }

            if(!(swapPerformed)) {
                break;
            }
        }
    }

    /**
     * Deletes all files associated with a piece of data
     * @param type the type of data
     * @param path the filePath in the object the file is linked to
     * @param obj a class implementing the FileListener interface to call onFileDeleted()
     */
    public static void deleteFiles(infoType type, String path, FileListener obj) {
        StorageReference ref = storage.getReference(getPath(user, path));

        ref.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if(task.isSuccessful()) {
                    ArrayList<Task<Void>> processes = new ArrayList<>();
                    for(StorageReference fileRef: task.getResult().getItems()) {
                        Task<Void> t = fileRef.delete();
                        processes.add(t);
                    }
                    if(obj == null) {
                        return;
                    }
                    Tasks.whenAllSuccess(processes).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> objects) {
                            obj.onFileDeleted(true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            obj.onFileDeleted(false);
                        }
                    });
                } else {
                    obj.onFileDeleted(false);
                }
            }
        });
    }

    /**
     * Deletes all files associated with a piece of data
     * @param type the type of data
     * @param path the filePath in the object the file is linked to
     */
    public static void deleteFiles(infoType type, String path) {
        deleteFiles(type, path, null);
    }

    /**
     * Deletes a specific file associated with a piece of data
     * @param type the type of data
     * @param path the filePath in the object the file is linked to
     * @param idx the index of the file
     * @param obj a class implementing the FileListener interface to call onFileDeleted()
     */
    public static void deleteFile(infoType type, String path, final int idx, FileListener obj) {
        StorageReference ref = storage.getReference(getPath(user, path));

        ref.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if(task.isSuccessful()) {
                    ArrayList<Task<StorageMetadata>> metadataProcesses = new ArrayList<>();
                    List<StorageReference> refs = task.getResult().getItems();

                    //Queue both metadata
                    for(StorageReference fileRef: refs) {
                        Task<StorageMetadata> metadataProcess = fileRef.getMetadata();
                        metadataProcesses.add(metadataProcess);
                    }
                    //Wait for metadata to get file creation times
                    ArrayList<Long> times = new ArrayList<Long>();
                    Tasks.whenAllSuccess(metadataProcesses).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> objects) {
                            //store file creation times
                            for(Object o: objects) {
                                times.add(((StorageMetadata)o).getCreationTimeMillis());
                            }

                            sortByAge(refs, times);
                            StorageReference deleteRef = refs.get(idx);
                            if(obj == null) {
                                deleteRef.delete();
                                return;
                            }
                            deleteRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                        obj.onFileDeleted(task.isSuccessful());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            obj.onFilesReceived(type, null);
                        }
                    });
                }
                if(obj == null) {
                    return;
                }
                obj.onFileDeleted(false);
            }
        });
    }

    /**
     * Deletes a specific file associated with a piece of data
     * @param type the type of data
     * @param path the filePath in the object the file is linked to
     * @param idx the index of the file
     */
    public static void deleteFile(infoType type, String path, final int idx) {
        deleteFile(type, path, idx, null);
    }
}
