package com.example.schoolhub.Student;

import android.os.Bundle;
import android.util.Log;
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
import com.example.schoolhub.Registration.LoginActivity;
import com.example.schoolhub.Student.Adapter.EventBoardAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentEventFragment extends Fragment {

    private ListView lstBooks;
    private EventBoardAdapter adapter;
    private List<EventBoardItem> eventList;
    private Set<Integer> confirmedEventIds = new HashSet<>();

    private  int studentId ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_events, container, false);
        if (getArguments() != null) {
            studentId = getArguments().getInt("student_id", -1);
        } else {
            studentId = -1;
        }
        lstBooks = view.findViewById(R.id.lstBooks);
        eventList = new ArrayList<>();

        fetchConfirmedEventsThenLoad();

        return view;
    }

    private void fetchConfirmedEventsThenLoad() {
        String url = LoginActivity.baseUrl+"check_event_confirmation.php?student_id=" + studentId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.length() > 0) {
                            JSONObject obj = response.getJSONObject(0);
                            JSONArray confirmed = obj.getJSONArray("confirmed_events");

                            confirmedEventIds.clear();
                            for (int i = 0; i < confirmed.length(); i++) {
                                confirmedEventIds.add(confirmed.getInt(i));
                            }
                        } else {
                            Log.w("JSON Parsing Warning", "No confirmed events found for student");
                        }

                        fetchEvents(); // Load events regardless
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSON Parsing Error", "Error parsing confirmation data: " + e.getMessage());
                        Toast.makeText(requireContext(), "Error parsing confirmation list", Toast.LENGTH_SHORT).show();
                        fetchEvents(); // fallback
                    }
                },
                error -> {
                    Toast.makeText(requireContext(), "Failed to check confirmations", Toast.LENGTH_SHORT).show();
                    fetchEvents(); // fallback
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void fetchEvents() {
        String url = LoginActivity.baseUrl+"get_event_board.php";

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

                        adapter = new EventBoardAdapter(requireContext(), eventList, studentId);
                        lstBooks.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSON Parsing Error", "Error parsing JSON data: " + e.getMessage());
                        Toast.makeText(requireContext(), "Error parsing event data", Toast.LENGTH_SHORT).show();
                    }
                },
                error ->{

                    Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private String capitalize(String input) {
        if (input == null || input.trim().isEmpty()) return "Unknown";
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
