package com.example.schoolhub.Teacher.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolhub.Model.Student;
import com.example.schoolhub.R;

import java.util.List;

public class TeacherAttendanceAdapter extends RecyclerView.Adapter<TeacherAttendanceAdapter.AttendanceViewHolder> {

    private List<Student> studentList;

    public TeacherAttendanceAdapter(List<Student> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        Student student = studentList.get(position);

        holder.tvStudentName.setText(student.getName());

        // Restore the previous selected status if exists
        switch (student.getStatus()) {
            case "Present":
                holder.rbPresent.setChecked(true);
                break;
            case "Absent":
                holder.rbAbsent.setChecked(true);
                break;
            case "Late":
                holder.rbLate.setChecked(true);
                break;
        }

        // Listen for radio group changes
        holder.rgAttendanceOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == holder.rbPresent.getId()) {
                student.setStatus("Present");
            } else if (checkedId == holder.rbAbsent.getId()) {
                student.setStatus("Absent");
            } else if (checkedId == holder.rbLate.getId()) {
                student.setStatus("Late");
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName;
        RadioGroup rgAttendanceOptions;
        RadioButton rbPresent, rbAbsent, rbLate;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            rgAttendanceOptions = itemView.findViewById(R.id.rgAttendanceOptions);
            rbPresent = itemView.findViewById(R.id.rbPresent);
            rbAbsent = itemView.findViewById(R.id.rbAbsent);
            rbLate = itemView.findViewById(R.id.rbLate);
        }
    }
}
