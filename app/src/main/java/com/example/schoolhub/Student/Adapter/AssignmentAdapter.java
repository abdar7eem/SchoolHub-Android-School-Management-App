package com.example.schoolhub.Student.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.schoolhub.Model.Assignment;
import com.example.schoolhub.R;

import java.util.List;

public class AssignmentAdapter extends ArrayAdapter<Assignment> {

    private final Context context;
    private final List<Assignment> assignmentList;
    private int selectedPosition = -1; // Track expanded item

    // Hex color palette for card backgrounds
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

        // Set dynamic background color
        int colorIndex = position % backgroundColors.length;
        int color = Color.parseColor(backgroundColors[colorIndex]);
        view.setBackgroundColor(color);

        // Bind data to views
        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtSubject = view.findViewById(R.id.txtSubject);
        TextView txtTeacher = view.findViewById(R.id.txtTeacher);
        TextView txtDue = view.findViewById(R.id.txtDue);
        TextView txtStatus = view.findViewById(R.id.txtStatus);
        LinearLayout expandableLayout = view.findViewById(R.id.expandableLayout);

        txtTitle.setText("Title: " + assignment.getTitle());
        txtSubject.setText("Subject: " + assignment.getSubjectName());
        txtTeacher.setText("Teacher: " + assignment.getTeacherName());
        txtDue.setText("Due: " + assignment.getDueDate());
        txtStatus.setText("Status: " + assignment.getStatus());

        // Handle expand/collapse
        if (position == selectedPosition) {
            expandableLayout.setVisibility(View.VISIBLE);
        } else {
            expandableLayout.setVisibility(View.GONE);
        }

        view.setOnClickListener(v -> {
            selectedPosition = (selectedPosition == position) ? -1 : position;
            notifyDataSetChanged();
        });

        return view;
    }
}
