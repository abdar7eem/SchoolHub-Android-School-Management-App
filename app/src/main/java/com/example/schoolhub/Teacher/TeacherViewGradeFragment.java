package com.example.schoolhub.Teacher;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.*;
import com.example.schoolhub.MainActivity;
import com.example.schoolhub.Model.ClassInfo;
import com.example.schoolhub.Model.SubjectInfo;
import com.example.schoolhub.R;

import org.json.*;

import java.util.*;

public class TeacherViewGradeFragment extends Fragment {

    Spinner spnClass, spnSubject;
    TextView tvClassAvgValue, tvTopStudentValue;
    TableLayout tableGrades;
    int teacherId = 1;
    int selectedClassId;
    int selectedSubjectId;
    String baseUrl = MainActivity.baseUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_view_grade, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        spnClass = view.findViewById(R.id.spnClass);
        spnSubject = view.findViewById(R.id.spnSubject);
        tvClassAvgValue = view.findViewById(R.id.tvClassAvgValue);
        tvTopStudentValue = view.findViewById(R.id.tvTopStudentValue);
        tableGrades = view.findViewById(R.id.tableGrades);

        loadClasses();

        spnClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ClassInfo selected = (ClassInfo) spnClass.getSelectedItem();
                selectedClassId = selected.getId();
                loadSubjects(selectedClassId);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spnSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SubjectInfo selected = (SubjectInfo) spnSubject.getSelectedItem();
                selectedSubjectId = selected.id;
                fetchGradeSummary(selectedClassId, selectedSubjectId);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    void loadClasses() {
        String url = baseUrl + "teacher_marks_get_classes.php?teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<ClassInfo> classList = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("class_name");
                            String room = obj.optString("room", "");
                            String subject = obj.optString("subject_name", "");
                            classList.add(new ClassInfo(id, name, room, subject));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ArrayAdapter<ClassInfo> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, classList);
                    spnClass.setAdapter(adapter);
                },
                error -> Toast.makeText(getContext(), "Failed to load classes", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(getContext()).add(request);
    }

    void loadSubjects(int classId) {
        String url = baseUrl + "teacher_marks_get_subjects_by_class.php?class_id=" + classId + "&teacher_id=" + teacherId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<SubjectInfo> subjectList = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            subjectList.add(new SubjectInfo(obj.getInt("id"), obj.getString("name")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ArrayAdapter<SubjectInfo> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, subjectList);
                    spnSubject.setAdapter(adapter);
                },
                error -> Toast.makeText(getContext(), "Failed to load subjects", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(getContext()).add(request);
    }

    void fetchGradeSummary(int classId, int subjectId) {
        String url = baseUrl + "teacher_get_grades_summary.php?class_id=" + classId + "&subject_id=" + subjectId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        tvClassAvgValue.setText(response.optDouble("class_avg", 0) + "%");
                        tvTopStudentValue.setText(response.optString("top_student", "N/A"));

                        JSONArray students = response.getJSONArray("students");
                        tableGrades.removeViews(1, tableGrades.getChildCount() - 1); // clear previous

                        for (int i = 0; i < students.length(); i++) {
                            JSONObject s = students.getJSONObject(i);
                            TableRow row = new TableRow(getContext());

                            row.addView(makeCell(s.getString("student_name")));

                            double assignment = s.optDouble("assignment_total", 0);
                            double assignmentMax = s.optDouble("assignment_max", 0);
                            row.addView(makeCell(formatMark(assignment, assignmentMax)));

                            double quiz = s.optDouble("quiz_total", 0);
                            double quizMax = s.optDouble("quiz_max", 0);
                            row.addView(makeCell(formatMark(quiz, quizMax)));

                            double mid = s.optDouble("mid_total", 0);
                            double midMax = s.optDouble("mid_max", 0);
                            row.addView(makeCell(formatMark(mid, midMax)));

                            double fin = s.optDouble("final_total", 0);
                            double finMax = s.optDouble("final_max", 0);
                            row.addView(makeCell(formatMark(fin, finMax)));

                            double total = assignment + quiz + mid + fin;
                            double totalMax = assignmentMax + quizMax + midMax + finMax;
                            row.addView(makeCell(formatMark(total, totalMax)));

                            tableGrades.addView(row);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Failed to parse data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Failed to load grades", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(getContext()).add(request);
    }

    TextView makeCell(String text) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setPadding(6, 6, 6, 6);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }

    String formatMark(double value, double max) {
        if (max == 0) return "-";
        return String.format("%.0f/%.0f", value, max);
    }
}
