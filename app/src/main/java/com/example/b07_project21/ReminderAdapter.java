package com.example.b07_project21;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class ReminderAdapter extends ListAdapter<Reminder, ReminderAdapter.VH> {
    private final List<Reminder> items = new ArrayList<>();
    public ReminderAdapter() {
        super(new DiffUtil.ItemCallback<Reminder>() {
            @Override public boolean areItemsTheSame(@NonNull Reminder a, @NonNull Reminder b) {
                return a.getId().equals(b.getId());
            }
            @Override public boolean areContentsTheSame(@NonNull Reminder a, @NonNull Reminder b) {
                return a.getTriggerAt() == b.getTriggerAt()
                        && a.getFrequency() == b.getFrequency();
            }
        });
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Reminder r = getItem(pos);
        h.text.setText(
                DateFormat.getDateTimeInstance()
                        .format(new Date(r.getTriggerAt()))
        );
    }

    /** Convenience for adding one new item on the fly. */
    public void addItem(Reminder r) {
        // take a copy of whatever’s currently in the list…
        List<Reminder> copy = new ArrayList<>(getCurrentList());
        // add the new one…
        copy.add(r);
        // and hand it back to the ListAdapter
        submitList(copy);
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView text;
        VH(View v) {
            super(v);
            text = v.findViewById(R.id.text_timestamp);
        }
    }
}
