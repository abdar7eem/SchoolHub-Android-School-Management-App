package com.example.schoolhub.Registrar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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


public class RegistrarAddClassFregment extends Fragment {

    EditText edtGrade,edtSection;
    Spinner spnAcademicStage,spnHomeromTeacher;
    Button btnAddClass;
    String[] stages = {"Primary", "Middle", "Secondary", "High School"};

    List<TeacherInfo> teacherList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrar_add_class, container, false);
        spnAcademicStage = view.findViewById(R.id.spnAcademicStage); // make sure ID matches your XML

        edtGrade=view.findViewById(R.id.edtGrade);
        edtSection=view.findViewById(R.id.edtSection);
        spnAcademicStage=view.findViewById(R.id.spnAcademicStage);
        spnHomeromTeacher=view.findViewById(R.id.spnHomeromTeacher);
        btnAddClass=view.findViewById(R.id.btnAddClass);

        LoadTeachers();

        btnAddClass.setOnClickListener(e->{
            if(edtGrade.getText().toString().trim().isEmpty()||
            edtSection.getText().toString().trim().isEmpty()){
                Toast.makeText(requireContext(), "Fill All fields", Toast.LENGTH_SHORT).show();
            }
            else{
                AddClass();
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                stages
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAcademicStage.setAdapter(adapter);

        return view;
    }
    private void LoadTeachers() {
        String url = LoginActivity.baseUrl+"get_available_teachers.php";

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
                        spnHomeromTeacher.setAdapter(adapter);

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
    private void AddClass() {
        String url = LoginActivity.baseUrl+"Add_class.php";


        RequestQueue queue = Volley.newRequestQueue(requireContext());

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(getContext(), "Class added successfully!", Toast.LENGTH_SHORT).show();
                    LoadTeachers(); // refresh the spinner

                },
                error -> {
                    Toast.makeText(getContext(), "Error in volley request : " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("academic_stage", spnAcademicStage.getSelectedItem().toString().trim());
                params.put("grade", edtGrade.getText().toString().trim());
                params.put("section", edtSection.getText().toString().trim());
                params.put("homeroom_teacher_name",spnHomeromTeacher.getSelectedItem().toString().trim() );
                return params;
            }
        };

        queue.add(request);
    }
}