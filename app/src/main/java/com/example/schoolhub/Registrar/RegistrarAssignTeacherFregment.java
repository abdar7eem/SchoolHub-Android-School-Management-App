package com.example.schoolhub.Registrar;

import android.os.Bundle;
import android.util.Log;
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
import com.example.schoolhub.Model.ClassInfo;
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


public class RegistrarAssignTeacherFregment extends Fragment {

    EditText edtGrade,edtSection;
    Spinner spnClass,spnHomeromTeacher;
    Button btnAddClass;

    List<TeacherInfo> teacherList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrar_assign_teacher, container, false);
        spnClass = view.findViewById(R.id.spnClass); // make sure ID matches your XML

        spnHomeromTeacher=view.findViewById(R.id.spnHomeromTeacher);
        btnAddClass=view.findViewById(R.id.btnAddClass);

        LoadTeachers();
        loadClasses();
        btnAddClass.setOnClickListener(e->{
            try {
                AddClass();
            }catch(Exception ex){

            }
        });


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
        if (spnClass.getSelectedItem() == null || spnHomeromTeacher.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int classId = ((ClassInfo) spnClass.getSelectedItem()).getId();
        int teacherId = ((TeacherInfo) spnHomeromTeacher.getSelectedItem()).getId();

        String url = LoginActivity.baseUrl + "Registrar_Assign_Teacher.php";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(getContext(), "Class assigned successfully!", Toast.LENGTH_SHORT).show();
                    LoadTeachers(); // Refresh spinner
                },
                error -> {
                    Log.e("AddClassError", "Volley Error: " + error.getMessage());
                    Toast.makeText(getContext(), "Coudlnt Assign Class!!", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("class_id", String.valueOf(classId));
                params.put("homeroom_teacher_id", String.valueOf(teacherId));

                Log.d("AddClassParams", "class_id: " +String.valueOf(classId) + ", homeroom_teacher_id: " + teacherId);
                return params;
            }
        };

        queue.add(request);
    }


    private void loadClasses() {
        String url = LoginActivity.baseUrl + "Registrar_get_free_classes.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        List<ClassInfo> classList = new ArrayList<>();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("class_name");
                            Log.e("ID", String.valueOf(id));
                            Log.e("name", name);

                            classList.add(new ClassInfo(id,name));
                        }

                        ArrayAdapter<ClassInfo> adapter = new ArrayAdapter<>(
                                requireContext(), android.R.layout.simple_spinner_item, classList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnClass.setAdapter(adapter);  // <- your Spinner for class

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Failed to load classes", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }

}