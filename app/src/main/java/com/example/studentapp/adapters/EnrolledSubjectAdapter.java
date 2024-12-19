package com.example.studentapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.example.studentapp.R;
import com.example.studentapp.models.Subject;
import java.util.List;

public class EnrolledSubjectAdapter extends RecyclerView.Adapter<EnrolledSubjectAdapter.ViewHolder> {
    private List<Subject> subjects;

    public EnrolledSubjectAdapter(List<Subject> subjects) {
        this.subjects = subjects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_enrolled_subject, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.bind(subject);
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public void updateSubjects(List<Subject> newSubjects) {
        this.subjects = newSubjects;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView subjectNameText;
        private final TextView subjectIdText;
        private final TextView creditsText;
        private final Chip categoryChip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectNameText = itemView.findViewById(R.id.subjectNameText);
            subjectIdText = itemView.findViewById(R.id.subjectIdText);
            creditsText = itemView.findViewById(R.id.creditsText);
            categoryChip = itemView.findViewById(R.id.categoryChip);
        }

        public void bind(Subject subject) {
            subjectNameText.setText(subject.getName());
            subjectIdText.setText(subject.getId());
            creditsText.setText(String.format("%d Credits", subject.getCredits()));
            categoryChip.setText(subject.getCategory());
        }
    }
}
