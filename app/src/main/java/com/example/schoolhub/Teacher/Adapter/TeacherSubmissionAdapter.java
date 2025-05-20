package com.example.schoolhub.Teacher.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolhub.Model.Submission;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;

import java.util.List;

public class TeacherSubmissionAdapter extends RecyclerView.Adapter<TeacherSubmissionAdapter.ViewHolder> {

    private final Context context;
    private final List<Submission> submissionList;
    private final String baseUrl = LoginActivity.baseUrl; // Adjust to your actual base URL

    public TeacherSubmissionAdapter(Context context, List<Submission> submissionList) {
        this.context = context;
        this.submissionList = submissionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.teacher_view_submissions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Submission submission = submissionList.get(position);

        holder.tvName.setText(submission.getStudentName());
        holder.tvSubject.setText(submission.getSubjectName());
        holder.tvSubmissionDate.setText(submission.getSubmissionDate());

        String fileUrl = submission.getFileUrl();
        if (fileUrl != null && !fileUrl.isEmpty()) {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            holder.tvFileName.setText(fileName);
        } else {
            holder.tvFileName.setText("No file");
        }

        holder.btnViewFile.setOnClickListener(v -> {
            if (fileUrl != null && !fileUrl.isEmpty()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(fileUrl.startsWith("http") ? fileUrl : baseUrl + fileUrl), "*/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "No app found to open this file.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No file to view.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return submissionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSubject, tvSubmissionDate, tvFileName;
        Button btnViewFile, btnEnterGrade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvSubmissionDate = itemView.findViewById(R.id.tvSubmissionDate);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            btnViewFile = itemView.findViewById(R.id.btnViewFile);
        }
    }
}
