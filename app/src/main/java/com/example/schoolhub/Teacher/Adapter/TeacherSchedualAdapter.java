package com.example.schoolhub.Teacher.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolhub.Model.Schedule;
import com.example.schoolhub.R;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TeacherSchedualAdapter extends ArrayAdapter<Schedule> {


    private final Context context;
    private final List<Schedule> scheduleList;

    public TeacherSchedualAdapter(Context context, List<Schedule> scheduleList) {
        super(context, 0, scheduleList);
        this.context = context;
        this.scheduleList = scheduleList;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Schedule schedule = scheduleList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.teacher_schedual_item, parent, false);
        }

        TextView tvSubject = convertView.findViewById(R.id.tvSubject);
        TextView tvRoom = convertView.findViewById(R.id.tvRoom);
        TextView tvTime = convertView.findViewById(R.id.tvTime);
        TextView tvDay = convertView.findViewById(R.id.tvDay);

        tvSubject.setText(schedule.getSubjectName());
        tvRoom.setText("Room: " + schedule.getRoom());
        tvDay.setText(schedule.getDayOfWeek());

        if (schedule.getStartTime() != null && schedule.getEndTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            String time = schedule.getStartTime().format(formatter) + " - " + schedule.getEndTime().format(formatter);
            tvTime.setText(time);
        } else {
            tvTime.setText("Time: -");
        }

        return convertView;
    }
}