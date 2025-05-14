package com.example.schoolhub.Student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.EventBoardItem;
import com.example.schoolhub.R;
import com.example.schoolhub.Student.Adapter.EventBoardAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentEventFragment extends Fragment {

    private ListView lstBooks;
    private EventBoardAdapter adapter;
    private List<EventBoardItem> eventList;

    private final int studentId = 1; // TODO: Replace with actual logged-in student ID

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_events, container, false);

        lstBooks = view.findViewById(R.id.lstBooks);
        eventList = new ArrayList<>();
        adapter = new EventBoardAdapter(requireContext(), eventList, studentId);
        lstBooks.setAdapter(adapter);

        fetchEvents();

        return view;
    }

    private void fetchEvents() {
        String url = "http://192.168.1.13/SchoolHub/get_event_board.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    eventList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            int id = obj.getInt("id");
                            String title = obj.getString("event_title");

                            String date = obj.getString("event_date");
                            String time = obj.optString("event_start_time", "");
                            String dateTime = time.isEmpty() ? date : date + " at " + time;

                            String location = obj.optString("location", "N/A");
                            String type = capitalize(obj.getString("event_type"));
                            String desc = obj.optString("event_description", "No description.");

                            eventList.add(new EventBoardItem(id, title, dateTime, location, type, desc));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error parsing event data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
