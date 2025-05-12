package com.example.schoolhub.Teacher;

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
import com.example.schoolhub.Teacher.Adapter.TeacherSchedualAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherScheduleFragment extends Fragment {

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_schedule, container, false);

        listView = view.findViewById(R.id.lvTeacherSchedual); // Ensure this ID exists in your layout

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fetchTeacherSchedule();
        } else {
            Toast.makeText(getContext(), "Requires Android O or higher", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchTeacherSchedule() {
        int teacherId = 1; // Replace with dynamic ID if available
        String url = "https://almushtarakagroup.infinityfreeapp.com/teacher_schedual.php?id=" + teacherId;

        RequestQueue queue = Volley.newRequestQueue(requireContext());



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
                            schedule.setDayOfWeek(obj.getString("day"));

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

                            try {
                                LocalTime startTime = LocalTime.parse(obj.getString("start_time"), formatter);
                                LocalTime endTime = LocalTime.parse(obj.getString("end_time"), formatter);
                                schedule.setStartTime(startTime);
                                schedule.setEndTime(endTime);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            schedule.setRoom(obj.getString("room"));
                            schedule.setSubjectName(obj.getString("subject_name"));
                            schedule.setInstructorName(obj.getString("instructor_name"));

                            scheduleList.add(schedule);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    TeacherSchedualAdapter adapter = new TeacherSchedualAdapter(getContext(), scheduleList);
                    listView.setAdapter(adapter);
                },
                error -> {
                    Log.e("VolleyError", "Error fetching schedule: " + error.getMessage());
                    Toast.makeText(getContext(), "Failed to load schedule", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
                return headers;
            }
        };

        queue.add(request);
    }
}
