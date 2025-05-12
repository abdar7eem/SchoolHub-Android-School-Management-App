package com.example.schoolhub.Student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class StudentScheduleFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_schedule, container, false);
        fetchSchedule(); // call the method here
        return view;
    }




    private void fetchSchedule() {
        String url = "http://your-server.com/api/get_schedule.php?student_id=123"; // Customize this


        RequestQueue queue = Volley.newRequestQueue(getContext());

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
                            //schedule.setStartTime(obj.getString("start_time"));
                           // schedule.setEndTime(obj.getString("end_time"));
                            schedule.setRoom(obj.getString("room"));
                            schedule.setSubjectName(obj.getString("subject_name")); // returned from JOIN
                            schedule.setInstructorName(obj.getString("instructor_name"));
                            scheduleList.add(schedule);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ScheduleAdapter adapter = new ScheduleAdapter(getContext(), scheduleList);
                    ListView listView = getView().findViewById(R.id.listView2);
                    listView.setAdapter(adapter);
                },
                error -> {
                    Toast.makeText(getContext(), "Failed to load schedule", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }






}