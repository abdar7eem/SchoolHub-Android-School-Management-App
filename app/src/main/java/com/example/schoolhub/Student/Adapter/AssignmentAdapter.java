package com.example.schoolhub.Student.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;

import com.example.schoolhub.Model.Assignment;
import com.example.schoolhub.R;

import java.util.List;

public class AssignmentAdapter extends ArrayAdapter<Assignment> {

    private final Context context;
    private final List<Assignment> assignmentList;
    private final FileSelectCallback callback;

    private final String baseUrl = "http://192.168.3.246/SchoolHub/";
    private final String[] backgroundColors = {
            "#AEFFA4", "#FBFF85", "#EB6C6E", "#98E3FF", "#FFB3D1",
            "#C3F5D9", "#FFE29A", "#D6C7FF", "#AED9FF", "#FFD6A5"
    };

    // Callback interface for submission button
    public interface FileSelectCallback {
        void onSubmitClick(int assignmentId, Button button);
    }

    // Constructor
    public AssignmentAdapter(Context context, List<Assignment> assignmentList, FileSelectCallback callback) {
        super(context, 0, assignmentList);
        this.context = context;
        this.assignmentList = assignmentList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_student_assignment, parent, false);
        }

        Assignment assignment = assignmentList.get(position);

        // Set background color for visual variety
        int colorIndex = position % backgroundColors.length;
        int color = Color.parseColor(backgroundColors[colorIndex]);
        view.setBackgroundColor(color);

        // UI elements
        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtSubject = view.findViewById(R.id.txtSubject);
        TextView txtTeacher = view.findViewById(R.id.txtTeacher);
        TextView txtDue = view.findViewById(R.id.txtDue);
        TextView txtStatus = view.findViewById(R.id.txtStatus);
        LinearLayout expandableLayout = view.findViewById(R.id.expandableLayout);
        Button btnDownload = view.findViewById(R.id.btnDownload);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);

        // Populate fields
        txtTitle.setText("Title: " + assignment.getTitle());
        txtSubject.setText("Subject: " + assignment.getSubjectName());
        txtTeacher.setText("Teacher: " + assignment.getTeacherName());
        txtDue.setText("Due: " + assignment.getDueDate());
        txtStatus.setText("Status: " + assignment.getStatus());

        // File download logic
        btnDownload.setOnClickListener(v -> {
            String path = assignment.getAttachmentPath();
            if (path != null && !path.trim().isEmpty()) {
                String fullUrl = path.startsWith("http") ? path : baseUrl + path;
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Error opening file", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No file available", Toast.LENGTH_SHORT).show();
            }
        });

        // Submit button click
        btnSubmit.setOnClickListener(v -> {
            Log.d("AssignmentAdapter", "Submitting assignment ID = " + assignment.getId());
            if (callback != null) {
                callback.onSubmitClick(assignment.getId(), btnSubmit);
            }
        });

        // Expand/collapse assignment card
        expandableLayout.setVisibility(View.GONE);
        view.setOnClickListener(v -> {
            expandableLayout.setVisibility(
                    expandableLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE
            );
        });

        return view;
    }
}
