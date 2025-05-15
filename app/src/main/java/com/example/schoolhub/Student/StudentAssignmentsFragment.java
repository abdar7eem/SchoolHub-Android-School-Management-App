package com.example.schoolhub.Student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.Assignment;
import com.example.schoolhub.R;
import com.example.schoolhub.Student.Adapter.AssignmentAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentAssignmentsFragment extends Fragment {

    private ListView lstBooks;
    private AssignmentAdapter adapter;
    private List<Assignment> assignmentList = new ArrayList<>();

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

        // Set default adapter
        adapter = new AssignmentAdapter(requireContext(), assignmentList);
        lstBooks.setAdapter(adapter);

        // Fetch from backend
        fetchAssignmentsFromDB();

        // Button logic
        btnPending.setOnClickListener(v -> filterBy("Pending", btnPending));
        btnSubmitted.setOnClickListener(v -> filterBy("Submitted", btnSubmitted));
        btnGraded.setOnClickListener(v -> filterBy("Graded", btnGraded));

        return view;
    }

    private void fetchAssignmentsFromDB() {
        String url = "http://192.168.1.13/SchoolHub/get_student_assignments.php?student_id=" + studentId;

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
                            String status = obj.optString("status", "Pending"); // Optional: support status column

                            assignmentList.add(new Assignment(title, subject, teacher, due, status));
                        }

                        adapter = new AssignmentAdapter(requireContext(), assignmentList);
                        lstBooks.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void filterBy(String status, Button activeButton) {
        List<Assignment> filtered = new ArrayList<>();
        for (Assignment a : assignmentList) {
            if (a.getStatus().equalsIgnoreCase(status)) {
                filtered.add(a);
            }
        }

        adapter = new AssignmentAdapter(requireContext(), filtered);
        lstBooks.setAdapter(adapter);
        updateButtonColors(activeButton);
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
}
