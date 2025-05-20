package com.example.schoolhub.Teacher;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.ClassInfo;
import com.example.schoolhub.Model.SubjectInfo;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TeacherLeaderBoardFragment extends Fragment {

    private Spinner spnClass, spnSubject;
    private TextView tv1Student, tv1Grade, tv2Student, tv2Grade, tv3Student, tv3Grade;

    private final int teacherId = 1; // Replace with actual teacher ID
    private int selectedClassId, selectedSubjectId;
    private final String baseUrl = LoginActivity.baseUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_leader_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        spnClass = view.findViewById(R.id.spnClass);
        spnSubject = view.findViewById(R.id.spnSubject);

        tv1Student = view.findViewById(R.id.tv1Student);
        tv1Grade = view.findViewById(R.id.tv1Grade);
        tv2Student = view.findViewById(R.id.tv2Student);
        tv2Grade = view.findViewById(R.id.tv2Grade);
        tv3Student = view.findViewById(R.id.tv3Student);
        tv3Grade = view.findViewById(R.id.tv3Grade);

        loadClasses();

        spnClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ClassInfo selected = (ClassInfo) spnClass.getSelectedItem();
                selectedClassId = selected.getId();
                loadSubjects(selectedClassId);
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spnSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SubjectInfo selected = (SubjectInfo) spnSubject.getSelectedItem();
                selectedSubjectId = selected.id;
                fetchLeaderboard(selectedClassId, selectedSubjectId);
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });
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
                    spnClass.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, classList));
                },
                error -> Toast.makeText(getContext(), "Failed to load classes", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(request);
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
                    spnSubject.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, subjects));
                },
                error -> Toast.makeText(getContext(), "Failed to load subjects", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void fetchLeaderboard(int classId, int subjectId) {
        String url = baseUrl + "teacher_get_leaderboard.php?class_id=" + classId + "&subject_id=" + subjectId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    clearTop3();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String name = obj.getString("student_name");
                            int total = obj.getInt("total_grade");
                            int max = obj.getInt("max_grade");

                            String gradeText = "Grade: " + total + "/" + max;

                            switch (i) {
                                case 0:
                                    tv1Student.setText(name);
                                    tv1Grade.setText(gradeText);
                                    break;
                                case 1:
                                    tv2Student.setText(name);
                                    tv2Grade.setText(gradeText);
                                    break;
                                case 2:
                                    tv3Student.setText(name);
                                    tv3Grade.setText(gradeText);
                                    break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("Leaderboard Error", error.toString());
                    Toast.makeText(getContext(), "Failed to load leaderboard", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void clearTop3() {
        tv1Student.setText("N/A");
        tv1Grade.setText("Grade: -");
        tv2Student.setText("N/A");
        tv2Grade.setText("Grade: -");
        tv3Student.setText("N/A");
        tv3Grade.setText("Grade: -");
    }
}
