package com.example.b07_project21.ui.emergency_info;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.b07_project21.R;

import java.io.File;

public class EmergencyListHelper {
    /** Construct a view to go inside the list
     * @param inflater layout inflater
     * @param root root view (the list)
     * @param text up to three lines of text to display (first line is bold)
     * @param onEdit what to do when edit button is clicked
     * @param onDelete what to do when delete button is clicked
     */
    public static View construct(LayoutInflater inflater, android.view.ViewGroup root, GeneralInfo text, Runnable onEdit, Runnable onDelete, Runnable onDownload, Runnable onOpen) {
        View view = inflater.inflate(R.layout.fragment_emergency_list_item, root, false);
        TextView info = view.findViewById(R.id.emergencyInfo);
        ImageButton edit = view.findViewById(R.id.editButton);
        ImageButton open = view.findViewById(R.id.openButton);
        ImageButton download = view.findViewById(R.id.downloadButton);
        ImageButton remove = view.findViewById(R.id.deleteButton);

        SpannableStringBuilder textBuilder = new SpannableStringBuilder();
        if (text.first() != null) {
            textBuilder.append(text.first());
            textBuilder.setSpan(new AbsoluteSizeSpan(24, true), 0, text.first().length(), 0);
            textBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, text.first().length(), 0);
        }
        if (text.second() != null) textBuilder.append("\n").append(text.second());
        if (text.third() != null) textBuilder.append("\n").append(text.third());

        info.setText(textBuilder);

        remove.setOnClickListener(v -> onDelete.run());

        if (onEdit == null) edit.setVisibility(View.GONE);
        else edit.setOnClickListener(v -> onEdit.run());

        if (onDownload == null) download.setVisibility(View.GONE);
        else download.setOnClickListener(v -> onDownload.run());

        if (onOpen == null) open.setVisibility(View.GONE);
        else open.setOnClickListener(v -> onOpen.run());

        return view;
    }

    public static View construct(LayoutInflater inflater, android.view.ViewGroup root, GeneralInfo text, Runnable onEdit, Runnable onDelete) {
        return construct(inflater, root, text, onEdit, onDelete, null, null);
    }
}
