package com.example.schoolhub.Student;

import android.os.Build;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentScheduleFragment extends Fragment {

    private Spinner spnDay;
    private TextView txtRoomNumber;
    private ListView listView;
    private List<Schedule> scheduleList = new ArrayList<>();
    private ScheduleAdapter adapter;
    private View rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_student_schedule, container, false);
        initViews(rootView);
        setupSpinner();
        return rootView;
    }

    private void initViews(View view) {
        spnDay = view.findViewById(R.id.spnDay);
        txtRoomNumber = view.findViewById(R.id.txtRoomNumber);
        listView = view.findViewById(R.id.listView2);
    }

    private void setupSpinner() {
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"}
        );

        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnDay.setAdapter(dayAdapter);

        spnDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDay = parent.getItemAtPosition(position).toString();
                updateScheduleList(selectedDay);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fetchSchedule();
        } else {
            Toast.makeText(getContext(), "This feature requires Android O or higher.", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void fetchSchedule() {
        String id = "1";
        String url = LoginActivity.baseUrl+"get_schedule.php?id=" + id;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    scheduleList.clear();
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

                            scheduleList.add(schedule);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // Update the list based on currently selected day
                    String selectedDay = spnDay.getSelectedItem().toString();
                    updateScheduleList(selectedDay);
                },
                error -> Toast.makeText(getContext(), "Error: " + error.toString(), Toast.LENGTH_LONG).show()
        );

        queue.add(request);
    }

    private void updateScheduleList(String selectedDay) {
        List<Schedule> filtered = new ArrayList<>();
        for (Schedule s : scheduleList) {
            if (s.getDayOfWeek().equalsIgnoreCase(selectedDay)) {
                filtered.add(s);
            }
        }

        if (!filtered.isEmpty()) {
            txtRoomNumber.setText("Your Room is " + filtered.get(0).getRoom());
        } else {
            txtRoomNumber.setText("No Room Found");
        }

        adapter = new ScheduleAdapter(requireContext(), filtered);
        listView.setAdapter(adapter);

    }


}
