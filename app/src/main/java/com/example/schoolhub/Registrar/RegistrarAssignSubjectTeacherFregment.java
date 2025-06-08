package com.example.schoolhub.Registrar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.InfoClass;
import com.example.schoolhub.Model.SubjectInfo;
import com.example.schoolhub.Model.TeacherInfo;
import com.example.schoolhub.R;
import com.example.schoolhub.Registration.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RegistrarAssignSubjectTeacherFregment extends Fragment {
    TextView txtSelectSubjects;
    EditText edtCode,edtSubjectName;
    boolean[] selectedClassItems;
    List<Integer> selectedClassIds = new ArrayList<>();
    Spinner spnTeacher;

    List<TeacherInfo> teacherList = new ArrayList<>();

    List<InfoClass> classList = new ArrayList<>();

    Button btnAddSubject;
    List<SubjectInfo> subjectList = new ArrayList<>();
    List<Integer> selectedSubjectIds = new ArrayList<>();
    boolean[] selectedSubjectItems;
    String[] subjectNames;
    String[] classNames;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrar_assign_subject_teacher, container, false);

        txtSelectSubjects = view.findViewById(R.id.txtSelectSubjects);
        spnTeacher=view.findViewById(R.id.spnTeacher);
        LoadUnassignedSubjects();
        LoadTeachers();
        btnAddSubject=view.findViewById(R.id.btnAddSubject);
        btnAddSubject.setOnClickListener(e->{

            AddSubject();

        });

        return view;
    }

    private void AddSubject() {
        String name = edtSubjectName.getText().toString().trim();
        String code = edtCode.getText().toString().trim();

        if (name.isEmpty() || selectedClassIds.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter subject name and select classes", Toast.LENGTH_SHORT).show();
            return;
        }

        TeacherInfo selectedTeacher = (TeacherInfo) spnTeacher.getSelectedItem();
        int teacherId = selectedTeacher.getId();


        String url = LoginActivity.baseUrl+"Registrar_Assign_Subject.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(requireContext(), "Subject added!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(requireContext(), "Error in volley : " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("teacher_id", String.valueOf(teacherId));
                params.put("subjects_ids", new JSONArray(selectedSubjectIds).toString()); // send as JSON array
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);

    }

    private void LoadUnassignedSubjects() {
        String url = LoginActivity.baseUrl + "Registrar_Get_free_subjects.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        subjectList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("name");
                            subjectList.add(new SubjectInfo(id, name));
                        }

                        selectedSubjectItems = new boolean[subjectList.size()];
                        subjectNames = new String[subjectList.size()];

                        for (int i = 0; i < subjectList.size(); i++) {
                            subjectNames[i] = subjectList.get(i).getName();
                        }

                        txtSelectSubjects.setOnClickListener(v -> showSubjectMultiDialog());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Failed to load subjects", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }
    private void showSubjectMultiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Subjects");

        builder.setMultiChoiceItems(subjectNames, selectedSubjectItems, (dialog, which, isChecked) -> {
            int subjectId = subjectList.get(which).getId();
            if (isChecked) {
                if (!selectedSubjectIds.contains(subjectId)) {
                    selectedSubjectIds.add(subjectId);
                }
            } else {
                selectedSubjectIds.remove(Integer.valueOf(subjectId));
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < subjectList.size(); i++) {
                if (selectedSubjectItems[i]) {
                    sb.append(subjectList.get(i).getName()).append(", ");
                }
            }
            if (sb.length() > 0) sb.setLength(sb.length() - 2);
            txtSelectSubjects.setText(sb.toString());
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void LoadTeachers() {
        String url = LoginActivity.baseUrl+"get_All_Teachers.php";


        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        teacherList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int id = obj.getInt("teacher_id");
                            String name = obj.getString("teacher_name");


                            teacherList.add(new TeacherInfo(id, name));
                        }

                        ArrayAdapter<TeacherInfo> adapter = new ArrayAdapter<>(
                                requireContext(), android.R.layout.simple_spinner_item, teacherList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnTeacher.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Failed to load teachers", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }
}