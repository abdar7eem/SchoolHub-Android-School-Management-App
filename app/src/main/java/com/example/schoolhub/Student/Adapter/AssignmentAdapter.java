package com.example.schoolhub.Student.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;

import com.example.schoolhub.Model.Assignment;
import com.example.schoolhub.R;

import java.util.List;

public class AssignmentAdapter extends ArrayAdapter<Assignment> {

    private Context context;
    private final String baseUrl = "http://192.168.3.246/SchoolHub/";

    public AssignmentAdapter(Context context, List<Assignment> assignmentList) {
        super(context, 0, assignmentList);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Assignment assignment = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_student_assignment, parent, false);
        }

        // Bind UI components
        TextView txtTitle = convertView.findViewById(R.id.txtTitle);
        TextView txtSubject = convertView.findViewById(R.id.txtSubject);
        TextView txtTeacher = convertView.findViewById(R.id.txtTeacher);
        TextView txtDue = convertView.findViewById(R.id.txtDue);
        TextView txtStatus = convertView.findViewById(R.id.txtStatus);
        Button btnDownload = convertView.findViewById(R.id.btnDownload);
        Button btnSubmit = convertView.findViewById(R.id.btnSubmit);
        LinearLayout expandableLayout = convertView.findViewById(R.id.expandableLayout);

        // Set assignment data
        txtTitle.setText("Title: " + assignment.getTitle());
        txtSubject.setText("Subject: " + assignment.getSubject());
        txtTeacher.setText("Teacher: " + assignment.getTeacher());
        txtDue.setText("Due: " + assignment.getDueDate());
        txtStatus.setText("Status: " + assignment.getStatus());

        // Toggle card on click
        convertView.setOnClickListener(v -> {
            if (expandableLayout.getVisibility() == View.VISIBLE) {
                expandableLayout.setVisibility(View.GONE);
            } else {
                expandableLayout.setVisibility(View.VISIBLE);
            }
        });

        btnDownload.setOnClickListener(v -> {
            String attachmentPath = assignment.getAttachment();
            if (attachmentPath != null && !attachmentPath.isEmpty()) {
                String fullUrl = baseUrl + attachmentPath;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl));
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "No file attached", Toast.LENGTH_SHORT).show();
            }
        });

        btnSubmit.setOnClickListener(v -> {
            Toast.makeText(context, "Submit button clicked", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}
