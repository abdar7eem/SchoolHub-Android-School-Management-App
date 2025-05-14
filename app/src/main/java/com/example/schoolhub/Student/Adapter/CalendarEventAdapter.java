package com.example.schoolhub.Student.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.schoolhub.Model.CalendarEvent;
import com.example.schoolhub.R;

import java.util.List;

public class CalendarEventAdapter extends ArrayAdapter<CalendarEvent> {
    private Context context;
    private List<CalendarEvent> eventList;

    public CalendarEventAdapter(Context context, List<CalendarEvent> list) {
        super(context, 0, list);
        this.context = context;
        this.eventList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CalendarEvent event = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_student_calendar, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.txtEventType)).setText(event.type);
        ((TextView) convertView.findViewById(R.id.txtEventSubject)).setText(event.subject);
        ((TextView) convertView.findViewById(R.id.txtEventTitle)).setText(event.title);
        ((TextView) convertView.findViewById(R.id.txtEventTime)).setText(event.time);

        TextView locationView = convertView.findViewById(R.id.txtEventLocation);
        if (event.location != null && !event.location.isEmpty()) {
            locationView.setVisibility(View.VISIBLE);
            locationView.setText("Location: " + event.location);
        } else {
            locationView.setVisibility(View.GONE);
        }

        return convertView;
    }
}
