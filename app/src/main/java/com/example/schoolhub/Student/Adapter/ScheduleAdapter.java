package com.example.schoolhub.Student.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.schoolhub.Model.Schedule;
import com.example.schoolhub.R;

import java.util.List;

public class ScheduleAdapter extends BaseAdapter {
    private Context context;
    private List<Schedule> scheduleList;

    public ScheduleAdapter(Context context, List<Schedule> scheduleList) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Schedule schedule = scheduleList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_student_schedule, parent, false);
        }

        TextView subject = convertView.findViewById(R.id.txtSubjectName);
        TextView time = convertView.findViewById(R.id.txtClassTime);
        TextView instructor = convertView.findViewById(R.id.txtInstructorName);

        subject.setText(schedule.getSubjectName());
        time.setText(schedule.getStartTime()) ;

        instructor.setText(schedule.getInstructorName());

        return convertView;
    }


}