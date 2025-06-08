package com.example.schoolhub.Teacher;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.*;
import com.example.schoolhub.Model.ClassInfo;
import com.example.schoolhub.Model.StudentGrade;
import com.example.schoolhub.Model.SubjectInfo;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;
import com.example.schoolhub.Teacher.Adapter.TeacherPublishMarksAdabter;

import org.json.*;

import java.util.*;

public class TeacherPublishMarksFragment extends Fragment {

    Spinner spnSubject, spnClass, spnMarkType;
    EditText etGradeValue;
    RecyclerView rvStudents;
    Button btnSaveMarks;

    List<StudentGrade> studentList = new ArrayList<>();
    TeacherPublishMarksAdabter adapter;

    int teacherId;
    int selectedClassId;
    int selectedSubjectId;
    String selectedMarkType;
    private final String baseUrl = LoginActivity.baseUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_publish_marks, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        spnSubject = view.findViewById(R.id.spnSubject);
        spnClass = view.findViewById(R.id.spnClass);
        spnMarkType = view.findViewById(R.id.spnTitle); 
        etGradeValue = view.findViewById(R.id.etGradeValue);
        rvStudents = view.findViewById(R.id.rvStudents);
        btnSaveMarks = view.findViewById(R.id.btnSaveMarks);

        rvStudents.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TeacherPublishMarksAdabter(getContext(), studentList);
        rvStudents.setAdapter(adapter);

        if (getArguments() != null) {
            teacherId = getArguments().getInt("teacher_id", -1);
        } else {
            teacherId = -1; // fallback
        }

        setupMarkTypeSpinner();
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
                loadStudentsByClass(selectedClassId);
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spnMarkType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMarkType = spnMarkType.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSaveMarks.setOnClickListener(v -> submitGrades());
    }

    void setupMarkTypeSpinner() {
        String[] types = {"Quiz", "Assignment", "Mid", "Final"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, types);
        spnMarkType.setAdapter(adapter);
        selectedMarkType = types[0]; // default
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
                    ArrayAdapter<ClassInfo> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, classList) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            TextView view = (TextView) super.getView(position, convertView, parent);
                            view.setText(classList.get(position).getName());
                            return view;
                        }

                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                            view.setText(classList.get(position).getName());
                            return view;
                        }
                    };
                    spnClass.setAdapter(adapter);
                },
                error -> {
                    Log.e("Classes Error", error.toString());
                    Toast.makeText(getContext(), "Failed to load classes", Toast.LENGTH_SHORT).show();
                });

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
                    spnSubject.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, subjectList));
                },
                error -> {
                    Log.e("Subjects Error", error.toString());
                    Toast.makeText(getContext(), "Failed to load subjects", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(getContext()).add(request);
    }

    void loadStudentsByClass(int classId) {
        String url = baseUrl + "teacher_marks_get_students_by_class.php?class_id=" + classId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    studentList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int studentId = obj.getInt("id");
                            String name = obj.getString("name");
                            studentList.add(new StudentGrade(studentId, name, -1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    Log.e("Students Error", error.toString());
                    Toast.makeText(getContext(), "Failed to load students", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(getContext()).add(request);
    }

    void submitGrades() {
        JSONArray gradesArray = new JSONArray();
        double maxMark;

        try {
            maxMark = Double.parseDouble(etGradeValue.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Enter a valid max mark", Toast.LENGTH_SHORT).show();
            return;
        }

        for (StudentGrade student : studentList) {
            double studentGrade = student.getGrade();
            if (studentGrade > maxMark) {
                Toast.makeText(getContext(), "Grade for " + student.getName() + " exceeds max value!", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject obj = new JSONObject();
            try {
                obj.put("student_id", student.getId());
                obj.put("subject_id", selectedSubjectId);
                obj.put("teacher_id", teacherId);
                obj.put("mark_type", selectedMarkType);
                obj.put("grade", studentGrade);
                obj.put("max_mark", maxMark);
                gradesArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("grades", gradesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                baseUrl + "teacher_submit_grades.php",
                requestBody,
                response -> Toast.makeText(getContext(), "Grades submitted!", Toast.LENGTH_SHORT).show(),
                error -> {
                    Log.e("Grades Error", error.toString());
                    Toast.makeText(getContext(), "Failed to submit grades", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(getContext()).add(request);
    }

}
