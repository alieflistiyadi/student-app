package com.example.studentapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.example.studentapp.R;
import com.example.studentapp.models.Subject;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {
    private List<Subject> subjects;
    private OnSubjectCheckListener checkListener;

    public interface OnSubjectCheckListener {
        boolean onSubjectChecked(Subject subject, boolean isChecked);
    }

    public SubjectAdapter(List<Subject> subjects, OnSubjectCheckListener listener) {
        this.subjects = subjects;
        this.checkListener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.bind(subject);
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    class SubjectViewHolder extends RecyclerView.ViewHolder {
        private TextView subjectNameText;
        private TextView creditsText;
        private Chip categoryChip;
        private CheckBox selectCheckBox;

        SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectNameText = itemView.findViewById(R.id.subjectNameText);
            creditsText = itemView.findViewById(R.id.creditsText);
            categoryChip = itemView.findViewById(R.id.categoryChip);
            selectCheckBox = itemView.findViewById(R.id.selectCheckBox);
        }

        void bind(Subject subject) {
            subjectNameText.setText(subject.getName());
            creditsText.setText(String.format("%d Credits", subject.getCredits()));
            categoryChip.setText(subject.getCategory());

            if (checkListener != null) {
                selectCheckBox.setVisibility(View.VISIBLE);
                selectCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (!checkListener.onSubjectChecked(subject, isChecked)) {
                        buttonView.setChecked(false);
                    }
                });
            } else {
                selectCheckBox.setVisibility(View.GONE);
            }
        }
    }
}
