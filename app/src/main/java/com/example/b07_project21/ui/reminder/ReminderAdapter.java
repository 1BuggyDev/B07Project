package com.example.b07_project21.ui.reminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07_project21.R;

import java.text.DateFormat;

public class ReminderAdapter extends ListAdapter<Reminder, ReminderAdapter.VH> {

    /** Callback for edit/delete actions from the fragment */
    public interface OnItemActionListener {
        /** user long-pressed to edit this reminder */
        void onEdit(Reminder r);
        /** user swiped or chose to delete this reminder */
        void onDelete(Reminder r);
    }

    private OnItemActionListener actionListener;
    public void setOnItemActionListener(OnItemActionListener l) {
        this.actionListener = l;
    }

    public ReminderAdapter() {
        super(new DiffUtil.ItemCallback<Reminder>() {
            @Override public boolean areItemsTheSame(@NonNull Reminder a, @NonNull Reminder b) {
                return a.getId().equals(b.getId());
            }
            @Override public boolean areContentsTheSame(@NonNull Reminder a, @NonNull Reminder b) {
                return a.getTriggerAt() == b.getTriggerAt()
                        && a.getFrequency().equals(b.getFrequency())
                        && a.getMessage().equals(b.getMessage());
            }
        });
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Reminder r = getItem(pos);
        // format the stored epoch millis into a human-readable string
        String label = DateFormat
                .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                .format(r.getTriggerAt());
        h.text.setText(label);
        String m = r.getMessage();
        if (m == null || m.isEmpty()) {
            h.msg.setVisibility(View.GONE);
        } else {
            h.msg.setText(m);
            h.msg.setVisibility(View.VISIBLE);
        }
        // long-press to edit:
        h.itemView.setOnLongClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEdit(r);
            }
            return true;
        });
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView text, msg;
        VH(View v) {
            super(v);
            text = v.findViewById(R.id.text_timestamp);
            msg = v.findViewById(R.id.text_msg);
        }
    }

    /** convenience for fragment swipe/delete */
    public Reminder getItemAt(int pos) {
        return getItem(pos);
    }
}