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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.Submission;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;
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
    private List<Integer> classIds = new ArrayList<>();

    private int teacherId;
    private final String baseUrl = LoginActivity.baseUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_view_submissions, container, false);

        spnClass = view.findViewById(R.id.spnClass);
        rvSubmissions = view.findViewById(R.id.rvViewSubmissions);

        rvSubmissions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TeacherSubmissionAdapter(getContext(), submissionList);
        rvSubmissions.setAdapter(adapter);

        if (getArguments() != null) {
            teacherId = getArguments().getInt("teacher_id", -1);
        } else {
            teacherId = -1;
        }

        loadClasses();

        spnClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedClassId = classIds.get(position);
                loadSubmissions(selectedClassId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    private void loadClasses() {
        String url = baseUrl + "teacher_get_classes.php?teacher_id=" + teacherId +" = 1";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<String> classNames = new ArrayList<>();
                        classIds.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            classIds.add(obj.getInt("id"));
                            classNames.add(obj.getString("class_name"));
                        }

                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                                getContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                classNames
                        );
                        spnClass.setAdapter(spinnerAdapter);
                    } catch (JSONException e) {
                        Log.e("JSON Error", "Failed to parse classes: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to parse class data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("Class Load Error", error.toString());
                    Toast.makeText(getContext(), "Failed to load classes", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("teacher_id", String.valueOf(teacherId));
                return map;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void loadSubmissions(int classId) {
        String url = baseUrl + "teacher_get_submissions.php?class_id=" + classId + "&teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    submissionList.clear();
                    Log.e("Submittions Response", response.toString() + "");
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            submissionList.add(new Submission(
                                    obj.getInt("submission_id"),
                                    obj.getString("student_name"),
                                    obj.getString("subject_name"),
                                    obj.getString("submission_date"),
                                    obj.optString("file_url", null)
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("ParseError", e.getMessage());
                        Toast.makeText(getContext(), "Failed to parse submissions", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("VolleyError", error.toString());
                    Toast.makeText(getContext(), "No submissions", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }
}
