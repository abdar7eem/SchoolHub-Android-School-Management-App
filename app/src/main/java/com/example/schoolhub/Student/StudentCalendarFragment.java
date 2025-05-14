package com.example.schoolhub.Student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.CalendarEvent;
import com.example.schoolhub.R;
import com.example.schoolhub.Student.Adapter.CalendarEventAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentCalendarFragment extends Fragment {

    private ListView lstBooks;
    private CalendarEventAdapter adapter;
    private List<CalendarEvent> eventList;

    private int classId = 1, userId=1; // Replace with actual class_id for the logged-in student

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_calendar, container, false);

        lstBooks = view.findViewById(R.id.lstBooks);
        eventList = new ArrayList<>();
        adapter = new CalendarEventAdapter(getContext(), eventList);
        lstBooks.setAdapter(adapter);

        fetchEvents();

        return view;
    }

    private void fetchEvents() {
        String url = "http://192.168.1.13/SchoolHub/get_calendar_events.php?class_id=" + classId + "&student_id=" + userId;


        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    eventList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            String type = capitalize(obj.getString("event_type"));
                            String subject = obj.isNull("subject_name") ? "Subject: -" : "Subject: " + obj.getString("subject_name");
                            String title = "Title: " + obj.getString("event_title");
                            String date = obj.getString("event_date");
                            String time = "Date: " + date;
                            int eventId = obj.getInt("id");

                            if (!obj.isNull("event_start_time")) {
                                time += " | Time: " + obj.getString("event_start_time");
                            }

                            String location = obj.optString("location", "");



                            eventList.add(new CalendarEvent(eventId, type, subject, title, time, location));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle errors
                });

        Volley.newRequestQueue(getContext()).add(request);
    }

    private String capitalize(String input) {
        return input.substring(0,1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
