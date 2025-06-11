package com.example.schoolhub.Teacher.Adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.schoolhub.Model.Schedule;
import com.example.schoolhub.R;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TeacherSchedualAdapter extends BaseAdapter {

    private final Context context;
    private final List<Schedule> scheduleList;

    public TeacherSchedualAdapter(Context context, List<Schedule> scheduleList) {
        this.context = context;
        this.scheduleList = scheduleList;
    }

    @Override
    public int getCount() {
        return scheduleList.size();
    }

    @Override
    public Object getItem(int position) {
        return scheduleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
        TextView tvClass = convertView.findViewById(R.id.tvClass);

        tvSubject.setText(schedule.getSubjectName());
        tvRoom.setText("Room: " + schedule.getRoom());
        tvDay.setText(schedule.getDayOfWeek());
        tvClass.setText(schedule.getClassName());

        if (schedule.getStartTime() != null && !schedule.getStartTime().isEmpty()) {
            try {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalTime startTime = LocalTime.parse(schedule.getStartTime(), inputFormatter);

                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("hh:mm a");
                String formattedStart = startTime.format(outputFormatter);
                tvTime.setText(formattedStart);

            } catch (Exception e) {
                tvTime.setText("Invalid Time");
                e.printStackTrace();
            }
        } else {
            tvTime.setText("Time: -");
        }

        return convertView;
    }
}
