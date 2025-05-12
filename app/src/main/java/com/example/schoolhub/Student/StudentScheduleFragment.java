package com.example.schoolhub.Student;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

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
import java.util.List;

public class StudentScheduleFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_schedule, container, false);

        // Check for Android version before calling fetchSchedule
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fetchSchedule();
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchSchedule() {
        String url = "http://your-server.com/api/get_schedule.php?student_id=123"; // Customize this URL

        // Create a request queue to handle Volley requests
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Create a new JSON Array request to fetch schedule
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<Schedule> scheduleList = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);

                            Schedule schedule = new Schedule();
                            schedule.setId(obj.getInt("id"));
                            schedule.setClassId(obj.getInt("class_id"));
                            schedule.setSubjectId(obj.getInt("subject_id"));
                            schedule.setDayOfWeek(obj.getString("day_of_week"));

                            String startTimeStr = obj.getString("start_time");
                            String endTimeStr = obj.getString("end_time");

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

                            // Parse the time strings to LocalTime
                            try {
                                LocalTime startTime = LocalTime.parse(startTimeStr, formatter);
                                LocalTime endTime = LocalTime.parse(endTimeStr, formatter);

                                // Set the start and end times
                                schedule.setStartTime(startTime);
                                schedule.setEndTime(endTime);
                            } catch (Exception e) {
                                e.printStackTrace();
                                // Handle invalid time format here, maybe use default times
                            }

                            // Set the other fields
                            schedule.setRoom(obj.getString("room"));
                            schedule.setSubjectName(obj.getString("subject_name")); // Assuming returned from JOIN query
                            schedule.setInstructorName(obj.getString("instructor_name"));

                            // Add the schedule to the list
                            scheduleList.add(schedule);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing errors here
                        }
                    }

                    // Set the adapter for the ListView to display the schedules
                    ScheduleAdapter adapter = new ScheduleAdapter(getContext(), scheduleList);
                    ListView listView = getView().findViewById(R.id.listView2);
                    listView.setAdapter(adapter);

                },
                error -> {
                    // Handle errors during the request
                    Toast.makeText(getContext(), "Failed to load schedule", Toast.LENGTH_SHORT).show();
                });

        // Add the request to the request queue for execution
        queue.add(request);
    }
}
