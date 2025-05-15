package com.example.schoolhub.Teacher;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.*;
import com.example.schoolhub.Model.ClassInfo;
import com.example.schoolhub.Model.SubjectInfo;
import com.example.schoolhub.R;

import org.json.*;

import java.util.*;

public class TeacherScheduleExamFragment extends Fragment {

    Spinner spinnerClass, spinnerSubject, spinnerStartTime, spinnerEndTime;
    EditText etDate, etLocation;
    Button btnScheduleExam, btnConflictStatus;
    EditText etExamTitle;
    int selectedClassId, selectedSubjectId;
    String baseUrl = "http://192.168.3.246/SchoolHub/";
    int teacherId = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher__schedule__exam, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        spinnerClass = view.findViewById(R.id.spinnerClass);
        spinnerSubject = view.findViewById(R.id.spinnerSubject);
        spinnerStartTime = view.findViewById(R.id.spnStartTime);
        spinnerEndTime = view.findViewById(R.id.spnEndTime);
        etDate = view.findViewById(R.id.etDate);
        etLocation = view.findViewById(R.id.etLocation);
        btnScheduleExam = view.findViewById(R.id.btnScheduleExam);
        btnConflictStatus = view.findViewById(R.id.btnConflictStatus);
        etExamTitle = view.findViewById(R.id.etExamTitle);

        loadClasses();
        populateTimeSpinners();

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ClassInfo selected = (ClassInfo) spinnerClass.getSelectedItem();
                selectedClassId = selected.getId();
                loadSubjects(selectedClassId);
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SubjectInfo selected = (SubjectInfo) spinnerSubject.getSelectedItem();
                selectedSubjectId = selected.id;
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        etDate.setOnClickListener(v -> showDatePicker());

        btnConflictStatus.setOnClickListener(v -> {
            String date = etDate.getText().toString().trim();
            String startTime = spinnerStartTime.getSelectedItem().toString();
            String endTime = spinnerEndTime.getSelectedItem().toString();
            int duration = calculateDuration(startTime, endTime);
            String location = etLocation.getText().toString().trim();

            if (date.isEmpty()) {
                Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }
            checkConflict(selectedClassId, date, startTime, endTime, location);
        });

        btnScheduleExam.setOnClickListener(v -> {
            String date = etDate.getText().toString().trim();
            String startTime = spinnerStartTime.getSelectedItem().toString();
            String endTime = spinnerEndTime.getSelectedItem().toString();
            String location = etLocation.getText().toString().trim();
            int duration = calculateDuration(startTime, endTime);
            String examTitle = etExamTitle.getText().toString().trim();

            if (duration <= 0) {
                Toast.makeText(getContext(), "End time must be after start time", Toast.LENGTH_SHORT).show();
                return;
            }

            if (date.isEmpty() || location.isEmpty() || examTitle.isEmpty()) {
                Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            scheduleExam(selectedClassId, selectedSubjectId, date, startTime, endTime, duration, location, examTitle);
        });
    }

    private void populateTimeSpinners() {
        List<String> timeSlots = new ArrayList<>();
        for (int hour = 8; hour <= 17; hour++) {
            for (int min = 0; min < 60; min += 30) {
                timeSlots.add(String.format(Locale.getDefault(), "%02d:%02d", hour, min));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, timeSlots);
        spinnerStartTime.setAdapter(adapter);
        spinnerEndTime.setAdapter(adapter);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, day) -> {
            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
            etDate.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private int calculateDuration(String start, String end) {
        try {
            String[] s = start.split(":");
            String[] e = end.split(":");

            int startMinutes = Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]);
            int endMinutes = Integer.parseInt(e[0]) * 60 + Integer.parseInt(e[1]);

            return endMinutes - startMinutes;
        } catch (Exception ex) {
            return -1;
        }
    }

    private void checkConflict(int classId, String date, String startTime, String endTime, String location) {
        String url = baseUrl + "teacher_check_exam_conflict.php?class_id=" + classId +
                "&exam_date=" + date +
                "&start_time=" + startTime +
                "&end_time=" + endTime +
                "&location=" + Uri.encode(location);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    boolean conflict = response.optBoolean("conflict", false);
                    String message = response.optString("message", "");

                    if (conflict) {
                        btnConflictStatus.setText("Conflict Detected ▼");
                        btnConflictStatus.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        btnConflictStatus.setText("No Conflicts Detected ▼");
                        btnConflictStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2DA72D")));
                        Toast.makeText(getContext(), "No conflict. You can schedule.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Error checking conflict", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }


    private void scheduleExam(int classId, int subjectId, String date, String startTime,String endTime, int duration, String location, String examTitle) {
        String url = baseUrl + "teacher_schedule_exam.php";

        JSONObject data = new JSONObject();
        try {
            data.put("class_id", classId);
            data.put("subject_id", subjectId);
            data.put("exam_date", date);
            data.put("start_time", startTime);
            data.put("end_time", endTime);
            data.put("duration", duration);
            data.put("location", location);
            data.put("exam_title", examTitle);
            data.put("teacher_id", teacherId);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data,
                response -> {
                    if (response.optString("status").equals("success")) {
                        Toast.makeText(getContext(), "Exam scheduled successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("BTN erro", response.toString());
                        Toast.makeText(getContext(), "Error: " + response.optString("error"), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void loadClasses() {
        String url = baseUrl + "teacher_marks_get_classes.php?teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<ClassInfo> classList = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            classList.add(new ClassInfo(obj.getInt("id"), obj.getString("class_name"), "", ""));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    spinnerClass.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, classList));
                },
                error -> Toast.makeText(getContext(), "Failed to load classes", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(getContext()).add(request);
    }

    private void loadSubjects(int classId) {
        String url = baseUrl + "teacher_marks_get_subjects_by_class.php?class_id=" + classId + "&teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<SubjectInfo> subjects = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            subjects.add(new SubjectInfo(obj.getInt("id"), obj.getString("name")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    spinnerSubject.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, subjects));
                },
                error -> Toast.makeText(getContext(), "Failed to load subjects", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(getContext()).add(request);
    }
}
