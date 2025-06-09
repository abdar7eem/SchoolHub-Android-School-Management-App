package com.example.schoolhub.Teacher;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.Schedule;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;
import com.example.schoolhub.Student.Adapter.ScheduleAdapter;
import com.example.schoolhub.Teacher.Adapter.TeacherSchedualAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TeacherScheduleFragment extends Fragment {

    private Spinner spnDay;
    private ListView listView;
    private TextView tvScheduleDay;
    private List<Schedule> scheduleList = new ArrayList<>();
    private TeacherSchedualAdapter adapter;
    private final String baseUrl = LoginActivity.baseUrl;
    private View rootView;
    private int teacherId;
    private int userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_teacher_schedule, container, false);

        if (getArguments() != null) {
            teacherId = getArguments().getInt("teacher_id", -1);
            userId = getArguments().getInt("user_id", -1);
        } else {
            teacherId = -1;
            userId = -1;
        }

        initViews();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fetchSchedule();
        } else {
            Toast.makeText(getContext(), "Android O or higher is required.", Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }

    private void initViews() {
        spnDay = rootView.findViewById(R.id.spnDay);
        listView = rootView.findViewById(R.id.lvTeacherSchedual);
        tvScheduleDay = rootView.findViewById(R.id.tvSchedualDay);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchSchedule() {
        String url = baseUrl + "get_teacher_schedule.php?id=" + userId;
        Log.d("User ID", String.valueOf(userId));

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    scheduleList.clear();
                    Set<String> daySet = new HashSet<>();
                    Log.d("TeacherSchedule", "Response: " + response.toString());
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);

                            Schedule schedule = new Schedule();
                            schedule.setId(obj.getInt("id"));
                            schedule.setClassId(obj.getInt("class_id"));
                            schedule.setSubjectId(obj.getInt("subject_id"));
                            schedule.setDayOfWeek(obj.getString("day_of_week"));
                            schedule.setStartTime(obj.getString("start_time"));
                            schedule.setEndTime(obj.getString("end_time"));
                            schedule.setRoom(obj.getString("room"));
                            schedule.setSubjectName(obj.getString("subject_name"));
                            schedule.setInstructorName(obj.getString("instructor_name"));
                            schedule.setClassName(obj.getString("class_name"));

                            Log.d("ScheduleParse", "Parsed subject: " + obj.toString());

                            scheduleList.add(schedule);
                            daySet.add(schedule.getDayOfWeek());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    setupDaySpinner(new ArrayList<>(daySet));
                },
                error -> {
                    Log.e("schedule Error", error.toString());
                    Toast.makeText(getContext(), "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }

    private void setupDaySpinner(List<String> days) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                days
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnDay.setAdapter(adapter);

        spnDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDay = parent.getItemAtPosition(position).toString();
                updateScheduleList(selectedDay);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        if (!days.isEmpty()) {
            updateScheduleList(days.get(0));
        }
    }

    private void updateScheduleList(String dayOfWeek) {
        List<Schedule> filtered = new ArrayList<>();
        for (Schedule s : scheduleList) {
            if (s.getDayOfWeek().equalsIgnoreCase(dayOfWeek)) {
                filtered.add(s);
            }
        }
        Log.d("FilteredData", "Items found for day " + dayOfWeek + ": " + filtered.size());

        if (!filtered.isEmpty()) {
            tvScheduleDay.setText(filtered.get(0).getDayOfWeek());
        } else {
            tvScheduleDay.setText("No Schedule Found");
        }

        adapter = new TeacherSchedualAdapter(requireContext(), filtered);
        listView.setAdapter(adapter);
    }
}
