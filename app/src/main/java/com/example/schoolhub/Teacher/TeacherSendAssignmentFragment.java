package com.example.schoolhub.Teacher;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.MainActivity;
import com.example.schoolhub.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.IOException;
import java.util.*;

public class TeacherSendAssignmentFragment extends Fragment {

    Spinner spnSubjects, spnClasses;
    EditText titleInput, descInput, dueDateInput, dueTimeInput;
    Button chooseFileButton, sendButton;
    Uri fileUri;
    String fileName;

    String baseUrl = MainActivity.baseUrl;
    int teacherId = 1; // Replace with real teacher ID

    List<ClassInfo> classList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_send_assignment, container, false);

        spnSubjects = view.findViewById(R.id.spnSubjects);
        spnClasses = view.findViewById(R.id.spnClasses);
        titleInput = view.findViewById(R.id.titleInput);
        descInput = view.findViewById(R.id.descInput);
        dueDateInput = view.findViewById(R.id.dueDateInput);
        dueTimeInput = view.findViewById(R.id.dueTimeInput);
        chooseFileButton = view.findViewById(R.id.chooseFileButton);
        sendButton = view.findViewById(R.id.sendButton);

        loadClasses();

        spnClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ClassInfo selected = (ClassInfo) spnClasses.getSelectedItem();
                loadSubjectsByClass(String.valueOf(selected.id));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        dueDateInput.setOnClickListener(v -> showDatePicker());
        dueTimeInput.setOnClickListener(v -> showTimePicker());
        chooseFileButton.setOnClickListener(v -> chooseFile());
        sendButton.setOnClickListener(v -> sendAssignment());

        return view;
    }

    void loadClasses() {
        String url = baseUrl + "teacher_get_classes.php?teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    classList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("class_name"); // or "name" depending on your PHP output
                            classList.add(new ClassInfo(id, name));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<ClassInfo> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, classList);
                    spnClasses.setAdapter(adapter);
                    Log.d("ClassResponse", response.toString());
                },
                error -> {
                    Log.e("Classes Error", error.toString());
                    Toast.makeText(getContext(), "Failed to load classes", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(getContext()).add(request);
    }

    void loadSubjectsByClass(String classId) {
        String url = baseUrl + "teacher_get_subjects_by_class.php?class_id=" + classId + "&teacher_id=" + teacherId;
        Log.d("SubjectRequestURL", url);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<String> subjects = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            subjects.add(obj.getString("id") + "-" + obj.getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, subjects);
                    spnSubjects.setAdapter(adapter);
                    Log.d("SubjectResponse", response.toString());
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Failed to load subjects", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(getContext()).add(request);
    }

    void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, day) ->
                dueDateInput.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    void showTimePicker() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(getContext(), (view, hour, minute) ->
                dueTimeInput.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute)),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && data != null && data.getData() != null) {
            fileUri = data.getData();
            fileName = getFileName(fileUri);
            chooseFileButton.setText(fileName);
        }
    }

    String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (index >= 0) result = cursor.getString(index);
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    void sendAssignment() {
        if (fileUri == null) {
            Toast.makeText(getContext(), "Select a file first", Toast.LENGTH_SHORT).show();
            return;
        }

        String dueDateStr = dueDateInput.getText().toString();
        String dueTimeStr = dueTimeInput.getText().toString();

        if (dueDateStr.isEmpty() || dueTimeStr.isEmpty()) {
            Toast.makeText(getContext(), "Please select due date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse due date and time
        Calendar dueCalendar = Calendar.getInstance();
        try {
            String[] dateParts = dueDateStr.split("-");
            String[] timeParts = dueTimeStr.split(":");

            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // Calendar months are 0-based
            int day = Integer.parseInt(dateParts[2]);
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            dueCalendar.set(year, month, day, hour, minute, 0);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Invalid due date or time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Compare with current time
        Calendar now = Calendar.getInstance();
        if (dueCalendar.before(now)) {
            Toast.makeText(getContext(), "Due date/time cannot be in the past", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(fileUri);
            byte[] fileBytes = new byte[inputStream.available()];
            inputStream.read(fileBytes);
            String fileBase64 = Base64.encodeToString(fileBytes, Base64.DEFAULT);

            StringRequest request = new StringRequest(Request.Method.POST, baseUrl + "teacher_upload_assignment.php",
                    response -> Toast.makeText(getContext(), "Assignment sent successfully", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(getContext(), "Upload Failed", Toast.LENGTH_SHORT).show()) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("teacher_id", String.valueOf(teacherId));
                    params.put("subject_id", spnSubjects.getSelectedItem().toString().split("-")[0]);
                    params.put("class_id", String.valueOf(((ClassInfo) spnClasses.getSelectedItem()).id));
                    params.put("title", titleInput.getText().toString());
                    params.put("description", descInput.getText().toString());
                    params.put("due_date", dueDateStr);
                    params.put("due_time", dueTimeStr);
                    params.put("file", fileBase64);
                    params.put("filename", fileName);
                    return params;
                }
            };

            Volley.newRequestQueue(getContext()).add(request);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "File error", Toast.LENGTH_SHORT).show();
        }
    }


    // ClassInfo model
    public static class ClassInfo {
        public int id;
        public String name;

        public ClassInfo(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
