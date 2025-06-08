package com.example.schoolhub.Registrar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolhub.Model.InfoClass;
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


public class RegistrarAddSubjectFregment extends Fragment {
    TextView txtSelectClasses;
    EditText edtCode,edtSubjectName;
    boolean[] selectedClassItems;
    List<Integer> selectedClassIds = new ArrayList<>();

    List<TeacherInfo> teacherList = new ArrayList<>();

    List<InfoClass> classList = new ArrayList<>();

    Button btnAddSubject;

    String[] classNames;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrar_add_subject, container, false);

        txtSelectClasses = view.findViewById(R.id.txtSelectClasses);
        edtCode = view.findViewById(R.id.edtCode);
        edtSubjectName = view.findViewById(R.id.edtSubjectName);
        btnAddSubject = view.findViewById(R.id.btnAddSubject);
        LoadClasses();

        btnAddSubject.setOnClickListener(e->{

            AddSubject();

        });

        return view;
    }

    private void AddSubject() {
            String name = edtSubjectName.getText().toString().trim();
            String code = edtCode.getText().toString().trim();

        if (name.isEmpty() || code.isEmpty() || selectedClassIds.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter subject name, code, and select classes", Toast.LENGTH_SHORT).show();
            return;
        }


        String url = LoginActivity.baseUrl+"Add_subject.php";

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> Toast.makeText(requireContext(), "Subject added!", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(requireContext(), "Error in volley : " + error.getMessage(), Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("subject_name", name);
                    params.put("subject_code", code);
                    params.put("class_ids", new JSONArray(selectedClassIds).toString()); // send as JSON array
                    return params;
                }
            };

            Volley.newRequestQueue(requireContext()).add(request);

    }

    private void LoadClasses() {
        String url = LoginActivity.baseUrl+"get_classes.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        classList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("class_name");
                            classList.add(new InfoClass(id, name));
                        }

                        // Init boolean array for selections
                        selectedClassItems = new boolean[classList.size()];
                        classNames = new String[classList.size()];

                        for (int i = 0; i < classList.size(); i++) {
                            classNames[i] = classList.get(i).getName(); // make sure InfoClass has getName()
                        }

                        txtSelectClasses.setOnClickListener(v -> showClassMultiDialog());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Failed to load classes", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }
    private void showClassMultiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Classes");

        builder.setMultiChoiceItems(classNames, selectedClassItems, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedClassIds.add(classList.get(which).getId());
            } else {
                selectedClassIds.remove(Integer.valueOf(classList.get(which).getId()));
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < classList.size(); i++) {
                if (selectedClassItems[i]) {
                    sb.append(classList.get(i).getName()).append(", ");
                }
            }
            if (sb.length() > 0) sb.setLength(sb.length() - 2); // remove trailing comma
            txtSelectClasses.setText(sb.toString());
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

}