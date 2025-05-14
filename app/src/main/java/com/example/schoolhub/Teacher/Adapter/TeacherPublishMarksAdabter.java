package com.example.schoolhub.Teacher.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolhub.Model.StudentGrade;
import com.example.schoolhub.R;

import java.util.List;

public class TeacherPublishMarksAdabter extends RecyclerView.Adapter<TeacherPublishMarksAdabter.StudentViewHolder> {

    Context context;
    List<StudentGrade> studentList;

    public TeacherPublishMarksAdabter(Context context, List<StudentGrade> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_teacher_publish_marks_item, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentGrade student = studentList.get(position);
        holder.txtStudentName.setText(student.getName());
        holder.btnEnterGrade.setText(student.getGrade() >= 0 ? String.valueOf(student.getGrade()) : "Enter Grade");

        holder.btnEnterGrade.setOnClickListener(v -> {
            EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.setHint("Enter grade");

            new AlertDialog.Builder(context)
                    .setTitle("Enter Grade for " + student.getName())
                    .setView(input)
                    .setPositiveButton("OK", (dialog, which) -> {
                        try {
                            double grade = Double.parseDouble(input.getText().toString());
                            student.setGrade(grade);
                            notifyItemChanged(holder.getAdapterPosition());
                        } catch (NumberFormatException e) {
                            Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView txtStudentName;
        Button btnEnterGrade;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtStudentName = itemView.findViewById(R.id.text_student_name);
            btnEnterGrade = itemView.findViewById(R.id.button_enter_grade);
        }
    }
}
