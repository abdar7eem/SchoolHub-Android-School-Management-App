package com.example.schoolhub.Student;

import android.app.Activity;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.Assignment;
import com.example.schoolhub.Model.NotificationHelper;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;
import com.example.schoolhub.Student.Adapter.AssignmentAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentAssignmentsFragment extends Fragment {

    private ListView lstBooks;
    private AssignmentAdapter adapter;
    private List<Assignment> assignmentList = new ArrayList<>();
    private int studentId;
    private final String baseUrl = LoginActivity.baseUrl;
    private Button btnPending, btnSubmitted;
    private int pendingAssignmentId = -1;
    private Button pendingSubmitButton = null;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private int classId;
    private int teacherId;
    private int subjectId;
    private int userId;
    private String fileName1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_assignments, container, false);

        lstBooks = view.findViewById(R.id.lstBooks);
        btnPending = view.findViewById(R.id.btnPending);
        btnSubmitted = view.findViewById(R.id.btnSubmitted);

        if (getArguments() != null) {
            studentId = getArguments().getInt("student_id", -1);
            userId = getArguments().getInt("user_id", -1);
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

        adapter = new AssignmentAdapter(requireActivity(), assignmentList, (assignment, button) -> {
            pendingAssignmentId = assignment.getId();
            classId = assignment.getClassId();
            teacherId = assignment.getTeacherId();
            subjectId = assignment.getSubjectId();
            pendingSubmitButton = button;

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            filePickerLauncher.launch(Intent.createChooser(intent, "Select file to submit"));
        });

        lstBooks.setAdapter(adapter);
        filterBy("Pending", btnPending);

        btnPending.setOnClickListener(v -> filterBy("Pending", btnPending));
        btnSubmitted.setOnClickListener(v -> filterBy("Submitted", btnSubmitted));

        return view;
    }

    private void fetchAssignmentsFromDB() {
        String url = baseUrl + "get_assignment_details.php?student_id=" + studentId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    assignmentList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            Assignment a = new Assignment(
                                    obj.getInt("id"),
                                    obj.getString("title"),
                                    obj.optString("subject", "-"),
                                    obj.optString("teacher_name", "-"),
                                    obj.getString("due_date"),
                                    "Pending",
                                    obj.optString("attachment_path", "")
                            );
                            a.setClassId(obj.optInt("class_id", -1));
                            a.setTeacherId(obj.optInt("teacher_id", -1));
                            a.setSubjectId(obj.optInt("subject_id", -1));

                            assignmentList.add(a);
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
                            Assignment a = new Assignment(
                                    obj.getInt("id"),
                                    obj.getString("title"),
                                    obj.optString("subject", "-"),
                                    obj.optString("teacher_name", "-"),
                                    obj.getString("due_date"),
                                    obj.optString("status", status),
                                    obj.optString("attachment_path", "")
                            );
                            a.setClassId(obj.optInt("class_id", -1));
                            a.setTeacherId(obj.optInt("teacher_id", -1));
                            a.setSubjectId(obj.optInt("subject_id", -1));
                            assignmentList.add(a);
                        }

                        adapter = new AssignmentAdapter(requireActivity(), assignmentList, (assignment, button) -> {
                            pendingAssignmentId = assignment.getId();
                            classId = assignment.getClassId();
                            teacherId = assignment.getTeacherId();
                            subjectId = assignment.getSubjectId();
                            pendingSubmitButton = button;

                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("*/*");
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
        Button[] buttons = {btnPending, btnSubmitted};
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
            fileName1 = fileName;

            String url = baseUrl + "student_submit_assignment.php";

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Toast.makeText(getContext(), "Submission successful", Toast.LENGTH_SHORT).show();
                        fetchAssignmentsFromDB();
                        Log.e("RESPONSE", response);
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
                    params.put("class_id", String.valueOf(classId));
                    params.put("teacher_id", String.valueOf(teacherId));
                    params.put("subject_id", String.valueOf(subjectId));
                    return params;
                }

                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }
            };

            Volley.newRequestQueue(requireContext()).add(request);

            String notifyUrl = baseUrl + "get_teacher_user_id.php?class_id=" + classId + "&subject_id=" + subjectId;

            JsonObjectRequest teacherRequest = new JsonObjectRequest(Request.Method.GET, notifyUrl, null,
                    response -> {
                        try {
                            if (!response.has("error")) {
                                int teacherUserId = response.getInt("user_id");

                                NotificationHelper.sendNotification(
                                        getContext(),
                                        "Assignment Submitted",
                                        "A student submitted: " + fileName1,
                                        "student",
                                        userId,
                                        subjectId,
                                        teacherUserId
                                );
                            } else {
                                Toast.makeText(getContext(), "Notification not sent: " + response.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> Toast.makeText(getContext(), "Error finding teacher", Toast.LENGTH_SHORT).show()
            );

            Volley.newRequestQueue(requireContext()).add(teacherRequest);

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
