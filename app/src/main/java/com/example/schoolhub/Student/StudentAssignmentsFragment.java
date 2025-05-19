package com.example.schoolhub.Student;

import static android.app.Activity.RESULT_OK;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.MainActivity;
import com.example.schoolhub.Model.Assignment;
import com.example.schoolhub.R;
import com.example.schoolhub.Student.Adapter.AssignmentAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentAssignmentsFragment extends Fragment {

    private ListView lstBooks;
    private AssignmentAdapter adapter;
    private final List<Assignment> assignmentList = new ArrayList<>();

    private final int studentId = 1; // Replace with actual logged-in student ID

    private Button btnPending, btnSubmitted, btnGraded;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_assignments, container, false);

        // Bind views
        lstBooks = view.findViewById(R.id.lstBooks);
        btnPending = view.findViewById(R.id.btnPending);
        btnSubmitted = view.findViewById(R.id.btnSubmitted);
        btnGraded = view.findViewById(R.id.btnGraded);

        // Init adapter
        adapter = new AssignmentAdapter(requireContext(), assignmentList);
        lstBooks.setAdapter(adapter);

        // Default load: Pending
        fetchAssignmentsFromDB("Pending");
        updateButtonColors(btnPending);

        // Button listeners
        btnPending.setOnClickListener(v -> {
            fetchAssignmentsFromDB("Pending");
            updateButtonColors(btnPending);
        });

        btnSubmitted.setOnClickListener(v -> {
            fetchAssignmentsFromDB("Submitted");
            updateButtonColors(btnSubmitted);
        });

        btnGraded.setOnClickListener(v -> {
            fetchAssignmentsFromDB("Graded");
            updateButtonColors(btnGraded);
        });

        return view;
    }

    private void fetchAssignmentsFromDB(String status) {
        String url = MainActivity.baseUrl+"get_student_assignments.php?student_id=" + studentId + "&status=" + status;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    assignmentList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String title = obj.getString("title");
                            String subject = obj.getString("subject");
                            String teacher = obj.getString("teacher_name");
                            String due = obj.getString("due_date");
                            String attachment = obj.optString("attachment_path", "");
                            int id = obj.getInt("id");
                            assignmentList.add(new Assignment(id, title, subject, teacher, due, status, attachment));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error parsing assignment data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Failed to fetch assignments", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void updateButtonColors(Button selected) {
        Button[] buttons = {btnPending, btnSubmitted, btnGraded};
        for (Button btn : buttons) {
            if (btn == selected) {
                btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.dark_red));
                btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            } else {
                btn.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
                btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            int assignmentId = requestCode - 1000;
            uploadFileToServer(fileUri, assignmentId, studentId);
        }
    }

    private void uploadFileToServer(Uri fileUri, int assignmentId, int studentId) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(fileUri);
            byte[] fileBytes = new byte[inputStream.available()];
            inputStream.read(fileBytes);
            inputStream.close();

            String base64File = Base64.encodeToString(fileBytes, Base64.DEFAULT);
            String fileName = getFileName(fileUri);

            StringRequest request = new StringRequest(Request.Method.POST,
                    MainActivity.baseUrl+"student_submit_assignment.php",
                    response -> {
                        Log.d("UPLOAD_SUCCESS", response);
                        Toast.makeText(getContext(), "Submission successful", Toast.LENGTH_SHORT).show();
                        fetchAssignmentsFromDB("Submitted");
                        updateButtonColors(btnSubmitted);
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(getContext(), "Upload failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }

            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("assignment_id", String.valueOf(assignmentId));
                    params.put("student_id", String.valueOf(studentId));
                    params.put("file", base64File);
                    params.put("filename", fileName);
                    return params;
                }
            };

            Volley.newRequestQueue(requireContext()).add(request);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FileError", "Error reading file: " + e.getMessage());
            Toast.makeText(getContext(), "File read error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null);
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
}
