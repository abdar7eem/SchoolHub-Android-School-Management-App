
package com.example.schoolhub.Teacher.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolhub.Model.Submission;
import com.example.schoolhub.R;

import java.util.List;

public class TeacherSubmissionAdapter extends RecyclerView.Adapter<TeacherSubmissionAdapter.ViewHolder> {


    private final Context context;
    private final List<Submission> submissionList;

    public TeacherSubmissionAdapter(Context context, List<Submission> submissionList) {
        this.context = context;
        this.submissionList = submissionList;
    }

    @NonNull
    @Override
    public TeacherSubmissionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.teacher_view_submissions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherSubmissionAdapter.ViewHolder holder, int position) {
        Submission submission = submissionList.get(position);

        holder.tvName.setText(submission.getStudentName());
        holder.tvSubject.setText(submission.getSubjectName());
        holder.tvDate.setText(submission.getSubmissionDate());
        holder.tvFile.setText(submission.getFileUrl());

        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(submission.getFileUrl()));
            context.startActivity(intent);
        });

        holder.btnGrade.setOnClickListener(v -> {
            // Future enhancement: show grade entry dialog
        });
    }

    @Override
    public int getItemCount() {
        return submissionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSubject, tvDate, tvFile;
        Button btnView, btnGrade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvDate = itemView.findViewById(R.id.tvSubmissionDate);
            tvFile = itemView.findViewById(R.id.tvFileName);
            btnView = itemView.findViewById(R.id.btnViewFile);
            btnGrade = itemView.findViewById(R.id.btnEnterGrade);
        }
    }
}