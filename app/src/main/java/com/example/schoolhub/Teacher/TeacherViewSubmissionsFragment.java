package com.example.schoolhub.Teacher;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.Submission;
import com.example.schoolhub.R;
import com.example.schoolhub.Teacher.Adapter.TeacherSubmissionAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherViewSubmissionsFragment extends Fragment {

    private Spinner spnClass;
    private RecyclerView rvSubmissions;
    private List<Submission> submissionList = new ArrayList<>();
    private TeacherSubmissionAdapter adapter;
    private int teacherId = 1;
    private List<Integer> classIds = new ArrayList<>(); // Keep track of classIds

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_view_submissions, container, false);

        spnClass = view.findViewById(R.id.spnClass);
        rvSubmissions = view.findViewById(R.id.rvViewSubmissions);
        rvSubmissions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TeacherSubmissionAdapter(getContext(), submissionList);
        rvSubmissions.setAdapter(adapter);

        loadClasses();

        spnClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected classId from the classIds list
                int classId = classIds.get(position);
                loadSubmissions(classId); // Load submissions for the selected classId
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optionally handle case when no item is selected
            }
        });

        return view;
    }

    private void loadClasses() {
        String url = "http://192.168.3.246/SchoolHub/teacher_get_classes.php?id=1";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<String> classNames = new ArrayList<>();

                        // Clear existing classIds before adding new ones
                        classIds.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            classIds.add(obj.getInt("id")); // Add classId to the list
                            classNames.add(obj.getString("name")); // Add className to the list
                        }

                        // Set up the spinner adapter with the class names
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                                android.R.layout.simple_spinner_dropdown_item, classNames);
                        spnClass.setAdapter(spinnerAdapter);

                    } catch (JSONException e) {
                        Log.e("JSON Parsing Error", e.getMessage());
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("Classes Error", error.toString());
                    Toast.makeText(getContext(), "Failed to load classes", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("teacher_id", String.valueOf(teacherId));
                return map;
            }
        };

        Volley.newRequestQueue(getContext()).add(request);
    }

    private void loadSubmissions(int classId) {
        String url = "http://192.168.3.246/SchoolHub/teacher_get_submissions.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        submissionList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Submission s = new Submission(
                                    obj.getInt("submission_id"),
                                    obj.getString("student_name"),
                                    obj.getString("subject_name"),
                                    obj.getString("submission_date"),
                                    obj.getString("file_url")
                            );
                            submissionList.add(s);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                        Log.e("Submit error", error.toString());
                        Toast.makeText(getContext(), "Error loading submissions", Toast.LENGTH_SHORT).show();

        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("class_id", String.valueOf(1));
                map.put("teacher_id", String.valueOf(teacherId));
                return map;
            }
        };

        Volley.newRequestQueue(getContext()).add(request);
    }
}
