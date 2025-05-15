package com.example.schoolhub.Student.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.EventBoardItem;
import com.example.schoolhub.Model.NotificationHelper;
import com.example.schoolhub.R;

import java.util.List;

public class EventBoardAdapter extends ArrayAdapter<EventBoardItem> {

    private int studentId;

    public EventBoardAdapter(Context context, List<EventBoardItem> list, int studentId) {
        super(context, 0, list);
        this.studentId = studentId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventBoardItem event = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_student_event, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.txtEventTitle)).setText(event.title);
        ((TextView) convertView.findViewById(R.id.txtEventDateTime)).setText(event.dateTime);
        ((TextView) convertView.findViewById(R.id.txtEventLocation)).setText("Location: " + event.location);
        ((TextView) convertView.findViewById(R.id.txtEventType)).setText("Type: " + event.type);
        ((TextView) convertView.findViewById(R.id.txtEventDescription)).setText(event.description);

        Button btn = convertView.findViewById(R.id.btnAddToCalendar);
        btn.setOnClickListener(v -> {
            String url = "http://192.168.1.13/SchoolHub/calendar_event_confirmations.php" +
                    "?event_id=" + event.id + "&student_id=" + studentId;

            StringRequest request = new StringRequest(Request.Method.GET, url,
                    response -> {

//                        NotificationHelper.sendNotification(
//                                getContext(),
//                                "Event Added",
//                                "You have successfully added "+event.title+" to your calendar.",
//                                studentId,
//                                2
//                        );


                        Toast.makeText(getContext(), "Event added to calendar!", Toast.LENGTH_SHORT).show();
                        btn.setEnabled(false); // Optional: disable to avoid re-adding
                        btn.setText("Added");
                    },
                    error -> Toast.makeText(getContext(), "Failed to add event", Toast.LENGTH_SHORT).show()
            );

            Volley.newRequestQueue(getContext()).add(request);
        });

        return convertView;
    }
}
