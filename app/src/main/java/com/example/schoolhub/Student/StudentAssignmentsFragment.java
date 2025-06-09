package com.example.schoolhub.Student;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.Assignment;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;
import com.example.schoolhub.Student.Adapter.AssignmentAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.IOException;
import java.util.*;

public class StudentAssignmentsFragment extends Fragment {

    private ListView lstBooks;
    private AssignmentAdapter adapter;
    private List<Assignment> assignmentList = new ArrayList<>();

    private int studentId;
    private final String baseUrl = LoginActivity.baseUrl;

    private Button btnPending, btnSubmitted, btnGraded;
    private int pendingAssignmentId = -1;
    private Button pendingSubmitButton = null;

    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_assignments, container, false);

        lstBooks = view.findViewById(R.id.lstBooks);
        btnPending = view.findViewById(R.id.btnPending);
        btnSubmitted = view.findViewById(R.id.btnSubmitted);
        btnGraded = view.findViewById(R.id.btnGraded);

        if (getArguments() != null) {
            studentId = getArguments().getInt("student_id", -1);
        } else {
            studentId = -1;
        }

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri fileUri = result.getData().getData();
                        String fileName = getFileName(fileUri);

                        if (pendingSubmitButton != null) {
                            pendingSubmitButton.setText(fileName);
                        }

                        uploadFileToServer(fileUri, pendingAssignmentId, studentId);
                    }
                }
        );

        adapter = new AssignmentAdapter(requireActivity(), assignmentList, (assignmentId, button) -> {
            pendingAssignmentId = assignmentId;
            pendingSubmitButton = button;

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            filePickerLauncher.launch(Intent.createChooser(intent, "Select file to submit"));
        });

        lstBooks.setAdapter(adapter);
        fetchAssignmentsFromDB();

        btnPending.setOnClickListener(v -> filterBy("Pending", btnPending));
        btnSubmitted.setOnClickListener(v -> filterBy("Submitted", btnSubmitted));
        btnGraded.setOnClickListener(v -> filterBy("Graded", btnGraded));

        return view;
    }

    private void fetchAssignmentsFromDB() {
        String url = baseUrl + "get_student_assignments.php?student_id=" + studentId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    assignmentList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id");
                            String title = obj.getString("title");
                            String subject = obj.optString("subject", "-");
                            String teacher = obj.optString("teacher_name", "-");

                            String due = obj.getString("due_date");
                            String status = obj.optString("status", "Pending");
                            String attachment = obj.optString("attachment_path", "");
                            assignmentList.add(new Assignment(id, title, subject , teacher, due, status, attachment));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void filterBy(String status, Button activeButton) {

        updateButtonColors(activeButton);

        String url = baseUrl + "get_student_assignments.php?student_id=" + studentId + "&status=" + status;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    assignmentList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id");
                            String title = obj.getString("title");
                            String due = obj.getString("due_date");
                            String subject = obj.optString("subject", "-");
                            String teacher = obj.optString("teacher_name", "-");

                            String assignmentStatus = obj.optString("status", status);
                            String attachment = obj.optString("attachment_path", "");
                            assignmentList.add(new Assignment(id, title, subject, teacher, due, assignmentStatus, attachment));
                        }
                        adapter = new AssignmentAdapter(requireActivity(), assignmentList, (assignmentId, button) -> {
                            pendingAssignmentId = assignmentId;
                            pendingSubmitButton = button;

                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("*");
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            filePickerLauncher.launch(Intent.createChooser(intent, "Select file to submit"));
                        });
                        lstBooks.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(requireContext(), "Error loading assignments", Toast.LENGTH_SHORT).show()
        );

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

    private void uploadFileToServer(Uri fileUri, int assignmentId, int studentId) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(fileUri);
            byte[] fileBytes = new byte[inputStream.available()];
            inputStream.read(fileBytes);
            inputStream.close();

            String base64File = Base64.encodeToString(fileBytes, Base64.NO_WRAP);
            String fileName = getFileName(fileUri);
            String url = baseUrl + "student_submit_assignment.php";

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Toast.makeText(getContext(), "Submission successful", Toast.LENGTH_SHORT).show();
                        fetchAssignmentsFromDB();
                    },
                    error -> Toast.makeText(getContext(), "Upload failed: " + error.getMessage(), Toast.LENGTH_LONG).show()
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
//
                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }
            };

            Volley.newRequestQueue(requireContext()).add(request);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "File read error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) result = cursor.getString(index);
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}