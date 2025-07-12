package dataAccess;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

class DataTree {
    HashMap<String, String> answers;
    ArrayList<Document> documents;
    ArrayList<EmergencyContact> contacts;
    ArrayList<SafeLocation> locations;
    ArrayList<Medication> medications;

    public DataTree(DataSnapshot snapshot) {
        answers = (HashMap<String, String>) snapshot.child("answers").getValue(DatabaseAccess.getGenericTypeIndicator(infoType.ANSWER));
        documents = (ArrayList<Document>) snapshot.child("documents").getValue(DatabaseAccess.getGenericTypeIndicator(infoType.DOCUMENT));
        contacts = (ArrayList<EmergencyContact>) snapshot.child("emergency_contacts").getValue(DatabaseAccess.getGenericTypeIndicator(infoType.CONTACT));
        locations = (ArrayList<SafeLocation>) snapshot.child("safe_locations").getValue(DatabaseAccess.getGenericTypeIndicator(infoType.LOCATION));
        medications = (ArrayList<Medication>) snapshot.child("medications").getValue(DatabaseAccess.getGenericTypeIndicator(infoType.MEDICATION));
        initilizeObjects();
    }

    private void initilizeObjects() {
        if(answers == null) {
            answers = new HashMap<String, String>();
        }
        if(documents == null) {
            documents = new ArrayList<Document>();
        }
        if(contacts == null) {
            contacts = new ArrayList<EmergencyContact>();
        }
        if(locations == null) {
            locations = new ArrayList<SafeLocation>();
        }
        if(medications == null) {
            medications = new ArrayList<Medication>();
        }
    }

    /**
     * Gets data of a specific type stored
     * @param type the type of data to retrieve
     * @return an object that must be casted to the correct type
     */
    protected Object getData(infoType type) {
        switch(type) {
            case ANSWER:
                return answers;
            case CONTACT:
                return contacts;
            case DOCUMENT:
                return documents;
            case LOCATION:
                return locations;
            case MEDICATION:
                return medications;
        }
        return null;
    }

    /**
     * Gets the path to the files for a specific piece of data
     * @param type the type of data
     * @param key a key to get the specific piece of data
     *            For ANSWER, the key is a key for the Map
     *            Otherwise, the key is String.valueOf(Index)
     * @return the path to the files for a specific piece of data
     *         null if type == ANSWER
     */
    protected String getFilePath(infoType type, String key) {
        if(type == infoType.ANSWER) {
            return null;
        }
        UserData obj = (UserData) ((ArrayList<?>)getData(type)).get(0);
        return obj.getFilePath();
    }

    /**
     * Adds data of a specific type to the dataTree
     * @param type the type of data
     * @param data the data to add
     * Note that this does not check that the data is of a valid type
     * It is your responsibility to verify the data is of a valid type
     */
    protected void addData(infoType type, Object data) {
        switch (type) {
            case ANSWER:
                Pair<String> res = (Pair<String>) data;
                answers.put(res.x, res.y);
                return;
            case CONTACT:
                contacts.add((EmergencyContact) data);
                return;
            case DOCUMENT:
                documents.add((Document) data);
                return;
            case LOCATION:
                locations.add((SafeLocation) data);
                return;
            case MEDICATION:
                medications.add((Medication) data);
                return;
        }
    }

    /**
     * Adds data of a specific type to the dataTree
     * @param type the type of data
     * @param key a key to get the specific piece of data
     *            For ANSWER, the key is a key for the Map
     *            Otherwise, the key is String.valueOf(Index)
     * @param data the data to replace the old data
     * Note that this does not check that the data is of a valid type
     * It is your responsibility to verify the data is of a valid type
     */
    protected void updateData(infoType type, String key, Object data) {
        if(data == null) {
            deleteData(type, key);
            return;
        }

        switch(type) {
            case ANSWER:
                answers.put(key, (String) data);
                return;
            case CONTACT:
                contacts.set(Integer.parseInt(key), (EmergencyContact) data);
                return;
            case DOCUMENT:
                documents.set(Integer.parseInt(key), (Document) data);
                return;
            case LOCATION:
                locations.set(Integer.parseInt(key), (SafeLocation) data);
                return;
            case MEDICATION:
                medications.set(Integer.parseInt(key), (Medication) data);
                return;
        }
    }

    /**
     * Removes data of a specific type from the tree
     * @param type the type of data
     * @param key a key to get the specific piece of data
     *            For ANSWER, the key is a key for the Map
     *            Otherwise, the key is String.valueOf(Index)
     */
    protected void deleteData(infoType type, String key) {
        switch(type) {
            case ANSWER:
                answers.remove(key);
                return;
            case CONTACT:
                contacts.remove(Integer.parseInt(key));
                return;
            case DOCUMENT:
                documents.remove(Integer.parseInt(key));
                return;
            case LOCATION:
                locations.remove(Integer.parseInt(key));
                return;
            case MEDICATION:
                medications.remove(Integer.parseInt(key));
                return;
        }
    }
}
