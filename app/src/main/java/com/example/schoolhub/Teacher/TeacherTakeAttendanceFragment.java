package com.example.schoolhub.Teacher;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.ClassInfo;
import com.example.schoolhub.Model.Student;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;
import com.example.schoolhub.Teacher.Adapter.TeacherAttendanceAdapter;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TeacherTakeAttendanceFragment extends Fragment {

    private Spinner spinnerClasses;
    private RecyclerView recyclerView;
    private Button btnSave;
    private TeacherAttendanceAdapter adapter;
    private List<Student> studentList = new ArrayList<>();
    private List<ClassInfo> classList = new ArrayList<>();
    private int selectedClassId = -1;
    private int teacherId;

    private final String BASE_URL = LoginActivity.baseUrl;

    private TextView tvClass, tvRoom, tvDate, tvToday;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_teacher_take_attendance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView qrCodeImg = view.findViewById(R.id.qrCodeImg);
        qrCodeImg.setOnClickListener(v -> generateAttendanceSessionAndShowQR());

        spinnerClasses = view.findViewById(R.id.spnTeacherTakeAttendance);
        recyclerView = view.findViewById(R.id.rvAttendanceList);
        btnSave = view.findViewById(R.id.btnSave);

        tvClass = view.findViewById(R.id.tvClass);
        tvRoom = view.findViewById(R.id.tvRoom);
        tvDate = view.findViewById(R.id.tvDate);
        tvToday = view.findViewById(R.id.tvToday);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TeacherAttendanceAdapter(studentList);
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            teacherId = getArguments().getInt("teacher_id", -1);
        } else {
            teacherId = -1;
        }

        loadHomeroomClasses();

        spinnerClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ClassInfo selected = classList.get(position);
                selectedClassId = selected.getId();
                updateClassDetailsUI(selected);
                loadStudentsForClass(selectedClassId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnSave.setOnClickListener(v -> submitAttendance());
    }


    private void loadHomeroomClasses() {
        String url = BASE_URL + "teacher_get_classes.php?teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    classList.clear();
                    List<String> classNames = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("class_name");
                            String room = obj.optString("room", "N/A");
                            String subject = obj.optString("subject_name", "N/A");

                            classList.add(new ClassInfo(id, name, room, subject));
                            classNames.add(name);
                        } catch (JSONException e) {
                            Log.e("JSON Response", e.toString());
                            e.printStackTrace();
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, classNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerClasses.setAdapter(adapter);
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(getContext(), "Failed to load classes", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void updateClassDetailsUI(ClassInfo classInfo) {
        tvClass.setText(classInfo.getName());
        tvRoom.setText(classInfo.getRoom());

        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

        tvDate.setText(dateFormat.format(today));
        tvToday.setText(dayFormat.format(today));
    }

    private void loadStudentsForClass(int classId) {
        String url = BASE_URL + "teacher_get_students.php";

        JSONObject params = new JSONObject();
        try {
            params.put("class_id", classId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    try {
                        studentList.clear();

                        if (response.has("students")) {
                            JSONArray students = response.getJSONArray("students");

                            for (int i = 0; i < students.length(); i++) {
                                JSONObject obj = students.getJSONObject(i);
                                int id = obj.getInt("student_id");
                                String name = obj.getString("student_name");
                                studentList.add(new Student(id, name, "Absent"));
                            }

                            adapter.notifyDataSetChanged();

                            if (students.length() == 0) {
                                Toast.makeText(getContext(), "No students found for this class.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (response.has("error")) {
                            Log.e("AttendanceError", response.getString("error"));
                            Toast.makeText(getContext(), response.getString("error"), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Log.e("JSON Parsing Error", e.getMessage());
                        Toast.makeText(getContext(), "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(getContext(), "Failed to load students", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void submitAttendance() {
        String url = BASE_URL + "teacher_submit_attendance.php";

        JSONArray attendanceArray = new JSONArray();
        for (Student s : studentList) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("student_id", s.getId());
                obj.put("status", s.getStatus().toLowerCase());
                attendanceArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("teacher_id", teacherId);
            requestBody.put("class_id", selectedClassId);
            requestBody.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
            requestBody.put("start_time", new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
            requestBody.put("attendance", attendanceArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    Log.d("ATTENDANCE_RESPONSE", response.toString());
                    Toast.makeText(getContext(), "Response: " + response.toString(), Toast.LENGTH_LONG).show();
                },
                error -> Toast.makeText(getContext(), "Failed to submit attendance", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void generateAttendanceSessionAndShowQR() {
        if (selectedClassId == -1) {
            Toast.makeText(getContext(), "Please select a class first", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = BASE_URL + "teacher_create_attendance_session.php";

        // Prepare JSON parameters
        JSONObject params = new JSONObject();
        try {
            params.put("teacher_id", teacherId);
            params.put("class_id", selectedClassId);
            params.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
            params.put("start_time", new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error creating JSON request", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send POST request to server
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                response -> {
                    try {
                        if (response.has("success") && response.getBoolean("success")) {
                            String sessionCode = response.getString("session_code");
                            showQRCodeDialog(sessionCode);
                        } else {
                            String errorMsg = response.has("error") ? response.getString("error") : "Unknown server error";
                            Log.e("QR_SESSION_ERROR", "Server error: " + errorMsg);
                            Toast.makeText(getContext(), "Failed: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Invalid server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("QR_SESSION_ERROR", "Volley error: " + error.toString());
                    Toast.makeText(getContext(), "Network/server error", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Add request to queue
        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void showQRCodeDialog(String sessionData) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(sessionData, BarcodeFormat.QR_CODE, 600, 600);

            ImageView imageView = new ImageView(getContext());
            imageView.setImageBitmap(bitmap);

            new AlertDialog.Builder(getContext())
                    .setTitle("Student Attendance QR")
                    .setView(imageView)
                    .setPositiveButton("Close", null)
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "QR generation error", Toast.LENGTH_SHORT).show();
        }
    }


}

