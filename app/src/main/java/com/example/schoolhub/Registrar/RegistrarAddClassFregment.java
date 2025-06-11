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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RegistrarAddClassFregment extends Fragment {

    EditText edtGrade,edtSection,edtRoom;
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
        edtRoom=view.findViewById(R.id.edtRoom);
        spnAcademicStage=view.findViewById(R.id.spnAcademicStage);
        btnAddClass=view.findViewById(R.id.btnAddClass);


        btnAddClass.setOnClickListener(e->{
            if(edtGrade.getText().toString().trim().isEmpty()||
            edtSection.getText().toString().trim().isEmpty()||
                    edtRoom.getText().toString().trim().isEmpty()){
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

    private void AddClass() {
        String url = LoginActivity.baseUrl+"Add_class.php";


        RequestQueue queue = Volley.newRequestQueue(requireContext());

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(getContext(), "Class added successfully!", Toast.LENGTH_SHORT).show();

                },
                error -> {
                    Toast.makeText(getContext(), "Coudlnt add class", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("academic_stage", spnAcademicStage.getSelectedItem().toString().trim());
                params.put("grade", edtGrade.getText().toString().trim());
                params.put("section", edtSection.getText().toString().trim());
                params.put("room",edtRoom.getText().toString().trim());
                return params;
            }
        };

        queue.add(request);
    }
}