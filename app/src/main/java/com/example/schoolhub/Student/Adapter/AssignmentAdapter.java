package com.example.schoolhub.Student.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.schoolhub.MainActivity;
import com.example.schoolhub.Model.Assignment;
import com.example.schoolhub.R;

import java.util.List;

public class AssignmentAdapter extends ArrayAdapter<Assignment> {

    private final Context context;
    private final List<Assignment> assignmentList;
    private int selectedPosition = -1;

    private final String[] backgroundColors = {
            "#AEFFA4", "#FBFF85", "#EB6C6E", "#98E3FF", "#FFB3D1",
            "#C3F5D9", "#FFE29A", "#D6C7FF", "#AED9FF", "#FFD6A5"
    };

    public AssignmentAdapter(Context context, List<Assignment> assignmentList) {
        super(context, 0, assignmentList);
        this.context = context;
        this.assignmentList = assignmentList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_student_assignment, parent, false);
        }

        Assignment assignment = assignmentList.get(position);

        int colorIndex = position % backgroundColors.length;
        int color = Color.parseColor(backgroundColors[colorIndex]);
        view.setBackgroundColor(color);

        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtSubject = view.findViewById(R.id.txtSubject);
        TextView txtTeacher = view.findViewById(R.id.txtTeacher);
        TextView txtDue = view.findViewById(R.id.txtDue);
        TextView txtStatus = view.findViewById(R.id.txtStatus);
        LinearLayout expandableLayout = view.findViewById(R.id.expandableLayout);
        Button downloadButton = view.findViewById(R.id.btnDownload);
        Button submitButton = view.findViewById(R.id.btnSubmit);

        txtTitle.setText("Title: " + assignment.getTitle());
        txtSubject.setText("Subject: " + assignment.getSubjectName());
        txtTeacher.setText("Teacher: " + assignment.getTeacherName());
        txtDue.setText("Due: " + assignment.getDueDate());
        txtStatus.setText("Status: " + assignment.getStatus());

        downloadButton.setOnClickListener(v -> {
            String rawPath = assignment.getAttachmentPath();
            if (rawPath != null && !rawPath.trim().isEmpty()) {
                String fullUrl = rawPath.startsWith("http://") || rawPath.startsWith("https://")
                        ? rawPath
                        : MainActivity.baseUrl+"uploads/" + rawPath;

                Log.d("DownloadButton", "Opening: " + fullUrl);
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl));
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(context, "Error opening file", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "No file available", Toast.LENGTH_SHORT).show();
            }
        });

        submitButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            ((Activity) context).startActivityForResult(Intent.createChooser(intent, "Choose file"),
                    1000 + assignment.getId());
        });

        expandableLayout.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);
        view.setOnClickListener(v -> {
            selectedPosition = (selectedPosition == position) ? -1 : position;
            notifyDataSetChanged();
        });

        return view;
    }
}
