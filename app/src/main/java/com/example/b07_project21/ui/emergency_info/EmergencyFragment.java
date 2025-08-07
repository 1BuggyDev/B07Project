package com.example.b07_project21.ui.emergency_info;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.b07_project21.R;
import com.example.b07_project21.databinding.FragmentEmergencyBinding;

import java.io.File;
import java.io.IOException;

import dataAccess.DatabaseAccess;
import dataAccess.Document;
import dataAccess.EmergencyContact;
import dataAccess.FileData;
import dataAccess.Medication;
import dataAccess.SafeLocation;
import dataAccess.StorageAccess;
import dataAccess.infoType;

public class EmergencyFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentEmergencyBinding binding;
    private EmergencyDataManager dataManager;
    private EmergencyFileManager fileManager;
    private LayoutInflater inflater;
    private ActivityResultLauncher<Intent> filePickerLauncher;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        binding = FragmentEmergencyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        dataManager = new EmergencyDataManager(this);
        fileManager = new EmergencyFileManager(this);

        return root;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Spinner
        Spinner spinner = binding.emergencySpinner;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.emergency_info_list));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(this);

        // Add button listeners
        binding.buttonEmergencyAdd.setOnClickListener(v -> {
            infoType type = getSpinnerType(spinner.getSelectedItemPosition());
            switch (type) {
                case DOCUMENT:
                    askForDocument();
                    break;
                case CONTACT:
                    EmergencyDialogHelper.showDialog(this, infoType.CONTACT, "Add Contact", new GeneralInfo("Name", "Relationship", "Contact Method"), null);
                    break;
                case LOCATION:
                    EmergencyDialogHelper.showDialog(this, infoType.LOCATION, "Add Location", new GeneralInfo("Address"), null);
                    break;
                case MEDICATION:
                    EmergencyDialogHelper.showDialog(this, infoType.MEDICATION, "Add Medication", new GeneralInfo("Name", "Amount", "Frequency (Daily)"), null);
                    break;
            }
        });

        // Initialize file picker
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent fileIntent = result.getData();
                        Uri fileUri = fileIntent.getData();
                        String fileName = EmergencyFileHelper.getUriName(requireContext(), fileUri);
                        File file = EmergencyFileHelper.uriToFile(requireContext(), fileUri);

                        FileData fileData = null;
                        try {
                            fileData = new FileData(EmergencyFileHelper.fileToBytes(file), fileName, null);
                            fileData.setType(fileData.getExtension());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        // Ask for more info in dialog
                        EmergencyDialogHelper.showDialog(this, infoType.DOCUMENT, "Add Document", new GeneralInfo("Name", null, "Category"), null, fileData);
                    }
                });
    }

    /** Asks user for a file to upload. */
    public void askForDocument() {
        Intent filePicker = new Intent(Intent.ACTION_GET_CONTENT);
        filePicker.addCategory(Intent.CATEGORY_OPENABLE);
        filePicker.setType("*/*");
        filePickerLauncher.launch(filePicker);
    }

    private void setDescription(String desc) {
        binding.emergencyDescription.setText(desc);
    }

    private infoType getSpinnerType(int pos) {
        switch (pos) {
            case 1: return infoType.CONTACT;
            case 2: return infoType.LOCATION;
            case 3: return infoType.MEDICATION;
            default: return infoType.DOCUMENT;
        }
    }

    public void edit(infoType type, Object data) {
        GeneralInfo hints;
        switch (type) {
            case CONTACT:
                hints = new GeneralInfo("Name", "Relationship", "Contact Method");
                EmergencyDialogHelper.showDialog(this, infoType.CONTACT, "Edit Contact", hints, GeneralInfo.castFrom(type, data));
                break;
            case LOCATION:
                hints = new GeneralInfo("Address");
                EmergencyDialogHelper.showDialog(this, infoType.LOCATION, "Edit Location", hints, GeneralInfo.castFrom(type, data));
                break;
            case MEDICATION:
                hints = new GeneralInfo("Name", "Amount", "Frequency (Daily)");
                EmergencyDialogHelper.showDialog(this, infoType.MEDICATION, "Edit Medication", hints, GeneralInfo.castFrom(type, data));
                break;
            }
    }

    /** Deletes specified item from specified list */
    public void delete(infoType type, int index) {
        if (index < 0) return;
        // if (type == infoType.DOCUMENT) StorageAccess.deleteFile(infoType.DOCUMENT, dataManager.getDocuments().get(index).getFilePath(), index, fileManager);
        DatabaseAccess.deleteData(type, String.valueOf(index), dataManager);
    }

    public void delete(infoType type, Object obj) {
        for (int i = 0; i < dataManager.getData(type).size(); i++) { // different object, indexOf doesn't work
            if (GeneralInfo.castFrom(type, obj).isEqualTo(type, dataManager.getData(type).get(i))
                    || (type == infoType.DOCUMENT && dataManager.getData(type).get(i).equals(obj))) {
                delete(type, i);
                return;
            }
        }
    }

    /** Updates specified list with current data */
    public void updateList(infoType type) {
        binding.emergencyContents.removeAllViews();
        switch (type) {
            case DOCUMENT:
                setDescription(getResources().getString(R.string.document_desc));
                for (Document document : dataManager.getDocuments()) addToList(infoType.DOCUMENT, document);
                break;
            case CONTACT:
                setDescription(getResources().getString(R.string.contact_desc));
                for (EmergencyContact contact : dataManager.getContacts()) addToList(infoType.CONTACT, contact);
                break;
            case LOCATION:
                setDescription(getResources().getString(R.string.location_desc));
                for (SafeLocation location : dataManager.getLocations()) addToList(infoType.LOCATION, location);
                break;
            case MEDICATION:
                setDescription(getResources().getString(R.string.medication_desc));
                for (Medication medication : dataManager.getMedications()) addToList(infoType.MEDICATION, medication);
                break;
        }
    }

    /** Adds specified data to list (contact, location, medication) */
    public void addToList(infoType type, Object data) {
        LinearLayout list = binding.emergencyContents;
        View item = null;
        Runnable editButton;
        Runnable deleteButton;
        switch (type) {
            case DOCUMENT:
                Document document = (Document) data;
                deleteButton = ()-> delete(infoType.DOCUMENT, document);
                Runnable onDownload = ()-> {
                    fileManager.setCurrentFile(document.getTitle());
                    fileManager.setReadMode("download");
                    StorageAccess.readFiles(type, document.getFilePath(), fileManager);
                };
                Runnable onOpen = ()-> {
                    fileManager.setCurrentFile(document.getTitle());
                    fileManager.setReadMode("open");
                    StorageAccess.readFiles(type, document.getFilePath(), fileManager);
                };
                item = EmergencyListHelper.construct(inflater, list, GeneralInfo.castFrom(type, data), null, deleteButton, onDownload, onOpen);
                break;
            case CONTACT:
                EmergencyContact contact = (EmergencyContact) data;
                editButton = ()-> edit(infoType.CONTACT, contact);
                deleteButton = ()-> delete(infoType.CONTACT, contact);
                item = EmergencyListHelper.construct(inflater, list, GeneralInfo.castFrom(type, data), editButton, deleteButton);
                break;
            case LOCATION:
                SafeLocation location = (SafeLocation) data;
                editButton = ()-> edit(infoType.LOCATION, location);
                deleteButton = ()-> delete(infoType.LOCATION, location);
                item = EmergencyListHelper.construct(inflater, list, GeneralInfo.castFrom(type, data), editButton, deleteButton);
                break;
            case MEDICATION:
                Medication medication = (Medication) data;
                editButton = ()-> edit(infoType.MEDICATION, medication);
                deleteButton = ()-> delete(infoType.MEDICATION, medication);
                item = EmergencyListHelper.construct(inflater, list, GeneralInfo.castFrom(type, data), editButton, deleteButton);
                break;
        }
        if (item != null) list.addView(item);
    }

    public EmergencyDataManager getDataManager() {
        return dataManager;
    }

    public EmergencyFileManager getFileManager() {
        return fileManager;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        DatabaseAccess.readData(getSpinnerType(pos), dataManager);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}