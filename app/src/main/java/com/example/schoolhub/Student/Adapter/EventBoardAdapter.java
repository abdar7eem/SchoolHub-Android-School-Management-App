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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.EventBoardItem;
import com.example.schoolhub.Model.NotificationHelper;
import com.example.schoolhub.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventBoardAdapter extends ArrayAdapter<EventBoardItem> {

    private final int studentId;
    private final Set<Integer> alreadyChecked = new HashSet<>();
    private final Set<Integer> confirmedEventIds = new HashSet<>();

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
        btn.setEnabled(true);
        btn.setText("Checking...");

        // Avoid rechecking if already verified
        if (alreadyChecked.contains(event.id)) {
            if (confirmedEventIds.contains(event.id)) {
                btn.setEnabled(false);
                btn.setText("Added");
            } else {
                enableAddButton(btn, event);
            }
        } else {
            // Check from server if this student already added this event
            String checkUrl = "http://192.168.2.30/SchoolHub/check_event_confirmation.php?event_id=" + event.id + "&student_id=" + studentId;

            JsonArrayRequest checkRequest = new JsonArrayRequest(Request.Method.GET, checkUrl, null,
                    response -> {
                        alreadyChecked.add(event.id);

                        if (response.length() > 0) {
                            confirmedEventIds.add(event.id);
                            btn.setEnabled(false);
                            btn.setText("Added");
                        } else {
                            enableAddButton(btn, event);
                        }
                    },
                    error -> {
                        btn.setEnabled(true);
                        btn.setText("Add to Calendar");
                        Toast.makeText(getContext(), "Check failed", Toast.LENGTH_SHORT).show();
                    });

            Volley.newRequestQueue(getContext()).add(checkRequest);
        }

        return convertView;
    }

    private void enableAddButton(Button btn, EventBoardItem event) {
        btn.setEnabled(true);
        btn.setText("Add to Calendar");

        btn.setOnClickListener(v -> {
            String url = "http://192.168.2.30/SchoolHub/calendar_event_confirmations.php" +
                    "?event_id=" + event.id + "&student_id=" + studentId;

            StringRequest request = new StringRequest(Request.Method.GET, url,
                    response -> {
                        NotificationHelper.sendNotification(
                                getContext(),
                                "Event Added",
                                "You have successfully added " + event.title + " to your calendar.",
                                studentId,
                                2
                        );

                        Toast.makeText(getContext(), "Event added to calendar!", Toast.LENGTH_SHORT).show();
                        btn.setEnabled(false);
                        btn.setText("Added");

                        confirmedEventIds.add(event.id);
                        alreadyChecked.add(event.id);
                    },
                    error -> Toast.makeText(getContext(), "Failed to add event", Toast.LENGTH_SHORT).show()
            );

            Volley.newRequestQueue(getContext()).add(request);
        });
    }
}
