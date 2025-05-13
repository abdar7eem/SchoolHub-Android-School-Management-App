package com.example.schoolhub.Student;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.Schedule;
import com.example.schoolhub.R;
import com.example.schoolhub.Student.Adapter.ScheduleAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentScheduleFragment extends Fragment {

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_schedule, container, false); // Make sure this layout is correct
        listView = view.findViewById(R.id.listView2); // Make sure this ID exists in your XML

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fetchStudentSchedule();
        } else {
            Toast.makeText(getContext(), "Requires Android O or higher", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchStudentSchedule() {
        String id = "1";
        String url = "http://192.168.3.246/SchoolHub/get_schedule.php?id=" + id; // Use 10.0.2.2 for emulator, change for real device

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("JSON_RESPONSE", response.toString());

                    List<Schedule> scheduleList = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);

                            Schedule schedule = new Schedule();
                            schedule.setId(obj.getInt("id"));
                            schedule.setClassId(obj.getInt("class_id"));
                            schedule.setSubjectId(obj.getInt("subject_id"));
                            schedule.setDayOfWeek(obj.getString("day"));

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

                            try {
                                LocalTime startTime = LocalTime.parse(obj.getString("start_time"), formatter);
                                LocalTime endTime = LocalTime.parse(obj.getString("end_time"), formatter);
                                schedule.setStartTime(startTime);
                                schedule.setEndTime(endTime);
                            } catch (Exception e) {
                                Log.e("TimeParseError", e.getMessage());
                            }

                            schedule.setRoom(obj.getString("room"));
                            schedule.setSubjectName(obj.getString("subject_name"));
                            schedule.setInstructorName(obj.getString("instructor_name"));

                            scheduleList.add(schedule);

                        } catch (JSONException e) {
                            Log.e("JSONParseError", e.getMessage());
                        }
                    }

                    if (!scheduleList.isEmpty()) {
                        ScheduleAdapter adapter = new ScheduleAdapter(getContext(), scheduleList);
                        listView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getContext(), "No schedule found", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("VolleyError", "Error fetching schedule: " + error.toString());
                    Toast.makeText(getContext(), "Failed to load schedule", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "Mozilla/5.0");
                return headers;
            }
        };

        queue.add(request);
    }
}
